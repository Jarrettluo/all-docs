package com.jiaruiblog.service.impl;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import com.google.common.collect.Maps;
import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.config.SystemConfig;
import com.jiaruiblog.entity.Category;
import com.jiaruiblog.entity.FileDocument;
import com.jiaruiblog.entity.Tag;
import com.jiaruiblog.entity.dto.BasePageDTO;
import com.jiaruiblog.entity.dto.DocumentDTO;
import com.jiaruiblog.entity.dto.document.UpdateInfoDTO;
import com.jiaruiblog.entity.po.FileUploadPO;
import com.jiaruiblog.entity.vo.DocWithCateVO;
import com.jiaruiblog.entity.vo.DocumentVO;
import com.jiaruiblog.entity.vo.PageVO;
import com.jiaruiblog.enums.DocStateEnum;
import com.jiaruiblog.service.*;
import com.jiaruiblog.task.exception.TaskRunException;
import com.jiaruiblog.util.BaseApiResult;
import com.jiaruiblog.util.PdfUtil;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSDownloadOptions;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.http.auth.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Field;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * @author jiarui.luo
 */
@Slf4j
@Lazy
@Service
public class FileServiceImpl implements IFileService {

    public static final String COLLECTION_NAME = "fileDatas";

    private static final String PDF_SUFFIX = ".pdf";

    private static final String FILE_NAME = "filename";

    private static final String CONTENT = "content";

    private static final String[] EXCLUDE_FIELD = new String[]{"md5", CONTENT, "contentType", "suffix", "description",
            "gridfsId", "thumbId", "textFileId", "errorMsg"};
    // 以点分割必须经过转译
    public static final String DOT = "\\.";

    @Resource
    SystemConfig systemConfig;

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private GridFsTemplate gridFsTemplate;

    @Resource
    private GridFSBucket gridFsBucket;

    @Resource
    private CategoryService categoryService;

    @Resource
    private ICommentService commentService;

    @Resource
    private CollectService collectService;

    private TagService tagService;

    @Autowired
    private void setTagService(@Lazy TagService tagService) {
        this.tagService = tagService;
    }

    private ElasticServiceImpl elasticServiceImpl;

    @Autowired
    private void setElasticServiceImpl(@Lazy ElasticServiceImpl elasticServiceImpl) {
        this.elasticServiceImpl = elasticServiceImpl;
    }

    @Resource
    private RedisService redisService;

    @Resource
    private TaskExecuteService taskExecuteService;

    @Resource
    private DocReviewService docReviewService;

    List<String> availableSuffixList = com.google.common.collect.Lists
            .newArrayList("pdf", "png", "docx", "pptx", "xlsx", "html", "md", "txt");


    /**
     * js文件流上传附件
     *
     * @param fileDocument 文档对象
     * @param inputStream  文档文件流
     * @return FileDocument
     */
    @Override
    public FileDocument saveFile(FileDocument fileDocument, InputStream inputStream) {
        //已存在该文件，则实现秒传
        FileDocument dbFile = getByMd5(fileDocument.getMd5());
        if (dbFile != null) {
            return dbFile;
        }
        //GridFSInputFile inputFile = gridFsTemplate

        String gridfsId = uploadFileToGridFs(inputStream, fileDocument.getContentType());
        fileDocument.setGridfsId(gridfsId);

        fileDocument = mongoTemplate.save(fileDocument, COLLECTION_NAME);

        return fileDocument;
    }

    @Override
    public void updateFile(FileDocument fileDocument) {
        Query query = new Query(Criteria.where("_id").is(fileDocument.getId()));
        Update update = new Update();
        update.set("textFileId", fileDocument.getTextFileId());
        update.set("thumbId", fileDocument.getThumbId());
        update.set("previewFileId", fileDocument.getPreviewFileId());
        update.set("description", fileDocument.getDescription());
        mongoTemplate.updateFirst(query, update, FileDocument.class, COLLECTION_NAME);

    }

    /**
     * @Author luojiarui
     * @Description // 更新文档状态
     * @Date 15:41 2022/11/13
     * @Param [fileDocument, state]
     **/
    @Override
    public void updateState(FileDocument fileDocument, DocStateEnum state, String errorMsg) throws TaskRunException {
        Query query = new Query(Criteria.where("_id").is(fileDocument.getId()));
        if (state != DocStateEnum.FAIL) {
            errorMsg = "无";
        }
        Update update = new Update();
        update.set("docState", state);
        update.set("errorMsg", errorMsg);
        try {
            mongoTemplate.updateFirst(query, update, FileDocument.class, COLLECTION_NAME);
        } catch (Exception e) {
            log.error("更新文档状态信息{}==>出错==>{}", fileDocument, e);
            throw new TaskRunException("更新文档状态信息==>出错==>{}", e);
        }
    }

    /**
     * @Author luojiarui
     * @Description // 从gridFs中删除文件
     * @Date 18:01 2022/11/13
     * @Param [id]
     **/
    @Override
    public void deleteGridFs(String... id) {
        List<String> ids = Arrays.asList(id);
        Query deleteQuery = new Query().addCriteria(Criteria.where(FILE_NAME).in(ids));
        gridFsTemplate.delete(deleteQuery);
    }

