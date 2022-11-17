package com.jiaruiblog.service.impl;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.entity.Category;
import com.jiaruiblog.entity.FileDocument;
import com.jiaruiblog.entity.Tag;
import com.jiaruiblog.entity.dto.DocumentDTO;
import com.jiaruiblog.entity.vo.DocWithCateVO;
import com.jiaruiblog.entity.vo.DocumentVO;
import com.jiaruiblog.enums.DocStateEnum;
import com.jiaruiblog.service.IFileService;
import com.jiaruiblog.service.RedisService;
import com.jiaruiblog.task.exception.TaskRunException;
import com.jiaruiblog.util.BaseApiResult;
import com.jiaruiblog.util.PdfUtil;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSDownloadOptions;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
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
@Service
public class FileServiceImpl implements IFileService {

    private static final String COLLECTION_NAME = "fileDatas";

    private static final String PDF_SUFFIX = ".pdf";

    private static final String FILE_NAME = "filename";

    private static final String CONTENT = "content";

    private static final String[] EXCLUDE_FIELD = new String[]{"md5", "content", "contentType", "suffix", "description",
            "gridfsId", "thumbId", "textFileId", "errorMsg"};

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFsBucket;

    @Autowired
    private CategoryServiceImpl categoryServiceImpl;

    @Autowired
    private CommentServiceImpl commentServiceImpl;

    @Autowired
    private CollectServiceImpl collectServiceImpl;

    @Autowired
    private TagServiceImpl tagServiceImpl;

    @Autowired
    private ElasticServiceImpl elasticServiceImpl;

    @Autowired
    private RedisService redisService;


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

        // TODO 在这里进行异步操作

        return fileDocument;
    }

    @Override
    public void updateFile(FileDocument fileDocument) {
        Query query = new Query(Criteria.where("_id").is(fileDocument.getId()));
        Update update = new Update();
        update.set("textFileId", fileDocument.getTextFileId());
        update.set("thumbId", fileDocument.getThumbId());
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
        Query deleteQuery = new Query().addCriteria(Criteria.where(FILE_NAME).in(id));
        gridFsTemplate.delete(deleteQuery);
    }

    /**
     * 表单上传附件
     *
     * @param md5 文件md5
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
        tagServiceImpl.saveTagWhenSaveDoc(fileDocument);

        return fileDocument;
    }

    /**
     * 上传文件到Mongodb的GridFs中
     *
     * @param in
     * @param contentType
     * @return
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

    @Override
    public List<FileDocument> listFilesByPage(int pageIndex, int pageSize) {
        Query query = new Query().with(Sort.by(Sort.Direction.DESC, "uploadDate"));
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
        query.addCriteria(Criteria.where("_id").in(ids));
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
        log.info(">>>>>>>检索文档>>>>>>检索参数{}", documentDTO.toString());
        List<DocumentVO> documentVos;
        List<FileDocument> fileDocuments = Lists.newArrayList();

        long totalNum = 0L;

        switch (documentDTO.getType()) {
            case ALL:
                fileDocuments = listFilesByPage(documentDTO.getPage(), documentDTO.getRows());
                totalNum = countAllFile();
                break;
            case TAG:
                Tag tag = tagServiceImpl.queryByTagId(documentDTO.getTagId());
                if (tag == null) {
                    break;
                }
                List<String> fileIdList1 = tagServiceImpl.queryDocIdListByTagId(tag.getId());
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
                docIdSet.addAll(categoryServiceImpl.fuzzySearchDoc(keyWord));
                // 模糊查询 标签
                docIdSet.addAll(tagServiceImpl.fuzzySearchDoc(keyWord));
                // 模糊查询 文件标题
                docIdSet.addAll(fuzzySearchDoc(keyWord));
                // 模糊查询 评论内容
                docIdSet.addAll(commentServiceImpl.fuzzySearchDoc(keyWord));
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
                Category category = categoryServiceImpl.queryById(documentDTO.getCategoryId());
                if (category == null) {
                    break;
                }
                List<String> fileIdList = categoryServiceImpl.queryDocListByCategory(category);
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
        log.info("查询到的详细细节内容是:{}", fileDocument);
        // 查询评论信息，查询分类信息，查询分类关系，查询标签信息，查询标签关系信息
        return BaseApiResult.success(convertDocument(null, fileDocument));
    }

    @Override
    public BaseApiResult remove(String id) {
        if (!isExist(id)) {
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
        // 删除评论信息，删除分类关系，删除标签关系
        removeFile(id, true);
        commentServiceImpl.removeByDocId(id);
        categoryServiceImpl.removeRelateByDocId(id);
        collectServiceImpl.removeRelateByDocId(id);
        tagServiceImpl.removeRelateByDocId(id);

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
                    boolean relateExist = categoryServiceImpl.relateExist(restrictId, fileDocument.getId());
                    documentVos.add(entityTransfer(relateExist, fileDocument));
                }
                break;
            case TAG:
                restrictId = documentDTO.getTagId();
                for (FileDocument fileDocument : fileDocuments) {
                    boolean relateExist = tagServiceImpl.relateExist(restrictId, fileDocument.getId());
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
     * @Author luojiarui
     * @Description 符合关键字的总数查询
     * @Date 21:55 2022/11/17
     * @Param [keyWord]
     * @return long
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
        doc.setCategoryVO(categoryServiceImpl.queryByDocId(docId));
        doc.setSize(fileDocument.getSize());
        doc.setCreateTime(fileDocument.getUploadDate());
        doc.setTitle(fileDocument.getName());
        doc.setTagVOList(tagServiceImpl.queryByDocId(docId));
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
        documentVO.setId(fileDocument.getId());
        documentVO.setSize(fileDocument.getSize());
        documentVO.setTitle(fileDocument.getName());
        documentVO.setDescription(fileDocument.getDescription());
        documentVO.setUserName("admin");
        documentVO.setCreateTime(fileDocument.getUploadDate());
        documentVO.setThumbId(fileDocument.getThumbId());
        // 根据文档的id进行查询 评论， 收藏，分类， 标签
        String docId = fileDocument.getId();
        documentVO.setCommentNum(commentServiceImpl.commentNum(docId));
        documentVO.setCollectNum(collectServiceImpl.collectNum(docId));
        documentVO.setCategoryVO(categoryServiceImpl.queryByDocId(docId));
        documentVO.setTagVOList(tagServiceImpl.queryByDocId(docId));
        // 查询文档的信息:新增文档地址，文档错误信息，文本id
        documentVO.setDocState(fileDocument.getDocState());
        documentVO.setErrorMsg(fileDocument.getErrorMsg());
        documentVO.setTxtId(fileDocument.getTextFileId());

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
}