    /**
     * 表单上传附件
     *
     * @param md5  文件md5
     * @param file 文件
     * @return FileDocument
     */
    @Override
    public FileDocument saveFile(String md5, MultipartFile file) {
        //已存在该文件，则实现秒传
        FileDocument fileDocument = getByMd5(md5);
        if (fileDocument != null) {
            return fileDocument;
        }
        String originFilename = file.getOriginalFilename();
        fileDocument = new FileDocument();
        fileDocument.setName(originFilename);
        fileDocument.setSize(file.getSize());
        fileDocument.setContentType(file.getContentType());
        fileDocument.setUploadDate(new Date());
        fileDocument.setMd5(md5);

        if (StringUtils.hasText(originFilename)) {
            String suffix = originFilename.substring(originFilename.lastIndexOf("."));
            fileDocument.setSuffix(suffix);
        }

        try {
            String gridfsId = uploadFileToGridFs(file.getInputStream(), file.getContentType());
            fileDocument.setGridfsId(gridfsId);
            fileDocument = mongoTemplate.save(fileDocument, COLLECTION_NAME);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        // 异步保存数据标签
        tagService.saveTagWhenSaveDoc(fileDocument);

        return fileDocument;
    }

    /**
     * @return com.jiaruiblog.util.BaseApiResult
     * @Author luojiarui
     * @Description 使用用户id 和 用户名进行保存，此接口必须使用auth进行验证
     * @Date 12:18 2023/2/19
     * @Param [file, userId, username]
     **/
    @Override
    public BaseApiResult documentUpload(MultipartFile file, String userId, String username) throws AuthenticationException {
//        List<String> availableSuffixList = com.google.common.collect.Lists
//                .newArrayList("pdf", "png", "docx", "pptx", "xlsx", "html", "md", "txt");
        try {
            if (file != null && !file.isEmpty()) {
                String originFileName = file.getOriginalFilename();
                if (!StringUtils.hasText(originFileName)) {
                    return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.FORMAT_ERROR);
                }
                //获取文件后缀名
                String suffix = originFileName.substring(originFileName.lastIndexOf(".") + 1);
//                if (!availableSuffixList.contains(suffix)) {
//                    return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.FORMAT_ERROR);
//                }
                String fileMd5 = SecureUtil.md5(file.getInputStream());

                //已存在该文件，则拒绝保存
                FileDocument fileDocumentInDb = getByMd5(fileMd5);
                if (fileDocumentInDb != null) {
                    return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.DATA_DUPLICATE);
                }
                FileDocument fileDocument = saveToDb(fileMd5, file, userId, username, null);

                // 目前支持这一类数据进行预览
                // 进行全文的制作，索引，文本入库等
                if (Boolean.TRUE.equals(systemConfig.getAdminReview())) {
                    return BaseApiResult.success(fileDocument.getId());
                }

                switch (suffix) {
                    case "pdf":
                    case "docx":
                    case "pptx":
                    case "xlsx":
                    case "html":
                    case "xhtml":
                    case "xht":
                    case "htm":
                    case "md":
                    case "txt":
                    case "jpeg":
                    case "jpg":
                    case "png":
                        taskExecuteService.execute(fileDocument);
                        break;
                    default:
                        break;
                }
                return BaseApiResult.success(fileDocument.getId());
            } else {
                return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_IS_NOT_NULL);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
    }

    @Override
    public BaseApiResult uploadBatch(String category, List<String> tags, String description,
                                     Boolean skipError, MultipartFile[] files,
                                     String userId, String username) {

        FileUploadPO fileUploadPO = saveOrUpdateCategory(category, tags);
        List<String> fileIds = new ArrayList<>();
        //循环多次上传多个文件
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                try {
                    FileDocument fileDocument = saveFileNew(file, userId, username, description);
                    if (fileDocument != null) {
                        fileIds.add(fileDocument.getId());
                    }
                } catch (IOException | RuntimeException e) {
                    if (Boolean.FALSE.equals(skipError)) {
                        if (e instanceof RuntimeException) {
                            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, e.getMessage());
                        } else {
                            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
                        }
                    }
                }
            }
        }
        categoryService.addRelationShipDefault(fileUploadPO.getCategoryId(), fileIds);
        tagService.addTagRelationShip(fileUploadPO.getTagIds(), fileIds);
        return BaseApiResult.success("共计保存了" + fileIds.size() + "文档");
    }

    //证书信任
    public final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    /**
     * @return com.jiaruiblog.util.BaseApiResult
     * @Author luojiarui
     * @Description 通过网络地址将文件保存下来
     * @Date 19:09 2023/4/22
     * @Param [category, tags, name, description, urlStr, userId, username]
     **/
    @Override
    public BaseApiResult uploadByUrl(String category, List<String> tags, String name, String description,
                                     String urlStr, String userId, String username) {

        FileDocument fileDocument = null;
        try {
            if (!StringUtils.hasText(name)) {
                name = getFileName(urlStr);
            }
            URL url = new URL(urlStr);

            HttpURLConnection conn;
            //证书信任
            //关键代码
            if ("HTTPS".equals(url.getProtocol().toUpperCase())) {
                trustAllHosts();
                HttpsURLConnection https = (HttpsURLConnection) url
                        .openConnection();
                https.setHostnameVerifier(DO_NOT_VERIFY);
                conn = https;
            } else {
                conn = (HttpURLConnection) url.openConnection();
            }

//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //设置超时间为5秒
            conn.setConnectTimeout(5 * 1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            //得到输入流
            InputStream inputStream = conn.getInputStream();
            if (!StringUtils.hasText(name)) {
                return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_IS_NOT_NULL);
            }
            MultipartFile file = new MockMultipartFile(name, name, MediaType.MULTIPART_FORM_DATA_VALUE, inputStream);
            if (!file.isEmpty()) {
                fileDocument = saveFileNew(file, userId, username, description);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
        if (fileDocument == null) {
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
        FileUploadPO fileUploadPO = saveOrUpdateCategory(category, tags);
        categoryService.addRelationShipDefault(fileUploadPO.getCategoryId(), fileDocument.getId());
        List<String> fileId = new ArrayList<>();
        fileId.add(fileDocument.getId());
        tagService.addTagRelationShip(fileUploadPO.getTagIds(), fileId);
        return BaseApiResult.success(MessageConstant.SUCCESS);
    }


    public static void trustAllHosts() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {

            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {

            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }
        }};
        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection
                    .setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从src文件路径获取文件名
     *
     * @param srcRealPath src文件路径
     * @return 文件名
     */
    private static String getFileName(String srcRealPath) {
        // 如果是包含中文的src需要将其转换为标准名称
        String decoderUrl = null;
        try {
            decoderUrl = URLDecoder.decode(srcRealPath, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (org.apache.commons.lang3.StringUtils.isNoneBlank(decoderUrl)) {
            srcRealPath = decoderUrl;
        }
        return org.apache.commons.lang3.StringUtils.substringAfterLast(srcRealPath, "/");
    }

    private FileDocument saveFileNew(MultipartFile file,
                                     String userId,
                                     String username,
                                     String desc) throws IOException {
        String originFileName = file.getOriginalFilename();
        if (!StringUtils.hasText(originFileName)) {
            throw new RuntimeException(MessageConstant.FORMAT_ERROR);
        }
        //获取文件后缀名
        String suffix = originFileName.substring(originFileName.lastIndexOf(".") + 1);
        if (!availableSuffixList.contains(suffix)) {
            throw new RuntimeException(MessageConstant.FORMAT_ERROR);
        }
        String fileMd5 = SecureUtil.md5(file.getInputStream());

        //已存在该文件，则拒绝保存
        FileDocument fileDocumentInDb = getByMd5(fileMd5);
        if (fileDocumentInDb != null) {
            throw new RuntimeException(MessageConstant.DATA_DUPLICATE);
        }
        FileDocument fileDocument = saveToDb(fileMd5, file, userId, username, desc);

        // 目前支持这一类数据进行预览
        // 进行全文的制作，索引，文本入库等
        // 这里可能是空的
        if (Boolean.TRUE.equals(systemConfig.getAdminReview())) {
            return fileDocument;
        } else {
            // 如果已经关闭了管理员审核功能，则设置审核状态为关闭
            fileDocument.setReviewing(false);
        }
        switch (suffix) {
            case "pdf":
            case "docx":
            case "pptx":
            case "xlsx":
            case "html":
            case "md":
            case "txt":
                taskExecuteService.execute(fileDocument);
                break;
            default:
                break;
        }
        return fileDocument;
    }

    /**
     * @return com.jiaruiblog.entity.po.FileUploadPO
     * @Author luojiarui
     * @Description 返回需要新建或者查询的分类和标签的列表信息
     * @Date 16:09 2023/4/22
     * @Param [category, tags]
     **/
    private FileUploadPO saveOrUpdateCategory(String category, List<String> tags) {
        FileUploadPO fileUploadPO = new FileUploadPO();
        String categoryId = categoryService.saveOrUpdateCate(category);
        fileUploadPO.setCategoryId(categoryId);
        List<String> tagIdList = tagService.saveOrUpdateBatch(tags);
        fileUploadPO.setTagIds(tagIdList);
        return fileUploadPO;
    }

    /**
     * @return java.lang.String
     * @Author luojiarui
     * @Description 存入数据库及解析索引
     * @Date 12:12 2023/2/19
     * @Param [fileMd5, file]
     **/
    private FileDocument saveToDb(String md5, MultipartFile file, String userId, String username, String desc) {
        FileDocument fileDocument;
        String originFilename = file.getOriginalFilename();
        if (originFilename.contains("/")) {
            String[] split = originFilename.split("/");
            originFilename = split[split.length-1];
        }
        fileDocument = new FileDocument();
        fileDocument.setName(originFilename);
        fileDocument.setSize(file.getSize());
        fileDocument.setContentType(file.getContentType());
        fileDocument.setUploadDate(new Date());
        fileDocument.setMd5(md5);
        fileDocument.setUserId(userId);
        fileDocument.setUserName(username);
        fileDocument.setDescription(desc);
        // 如果已经关闭了管理员审核功能，则设置审核状态为关闭
        fileDocument.setReviewing(Boolean.TRUE.equals(systemConfig.getAdminReview()));

        if (StringUtils.hasText(originFilename)) {
            String suffix = originFilename.substring(originFilename.lastIndexOf("."));
            fileDocument.setSuffix(suffix);
        }

        try {
            String gridfsId = uploadFileToGridFs(file.getInputStream(), file.getContentType());
            fileDocument.setGridfsId(gridfsId);
            fileDocument = mongoTemplate.save(fileDocument, COLLECTION_NAME);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        // 异步保存数据标签
        tagService.saveTagWhenSaveDoc(fileDocument);

        return fileDocument;

    }

    /**
     * 上传文件到Mongodb的GridFs中
     *
     * @param in          -> InputStream
     * @param contentType -> String
     * @return -> String
     */
    private String uploadFileToGridFs(InputStream in, String contentType) {
        String gridfsId = IdUtil.simpleUUID();
        //文件，存储在GridFS中
        gridFsTemplate.store(in, gridfsId, contentType);
        // 其实应该使用文件id进行存储
//        ObjectId objectId = gridFsTemplate.store()
        return gridfsId;
    }

    /**
     * 上传文件到Mongodb的GridFs中
     *
     * @param in
     * @param contentType
     * @return
     */
    @Override
    public String uploadFileToGridFs(String prefix, InputStream in, String contentType) {
        String gridfsId = prefix + IdUtil.simpleUUID();
        //文件，存储在GridFS中
        gridFsTemplate.store(in, gridfsId, contentType);
        return gridfsId;
    }

    /**
     * 删除附件
     *
     * @param id           文件id
     * @param isDeleteFile 是否删除文件
     */
    @Override
    public void removeFile(String id, boolean isDeleteFile) {
        FileDocument fileDocument = mongoTemplate.findById(id, FileDocument.class, COLLECTION_NAME);
        if (fileDocument != null) {
            Query query = new Query().addCriteria(Criteria.where("_id").is(id));
            mongoTemplate.remove(query, COLLECTION_NAME);
            if (isDeleteFile) {
                Query deleteQuery = new Query().addCriteria(Criteria.where(FILE_NAME).is(fileDocument.getGridfsId()));
                gridFsTemplate.delete(deleteQuery);
            }
        }
    }

    /**
     * @Author luojiarui
     * @Description 对文档的名称，标签，分类，描述进行修改
     * @Date 09:51 2023/7/2
     * @Param [updateInfoDTO]
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    @Override
//    @Transactional  应该是不生效
    public BaseApiResult updateInfo(UpdateInfoDTO updateInfoDTO) {
        String docId = updateInfoDTO.getId();
        Query query = new Query().addCriteria(Criteria.where("_id").is(docId));
        FileDocument document = mongoTemplate.findById(docId, FileDocument.class, COLLECTION_NAME);
        if (Objects.isNull(document)) {
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
        // 清除全部的标签和分类信息
        // 删除分类关系
        categoryService.removeRelateByDocId(docId);
        // 删除标签
        tagService.removeRelateByDocId(docId);

        // 保存文档和分类/标签的关系
        List<String> fileIds = com.google.common.collect.Lists.newArrayList(docId);
        String categoryId = updateInfoDTO.getCategoryId();
        List<String> tags = updateInfoDTO.getTags();
        FileUploadPO fileUploadPO = saveOrUpdateCategory(null, tags);

        if (Objects.nonNull(categoryId)) {
            categoryService.addRelationShipDefault(categoryId, fileIds);
        }
        tagService.addTagRelationShip(fileUploadPO.getTagIds(), fileIds);

        // 更新文档的名称和描述信息
        String name = updateInfoDTO.getName();
        String desc = updateInfoDTO.getDesc();

        String originName = document.getName();
        String[] split = originName.split(DOT);
        if (split.length > 1) {
            String suffix = split[split.length - 1];
            name = name + "." + suffix;
        }
        Update update = new Update();
        update.set("name", name);
        update.set("description", desc);
        mongoTemplate.updateFirst(query, update, FileDocument.class, COLLECTION_NAME);

        return BaseApiResult.success(MessageConstant.SUCCESS);
    }

    /**
     * 查询附件
     *
     * @param id 文件id
     * @return
     */
    @Override
    public Optional<FileDocument> getById(String id) {
        FileDocument fileDocument = mongoTemplate.findById(id, FileDocument.class, COLLECTION_NAME);
        if (fileDocument != null) {
            Query gridQuery = new Query().addCriteria(Criteria.where(FILE_NAME).is(fileDocument.getGridfsId()));
            GridFSFile fsFile = gridFsTemplate.findOne(gridQuery);

            if (fsFile == null || fsFile.getObjectId() == null) {
                return Optional.empty();
            }

            // 开启文件下载
            GridFSDownloadOptions gridFSDownloadOptions = new GridFSDownloadOptions();

            try (GridFSDownloadStream in = gridFsBucket.openDownloadStream(fsFile.getObjectId())) {
                if (in.getGridFSFile().getLength() > 0) {
                    GridFsResource resource = new GridFsResource(fsFile, in);
                    fileDocument.setContent(IoUtil.readBytes(resource.getInputStream()));
                    return Optional.of(fileDocument);
                } else {
                    return Optional.empty();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return Optional.empty();
    }

    /**
     * 查询附件
     *
     * @param id 文件id
     * @return
     */
    @Override
    public Optional<FileDocument> getPreviewById(String id) {
        FileDocument fileDocument = new FileDocument();
        if (fileDocument != null) {
            Query gridQuery = new Query().addCriteria(Criteria.where(FILE_NAME).is(id));
            GridFSFile fsFile = gridFsTemplate.findOne(gridQuery);

            if (fsFile == null) {
                return Optional.empty();
            }

            fileDocument.setSize(fsFile.getLength());
            fileDocument.setName(fsFile.getFilename());

            if (fsFile != null) {
                fsFile.getObjectId();
            }

            // 开启文件下载
            GridFSDownloadOptions gridFSDownloadOptions = new GridFSDownloadOptions();

            try (GridFSDownloadStream in = gridFsBucket.openDownloadStream(fsFile.getObjectId())) {
                if (in.getGridFSFile().getLength() > 0) {
                    GridFsResource resource = new GridFsResource(fsFile, in);
                    fileDocument.setContent(IoUtil.readBytes(resource.getInputStream()));
                    return Optional.of(fileDocument);
                } else {
                    return Optional.empty();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return Optional.empty();
    }

    /**
     * 根据md5获取文件对象
     *
     * @param md5 String
     * @return -> FileDocument
     */
    @Override
    public FileDocument getByMd5(String md5) {
        if (md5 == null) {
            return null;
        }
        Query query = new Query().addCriteria(Criteria.where("md5").is(md5));
        return mongoTemplate.findOne(query, FileDocument.class, COLLECTION_NAME);
    }

    /**
     * 根据md5获取文件对象
     *
     * @param md5Set String
     * @return -> FileDocument
     */
    @Override
    public List<FileDocument> getByMd5Set(Set<String> md5Set) {
        if (Objects.isNull(md5Set)) {
            return null;
        }
        Query query = new Query().addCriteria(Criteria.where("md5").in(md5Set));
        return mongoTemplate.find(query, FileDocument.class, COLLECTION_NAME);
    }

    @Override
    public List<FileDocument> listFilesByPage(int pageIndex, int pageSize) {
        Query query = new Query().with(Sort.by(Sort.Direction.DESC, "uploadDate"))
                .addCriteria(Criteria.where("reviewing").is(false));
        long skip = (long) (pageIndex) * pageSize;
        query.skip(skip);
        query.limit(pageSize);
        Field field = query.fields();
        field.exclude(CONTENT);
        return mongoTemplate.find(query, FileDocument.class, COLLECTION_NAME);
    }

    /**
     * @return java.util.List<com.jiaruiblog.entity.FileDocument>
     * @Author luojiarui
     * @Description // 增加过滤条件的分页功能
     * @Date 11:12 下午 2022/6/22
     * @Param [pageIndex, pageSize, ids]
     **/
    @Override
    public List<FileDocument> listAndFilterByPage(int pageIndex, int pageSize, Collection<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Lists.newArrayList();
        }
        Query query = new Query().with(Sort.by(Sort.Direction.DESC, "uploadDate"));
        // 增加过滤条件
        query.addCriteria(Criteria.where("_id").in(ids)).addCriteria(Criteria.where("reviewing").is(false));
        // 设置起始页和每页查询条数
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        query.with(pageable);


        Field field = query.fields();
        field.exclude(CONTENT);
        return mongoTemplate.find(query, FileDocument.class, COLLECTION_NAME);
    }

    @Override
    public List<FileDocument> listAndFilterByPageNotSort(int pageIndex, int pageSize, List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Lists.newArrayList();
        }
        Query query = new Query();
        query.with(Sort.unsorted());
        // 增加过滤条件
        query.addCriteria(Criteria.where("_id").in(ids));
        // 设置起始页和每页查询条数
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        query.with(pageable);


        Field field = query.fields();
        field.exclude(CONTENT);
        return mongoTemplate.find(query, FileDocument.class, COLLECTION_NAME);
    }

    /**
     * @return com.jiaruiblog.utils.ApiResult
     * @Author luojiarui
     * @Description 列表；过滤；检索等
     * @Date 11:49 2022/8/6
     * @Param [documentDTO]
     **/
    @Override
    public BaseApiResult list(DocumentDTO documentDTO) {
        List<DocumentVO> documentVos;
        List<FileDocument> fileDocuments = Lists.newArrayList();

        long totalNum = 0L;

        switch (documentDTO.getType()) {
            case ALL:
                fileDocuments = listFilesByPage(documentDTO.getPage(), documentDTO.getRows());
                totalNum = countAllFile();
                break;
            case TAG:
                long startTime = System.currentTimeMillis();
                Tag tag = tagService.queryByTagId(documentDTO.getTagId());
                if (tag == null) {
                    break;
                }
                List<String> fileIdList1 = tagService.queryDocIdListByTagId(tag.getId());
                fileDocuments = listAndFilterByPage(documentDTO.getPage(), documentDTO.getRows(), fileIdList1);
                if (CollectionUtils.isEmpty(fileIdList1)) {
                    break;
                }
                Query query = new Query().addCriteria(Criteria.where("_id").in(fileIdList1));
                totalNum = countFileByQuery(query);
                long endTime1 = System.currentTimeMillis();
                // 异步执行清理无效的标签关系
                tagService.clearInvalidTagRelationship(documentDTO.getTagId());
                long endTime2 = System.currentTimeMillis();
                log.info("查询数据花费时间" + (endTime1 - startTime));
                log.info("清理异常数据" + (endTime2 - startTime));
                break;
            case FILTER:
                Set<String> docIdSet = new HashSet<>();
                String keyWord = Optional.of(documentDTO).map(DocumentDTO::getFilterWord).orElse("");
                // 模糊查询 分类
                docIdSet.addAll(categoryService.fuzzySearchDoc(keyWord));
                // 模糊查询 标签
                docIdSet.addAll(tagService.fuzzySearchDoc(keyWord));
                // 模糊查询 文件标题
                docIdSet.addAll(fuzzySearchDoc(keyWord));
                // 模糊查询 评论内容
                docIdSet.addAll(commentService.fuzzySearchDoc(keyWord));
                List<FileDocument> esDoc = null;
                try {
                    esDoc = elasticServiceImpl.search(keyWord);
                    if (!CollectionUtils.isEmpty(esDoc)) {
                        Set<String> existIds = esDoc.stream().map(FileDocument::getId).collect(Collectors.toSet());
                        docIdSet.removeAll(existIds);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                fileDocuments = listAndFilterByPage(documentDTO.getPage(), documentDTO.getRows(), docIdSet);
                if (esDoc != null) {
                    fileDocuments = Optional.ofNullable(fileDocuments).orElse(new ArrayList<>());
                    fileDocuments.addAll(esDoc);
                    totalNum = fileDocuments.size();
                }
                break;
            case CATEGORY:
                Category category = categoryService.queryById(documentDTO.getCategoryId());
                if (category == null) {
                    break;
                }
                List<String> fileIdList = categoryService.queryDocListByCategory(category);
                fileDocuments = listAndFilterByPage(documentDTO.getPage(), documentDTO.getRows(), fileIdList);
                if (CollectionUtils.isEmpty(fileIdList)) {
                    break;
                }
                Query query1 = new Query().addCriteria(Criteria.where("_id").in(fileIdList));
                totalNum = countFileByQuery(query1);
                break;
            default:
                return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_IS_NOT_NULL);
        }
        documentVos = convertDocuments(fileDocuments);
        Map<String, Object> result = new HashMap<>(8);
        result.put("totalNum", totalNum);
        result.put("documents", documentVos);
        return BaseApiResult.success(result);
    }

    /**
     * 过滤的时候限制分类，只能在某个分类下进行检索
     * @return com.jiaruiblog.utils.ApiResult
     * @Author luojiarui
     * @Description 列表；过滤；检索等
     * @Date 11:49 2022/8/6
     * @Param [documentDTO]
     **/
    @Override
    public BaseApiResult listNew(DocumentDTO documentDTO) {
        List<DocumentVO> documentVos;
        List<FileDocument> fileDocuments = Lists.newArrayList();

        long totalNum = 0L;

        switch (documentDTO.getType()) {
            case ALL:
                fileDocuments = listFilesByPage(documentDTO.getPage(), documentDTO.getRows());
                totalNum = countAllFile();
                break;
            case TAG:
                Tag tag = tagService.queryByTagId(documentDTO.getTagId());
                if (tag == null) {
                    break;
                }
                List<String> fileIdList1 = tagService.queryDocIdListByTagId(tag.getId());
                fileDocuments = listAndFilterByPage(documentDTO.getPage(), documentDTO.getRows(), fileIdList1);
                if (CollectionUtils.isEmpty(fileIdList1)) {
                    break;
                }
                Query query = new Query().addCriteria(Criteria.where("_id").in(fileIdList1));
                totalNum = countFileByQuery(query);
                break;
            case FILTER:
                Set<String> docIdSet = new HashSet<>();
                String keyWord = Optional.of(documentDTO).map(DocumentDTO::getFilterWord).orElse("");
                // 模糊查询 分类
                docIdSet.addAll(categoryService.fuzzySearchDoc(keyWord));
                // 模糊查询 标签
                docIdSet.addAll(tagService.fuzzySearchDoc(keyWord));
                // 模糊查询 文件标题
                docIdSet.addAll(fuzzySearchDoc(keyWord));
                // 模糊查询 评论内容
                docIdSet.addAll(commentService.fuzzySearchDoc(keyWord));

                List<DocumentVO> esDocVO = Lists.newArrayList();
                // 用户进行检索的分类id
                String categoryId = documentDTO.getCategoryId();

                List<String> targetFileIdList = new ArrayList<>();
                if (org.apache.commons.lang3.StringUtils.isNoneBlank(categoryId)) {
                    Category category = new Category();
                    category.setId(categoryId);
                    // 得到这个分类下的文档的md5信息，这里的数据容易爆掉
                    targetFileIdList = categoryService.queryDocListByCategory(category);
                }
                try {
                    Map<String, List<PageVO>> search = elasticServiceImpl.search(keyWord, new HashSet<>());
                    Set<String> md5Set = search.keySet();
                    List<FileDocument> esDoc  = getByMd5Set(md5Set);
                    if (!CollectionUtils.isEmpty(esDoc)) {
                        List<String> finalTargetFileIdList = targetFileIdList;
                        if (!finalTargetFileIdList.isEmpty()) {
                            esDoc = esDoc.stream().filter(item -> finalTargetFileIdList.contains(item.getId()))
                                    .collect(Collectors.toList());
                        }
                        Set<String> existIds = esDoc.stream().map(FileDocument::getId).collect(Collectors.toSet());
                        docIdSet.removeAll(existIds);
                        for (FileDocument fileDocument : esDoc) {
                            DocumentVO documentVO = new DocumentVO();
                            String md5 = fileDocument.getMd5();
                            List<PageVO> pageVOList = search.get(md5);
                            documentVO.setPageVOList(pageVOList);
                            DocumentVO documentVO1 = convertDocumentNew(documentVO, fileDocument);
                            esDocVO.add(documentVO1);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!targetFileIdList.isEmpty()) {
                    Iterator<String> iterator = docIdSet.iterator();
                    while (iterator.hasNext()) {
                        if (!targetFileIdList.contains(iterator.next())) {
                            iterator.remove();
                        }
                    }
                }

                fileDocuments = listAndFilterByPage(documentDTO.getPage(), documentDTO.getRows(), docIdSet);
                fileDocuments = Optional.ofNullable(fileDocuments).orElse(new ArrayList<>());
                for (FileDocument fileDocument : fileDocuments) {
                    DocumentVO documentVO = new DocumentVO();
                    documentVO.setPageVOList(new ArrayList<>());
                    DocumentVO documentVO2 = convertDocumentNew(documentVO, fileDocument);
                    esDocVO.add(documentVO2);
                }
                esDocVO.sort(Comparator.comparingInt((DocumentVO obj) ->obj.getPageVOList().size()).reversed());
                Map<String, Object> result = new HashMap<>(16);
                result.put("totalNum", esDocVO.size());
                result.put("documents", esDocVO);
                return BaseApiResult.success(result);
            case CATEGORY:
                Category category = categoryService.queryById(documentDTO.getCategoryId());
                if (category == null) {
                    break;
                }
                List<String> fileIdList = categoryService.queryDocListByCategory(category);
                fileDocuments = listAndFilterByPage(documentDTO.getPage(), documentDTO.getRows(), fileIdList);
                if (CollectionUtils.isEmpty(fileIdList)) {
                    break;
                }
                Query query1 = new Query().addCriteria(Criteria.where("_id").in(fileIdList));
                totalNum = countFileByQuery(query1);
                break;
            default:
                return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_IS_NOT_NULL);
        }
        documentVos = convertDocuments(fileDocuments);
        Map<String, Object> result = new HashMap<>(8);
        result.put("totalNum", totalNum);
        result.put("documents", documentVos);
        return BaseApiResult.success(result);
    }

    /**
     * @return com.jiaruiblog.utils.ApiResult
     * @Author luojiarui
     * @Description // 查询文档的详细信息
     * @Date 9:27 下午 2022/6/23
     * @Param [id]
     **/
    @Override
    public BaseApiResult detail(String id) {
        FileDocument fileDocument = queryById(id);
        if (fileDocument == null) {
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.PARAMS_LENGTH_REQUIRED);
        } else {
            try {
                redisService.incrementScoreByUserId(id, RedisServiceImpl.DOC_KEY);
            } catch (RedisConnectionFailureException e) {
                log.error("连接redis失败，暂时无法写入数据库", e);
            }
        }
        // 查询评论信息，查询分类信息，查询分类关系，查询标签信息，查询标签关系信息
        return BaseApiResult.success(convertDocument(null, fileDocument));
    }

    @Override
    public BaseApiResult remove(FileDocument fileDocument) {
        String id = fileDocument.getId();
        if (!isExist(id)) {
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
        // 删除评论信息，删除分类关系，删除标签关系
        // 删除dfs的文件；删除es的索引；删除审核的消息
        removeFile(id, true);

        // 删除评论信息
        commentService.removeByDocId(id);
        // 删除分类关系
        categoryService.removeRelateByDocId(id);
        // 删除收藏关系
        collectService.removeRelateByDocId(id);
        // 删除标签
        tagService.removeRelateByDocId(id);

        // 删除文档评审关系
        docReviewService.removeReviews(Collections.singletonList(id));
        // 删除文档的索引内容
        elasticServiceImpl.removeByDocId(fileDocument.getMd5());

        // 删除redis中对于文档的统计信息
        redisService.removeByDocId(id);

        return BaseApiResult.success(MessageConstant.SUCCESS);
    }

    /**
     * @return com.jiaruiblog.util.BaseApiResult
     * @Author luojiarui
     * @Description 查询不同分类条件的文档列表
     * @Date 22:44 2022/11/15
     * @Param [documentDTO]
     **/
    @Override
    public BaseApiResult listWithCategory(DocumentDTO documentDTO) {
        String restrictId;
        String filterWord = documentDTO.getFilterWord();
        int page = documentDTO.getPage();
        int row = documentDTO.getRows();
        List<FileDocument> fileDocuments = fuzzySearchDocWithPage(filterWord, page, row);
        long totalNum = countNumByKeyWord(filterWord);

        List<DocWithCateVO> documentVos = Lists.newArrayList();
        switch (documentDTO.getType()) {
            case CATEGORY:
                restrictId = documentDTO.getCategoryId();
                for (FileDocument fileDocument : fileDocuments) {
                    boolean relateExist = categoryService.relateExist(restrictId, fileDocument.getId());
                    documentVos.add(entityTransfer(relateExist, fileDocument));
                }
                break;
            case TAG:
                restrictId = documentDTO.getTagId();
                for (FileDocument fileDocument : fileDocuments) {
                    boolean relateExist = tagService.relateExist(restrictId, fileDocument.getId());
                    documentVos.add(entityTransfer(relateExist, fileDocument));
                }
                break;
            default:
                break;
        }
        Map<String, Object> result = new HashMap<>(8);
        result.put("totalNum", totalNum);
        result.put("documents", documentVos);
        return BaseApiResult.success(result);
    }

    /**
     * 根据搜索条件进行模糊查询
     *
     * @param keyWord 关键字
     * @return -> List<FileDocument>
     * @since 2022年11月16日
     */
    private List<FileDocument> fuzzySearchDocWithPage(String keyWord, int page, int row) {
        Query query = new Query();
        if (StringUtils.hasText(keyWord)) {
            Pattern pattern = Pattern.compile("^.*" + keyWord + ".*$", Pattern.CASE_INSENSITIVE);
            query.addCriteria(Criteria.where("name").regex(pattern));
        }
        // 不包含该字段
        query.fields().exclude(EXCLUDE_FIELD);

        // 设置起始页和每页查询条数
        Pageable pageable = PageRequest.of(page, row);
        query.with(pageable);
        query.with(Sort.by(Sort.Direction.DESC, "uploadDate"));
        return mongoTemplate.find(query, FileDocument.class, COLLECTION_NAME);
    }

    /**
     * @return long
     * @Author luojiarui
     * @Description 符合关键字的总数查询
     * @Date 21:55 2022/11/17
     * @Param [keyWord]
     **/
    private long countNumByKeyWord(String keyWord) {
        if (StringUtils.hasText(keyWord)) {
            Query query = new Query();
            Pattern pattern = Pattern.compile("^.*" + keyWord + ".*$", Pattern.CASE_INSENSITIVE);
            query.addCriteria(Criteria.where("name").regex(pattern));
            return mongoTemplate.count(query, COLLECTION_NAME);
        } else {
            return countAllFile();
        }
    }

    private DocWithCateVO entityTransfer(boolean checkState, FileDocument fileDocument) {
        DocWithCateVO doc = new DocWithCateVO();
        String docId = fileDocument.getId();
        doc.setId(docId);
        doc.setCategoryVO(categoryService.queryByDocId(docId));
        doc.setSize(fileDocument.getSize());
        doc.setCreateTime(fileDocument.getUploadDate());
        doc.setTitle(fileDocument.getName());
        doc.setTagVOList(tagService.queryByDocId(docId));
        doc.setUserName("admin");
        doc.setChecked(checkState);
        return doc;
    }

    /**
     * @return java.util.List<com.jiaruiblog.entity.vo.DocumentVO>
     * @Author luojiarui
     * @Description convertDocuments
     * @Date 10:16 下午 2022/6/21
     * @Param fileDocuments
     **/
    private List<DocumentVO> convertDocuments(List<FileDocument> fileDocuments) {
        if (fileDocuments == null) {
            return Lists.newArrayList();
        }
        List<DocumentVO> documentVos = Lists.newArrayList();
        for (FileDocument fileDocument : fileDocuments) {
            DocumentVO documentVO = new DocumentVO();
            documentVO = convertDocument(documentVO, fileDocument);
            documentVos.add(documentVO);
        }
        return documentVos;
    }

    /**
     * @return com.jiaruiblog.entity.vo.DocumentVO
     * @Author luojiarui
     * @Description convertDocument
     * @Date 10:24 下午 2022/6/21
     * @Param [documentVO, fileDocument]
     **/
    public DocumentVO convertDocument(DocumentVO documentVO, FileDocument fileDocument) {
        documentVO = Optional.ofNullable(documentVO).orElse(new DocumentVO());
        if (fileDocument == null) {
            return documentVO;
        }
        String username = fileDocument.getUserName();
        if (!StringUtils.hasText(username)) {
            username = "未知用户";
        }
        documentVO.setId(fileDocument.getId());
        documentVO.setSize(fileDocument.getSize());
        documentVO.setTitle(fileDocument.getName());
        documentVO.setDescription(fileDocument.getDescription());
        documentVO.setUserName(username);
        documentVO.setCreateTime(fileDocument.getUploadDate());
        documentVO.setThumbId(fileDocument.getThumbId());
        // 根据文档的id进行查询 评论， 收藏，分类， 标签
        String docId = fileDocument.getId();
        documentVO.setCommentNum(commentService.commentNum(docId));
        documentVO.setCollectNum(collectService.collectNum(docId));
        documentVO.setCategoryVO(categoryService.queryByDocId(docId));
        documentVO.setTagVOList(tagService.queryByDocId(docId));
        // 查询文档的信息:新增文档地址，文档错误信息，文本id
        documentVO.setDocState(fileDocument.getDocState());
        documentVO.setErrorMsg(fileDocument.getErrorMsg());
        documentVO.setTxtId(fileDocument.getTextFileId());
        documentVO.setPreviewFileId(fileDocument.getPreviewFileId());

        return documentVO;
    }

    /**
     * @return com.jiaruiblog.entity.vo.DocumentVO
     * @Author luojiarui
     * @Description convertDocument
     * @Date 10:24 下午 2022/6/21
     * @Param [documentVO, fileDocument]
     **/
    public DocumentVO convertDocumentNew(DocumentVO documentVO, FileDocument fileDocument) {
        documentVO = Optional.ofNullable(documentVO).orElse(new DocumentVO());
        if (fileDocument == null) {
            return documentVO;
        }
        String username = fileDocument.getUserName();
        if (!StringUtils.hasText(username)) {
            username = "未知用户";
        }
        documentVO.setId(fileDocument.getId());
        documentVO.setSize(fileDocument.getSize());
        documentVO.setTitle(fileDocument.getName());
        documentVO.setDescription(fileDocument.getDescription());
        documentVO.setUserName(username);
        documentVO.setCreateTime(fileDocument.getUploadDate());
        documentVO.setThumbId(fileDocument.getThumbId());
        // 根据文档的id进行查询 评论， 收藏，分类， 标签
        // 略
        // 查询文档的信息:新增文档地址，文档错误信息，文本id
        documentVO.setDocState(fileDocument.getDocState());
        documentVO.setErrorMsg(fileDocument.getErrorMsg());
        documentVO.setTxtId(fileDocument.getTextFileId());
        documentVO.setPreviewFileId(fileDocument.getPreviewFileId());

        return documentVO;
    }

    /**
     * 模糊搜索
     *
     * @param keyWord 关键字
     * @return -> List<String>
     */
    public List<String> fuzzySearchDoc(String keyWord) {
        if (!StringUtils.hasText(keyWord)) {
            return Lists.newArrayList();
        }
        Pattern pattern = Pattern.compile("^.*" + keyWord + ".*$", Pattern.CASE_INSENSITIVE);
        Query query = new Query();
        query.addCriteria(Criteria.where("name").regex(pattern));

        List<FileDocument> documents = mongoTemplate.find(query, FileDocument.class, COLLECTION_NAME);
        return documents.stream().map(FileDocument::getId).collect(Collectors.toList());
    }


    /**
     * 根据用户的主键id查询用户信息
     * //Pattern pattern=Pattern.compile("^.*"+pattern_name+".*$", Pattern.CASE_INSENSITIVE);
     * //query.addCriteria(Criteria.where("name").regex(pattern))；
     *
     * @param docId 文档id
     * @return boolean
     */
    @Override
    public boolean isExist(String docId) {
        if (!StringUtils.hasText(docId)) {
            return false;
        }
        FileDocument fileDocument = queryById(docId);
        return fileDocument != null;
    }

    /**
     * 检索已经存在的user
     *
     * @param docId String
     * @return FileDocument
     */
    @Override
    public FileDocument queryById(String docId) {
        return mongoTemplate.findById(docId, FileDocument.class, COLLECTION_NAME);
    }

    /**
     * @return java.lang.Integer
     * @Author luojiarui
     * @Description // 统计总数
     * @Date 4:40 下午 2022/6/26
     * @Param []
     **/
    @Override
    public long countAllFile() {
        return mongoTemplate.getCollection(COLLECTION_NAME).estimatedDocumentCount();
    }

    /**
     * @Author luojiarui
     * @Description //转换pdf文档的图片，然后保存
     * @Date 7:49 下午 2022/7/24
     * @Param [inputStream, fileDocument]
     **/
    @Async
    @Override
    public void updateFileThumb(InputStream inputStream, FileDocument fileDocument) throws FileNotFoundException {
        // 新建pdf文件的路径
        String path = "thumbnail";
        String picPath = path + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + ".png";
        String gridfsId = IdUtil.simpleUUID();
        if (PDF_SUFFIX.equals(fileDocument.getSuffix())) {
            // 将pdf输入流转换为图片并临时保存下来
            PdfUtil.pdfThumbnail(inputStream, picPath);

            if (new File(picPath).exists()) {
                String contentType = "image/png";
                FileInputStream in = new FileInputStream(picPath);
                //文件，存储在GridFS
                gridFsTemplate.store(in, gridfsId, contentType);
                try {
                    Files.delete(Paths.get(picPath));
                } catch (IOException e) {
                    log.error("删除文件路径{} ==> 失败信息{}", picPath, e);
                }
            }
        }

        Query query = new Query().addCriteria(Criteria.where("_id").is(fileDocument.getId()));
        Update update = new Update().set("thumbId", gridfsId);
        mongoTemplate.updateFirst(query, update, FileDocument.class, COLLECTION_NAME);

    }

    /**
     * @return java.io.InputStream
     * @Author luojiarui
     * @Description //根据缩略图id返回图片信息
     * @Date 7:59 下午 2022/7/24
     * @Param [thumbId]
     **/
    @Override
    public InputStream getFileThumb(String thumbId) {
        if (StringUtils.hasText(thumbId)) {
            Query gridQuery = new Query().addCriteria(Criteria.where(FILE_NAME).is(thumbId));
//            Query gridQuery = new Query().addCriteria(Criteria.where("_id").is(thumbId));
            GridFSFile fsFile = gridFsTemplate.findOne(gridQuery);
            if (fsFile == null) {
                return null;
            }
            try (GridFSDownloadStream in = gridFsBucket.openDownloadStream(fsFile.getObjectId())) {
                if (in.getGridFSFile().getLength() > 0) {
                    GridFsResource resource = new GridFsResource(fsFile, in);
                    return resource.getInputStream();
                } else {
                    return null;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public byte[] getFileBytes(String thumbId) {

        if (StringUtils.hasText(thumbId)) {
            Query gridQuery = new Query().addCriteria(Criteria.where(FILE_NAME).is(thumbId));
            GridFSFile fsFile = gridFsTemplate.findOne(gridQuery);
            if (fsFile == null) {
                return new byte[0];
            }
            try (GridFSDownloadStream in = gridFsBucket.openDownloadStream(fsFile.getObjectId())) {
                if (in.getGridFSFile().getLength() > 0) {
                    GridFsResource resource = new GridFsResource(fsFile, in);
                    return IoUtil.readBytes(resource.getInputStream());
                } else {
                    return new byte[0];
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return new byte[0];
    }

    /**
     * @return long
     * @Author luojiarui
     * @Description 根据查询条件查询总数量
     * @Date 12:09 2022/8/6
     * @Param [query]
     **/
    public long countFileByQuery(Query query) {
        return mongoTemplate.count(query, FileDocument.class, COLLECTION_NAME);
    }

    /**
     * @Author luojiarui
     * @Description // 根据文档idid查询文档
     * @Date 22:28 2024/7/21
     * @Param [docId]
     * @return java.util.List<com.jiaruiblog.entity.FileDocument>
     **/
    @Override
    public List<FileDocument> queryByDocIds(String ...docId) {
        List<String> docIds = Arrays.asList(docId);
        if (CollectionUtils.isEmpty(docIds)) {
            return Collections.emptyList();
        }
        Query query = new Query(Criteria.where("_id").in(docIds));
        return mongoTemplate.find(query, FileDocument.class, COLLECTION_NAME);
    }

    /**
     * @return com.jiaruiblog.entity.FileDocument
     * @Author luojiarui
     * @Description 查询并删除文档
     * @Date 10:07 2022/12/10
     * @Param [docId]
     **/
    @Override
    public List<FileDocument> queryAndRemove(String... docId) {
        List<String> docIds = Arrays.asList(docId);
        if (CollectionUtils.isEmpty(docIds)) {
            return Collections.emptyList();
        }
        Query query = new Query(Criteria.where("_id").in(docIds));
        return mongoTemplate.findAllAndRemove(query, FileDocument.class, COLLECTION_NAME);
    }

    /**
     * @return java.util.List<com.jiaruiblog.entity.FileDocument>
     * @Author luojiarui
     * @Description 修改并返回查询到的文档信息
     * @Date 10:31 2022/12/10
     * @Param [docId]
     **/
    @Override
    public List<FileDocument> queryAndUpdate(String... docId) {
        List<String> docIds = Arrays.asList(docId);
        if (CollectionUtils.isEmpty(docIds)) {
            return Collections.emptyList();
        }
        Query query = new Query(Criteria.where("_id").in(docIds));
        Update update = new Update();
        update.set("reviewing", false);
        mongoTemplate.updateMulti(query, update, COLLECTION_NAME);
        return mongoTemplate.find(query, FileDocument.class, COLLECTION_NAME);
    }

    @Override
    public List<FileDocument> queryFileDocument(BasePageDTO pageDTO, boolean reviewing) {

        Query query = new Query().with(Sort.by(Sort.Direction.DESC, "uploadDate"));
        query.addCriteria(Criteria.where("reviewing").is(reviewing));
        int pageIndex = pageDTO.getPage();
        int pageSize = pageDTO.getRows();
        long skip = (long) (pageIndex - 1) * pageSize;
        query.skip(skip);
        query.limit(pageSize);
        Field field = query.fields();
        field.exclude(CONTENT);

        return mongoTemplate.find(query, FileDocument.class, COLLECTION_NAME);
    }

    @Override
    public BaseApiResult queryFileDocumentResult(BasePageDTO pageDTO, boolean reviewing) {
        Query query = new Query().with(Sort.by(Sort.Direction.DESC, "uploadDate"));
        query.addCriteria(Criteria.where("reviewing").is(reviewing));
        Map<String, Object> result = Maps.newHashMap();
        result.put("data", queryFileDocument(pageDTO, reviewing));
        result.put("total", mongoTemplate.count(query, FileDocument.class, COLLECTION_NAME));
        return BaseApiResult.success(result);
    }
}