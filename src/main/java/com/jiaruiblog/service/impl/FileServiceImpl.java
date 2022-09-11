package com.jiaruiblog.service.impl;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.entity.Category;
import com.jiaruiblog.entity.dto.DocumentDTO;
import com.jiaruiblog.entity.Tag;
import com.jiaruiblog.entity.vo.DocumentVO;
import com.jiaruiblog.service.IFileService;
import com.jiaruiblog.service.RedisService;
import com.jiaruiblog.utils.ApiResult;
import com.jiaruiblog.utils.PDFUtil;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.result.DeleteResult;
import com.jiaruiblog.entity.FileDocument;


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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FileServiceImpl implements IFileService {

    private static String collectionName = "fileDatas";

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

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
     * @param fileDocument
     * @param inputStream
     * @return
     */
    @Override
    public FileDocument saveFile(FileDocument fileDocument, InputStream inputStream) {
        //已存在该文件，则实现秒传
        FileDocument dbFile = getByMd5(fileDocument.getMd5());
        if (dbFile != null) {
            return dbFile;
        }

        //GridFSInputFile inputFile = gridFsTemplate

        String gridfsId = uploadFileToGridFS(inputStream, fileDocument.getContentType());
        fileDocument.setGridfsId(gridfsId);

        fileDocument = mongoTemplate.save(fileDocument, collectionName);

        // TODO 在这里进行异步操作

        return fileDocument;
    }

    /**
     * 表单上传附件
     *
     * @param md5
     * @param file
     * @return
     */
    @Override
    public FileDocument saveFile(String md5, MultipartFile file) {
        //已存在该文件，则实现秒传
        FileDocument fileDocument = getByMd5(md5);
        if ( fileDocument != null) {
            return fileDocument;
        }

        fileDocument = new FileDocument();
        fileDocument.setName(file.getOriginalFilename());
        fileDocument.setSize(file.getSize());
        fileDocument.setContentType(file.getContentType());
        fileDocument.setUploadDate(new Date());
        fileDocument.setMd5(md5);
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        fileDocument.setSuffix(suffix);

        try {
            String gridfsId = uploadFileToGridFS(file.getInputStream(), file.getContentType());
            fileDocument.setGridfsId(gridfsId);
            fileDocument = mongoTemplate.save(fileDocument, collectionName);
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
    private String uploadFileToGridFS(InputStream in, String contentType) {
        String gridfsId = IdUtil.simpleUUID();
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
        FileDocument fileDocument = mongoTemplate.findById(id, FileDocument.class, collectionName);
        if (fileDocument != null) {
            Query query = new Query().addCriteria(Criteria.where("_id").is(id));
            DeleteResult result = mongoTemplate.remove(query, collectionName);
            if (isDeleteFile) {
                Query deleteQuery = new Query().addCriteria(Criteria.where("filename").is(fileDocument.getGridfsId()));
                gridFsTemplate.delete(deleteQuery);
            }
        }
    }

    /**
     * 查询附件
     *
     * @param id 文件id
     * @return
     * @throws IOException
     */
    @Override
    public Optional<FileDocument> getById(String id) {
        FileDocument fileDocument = mongoTemplate.findById(id, FileDocument.class, collectionName);
        if (fileDocument != null) {
            Query gridQuery = new Query().addCriteria(Criteria.where("filename").is(fileDocument.getGridfsId()));
            try {
                GridFSFile fsFile = gridFsTemplate.findOne(gridQuery);
                GridFSDownloadStream in = gridFSBucket.openDownloadStream(fsFile.getObjectId());
                if (in.getGridFSFile().getLength() > 0) {
                    GridFsResource resource = new GridFsResource(fsFile, in);
                    fileDocument.setContent(IoUtil.readBytes(resource.getInputStream()));
                    return Optional.of(fileDocument);
                } else {
                    fileDocument = null;
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
     * @param md5
     * @return
     */
    @Override
    public FileDocument getByMd5(String md5) {
        if (md5 == null) {
            return null;
        }
        Query query = new Query().addCriteria(Criteria.where("md5").is(md5));
        FileDocument fileDocument = mongoTemplate.findOne(query, FileDocument.class, collectionName);
        return fileDocument;
    }

    @Override
    public List<FileDocument> listFilesByPage(int pageIndex, int pageSize) {
        Query query = new Query().with(Sort.by(Sort.Direction.DESC, "uploadDate"));
        long skip = (pageIndex) * pageSize;
        query.skip(skip);
        query.limit(pageSize);
        Field field = query.fields();
        field.exclude("content");
        List<FileDocument> files = mongoTemplate.find(query, FileDocument.class, collectionName);
        return files;
    }

    /**
     * @Author luojiarui
     * @Description // 增加过滤条件的分页功能
     * @Date 11:12 下午 2022/6/22
     * @Param [pageIndex, pageSize, ids]
     * @return java.util.List<com.jiaruiblog.entity.FileDocument>
     **/
    @Override
    public List<FileDocument> listAndFilterByPage(int pageIndex, int pageSize, Collection<String> ids) {
        if( CollectionUtils.isEmpty(ids)) {
            return null;
        }
        Query query = new Query().with(Sort.by(Sort.Direction.DESC, "uploadDate"));
        // 增加过滤条件
        query.addCriteria(Criteria.where("_id").in(ids));
        // 设置起始页和每页查询条数
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        query.with(pageable);


        Field field = query.fields();
        field.exclude("content");
        List<FileDocument> files = mongoTemplate.find(query, FileDocument.class, collectionName);
        return files;
    }

    /**
     * @Author luojiarui
     * @Description 列表；过滤；检索等
     * @Date 11:49 2022/8/6
     * @Param [documentDTO]
     * @return com.jiaruiblog.utils.ApiResult
     **/
    @Override
    public ApiResult list(DocumentDTO documentDTO) {
        log.info(MessageFormat.format(">>>>>>>检索文档>>>>>>检索参数{0}", documentDTO.toString()));
        List<DocumentVO> documentVOS;
        List<FileDocument> fileDocuments = Lists.newArrayList();

        long totalNum = 0L;

        switch (documentDTO.getType()) {
            case ALL:
                fileDocuments = listFilesByPage(documentDTO.getPage(),documentDTO.getRows());
                totalNum = countAllFile();
                break;
            case TAG:
                Tag tag = tagServiceImpl.queryByTagId(documentDTO.getTagId());
                if(tag == null) {
                    break;
                }
                List<String> fileIdList1 = tagServiceImpl.queryDocIdListByTagId(tag.getId());
                fileDocuments = listAndFilterByPage(documentDTO.getPage(), documentDTO.getRows(), fileIdList1);
                if(CollectionUtils.isEmpty(fileIdList1)) {
                    break;
                }
                Query query = new Query().addCriteria(Criteria.where("_id").in(fileIdList1));
                totalNum = countFileByQuery(query);
                break;
            case FILTER:
                Set<String> docIdSet = new HashSet<>();
                String keyWord = Optional.ofNullable(documentDTO).map(DocumentDTO::getFilterWord).orElse("");
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
                    if( !CollectionUtils.isEmpty(esDoc)) {
                        Set<String> existIds = esDoc.stream().map(FileDocument::getId).collect(Collectors.toSet());
                        docIdSet.removeAll(existIds);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                fileDocuments = listAndFilterByPage(documentDTO.getPage(), documentDTO.getRows(), docIdSet);
                if(esDoc != null){
                    fileDocuments = Optional.ofNullable(fileDocuments).orElse(new ArrayList<>());
                    fileDocuments.addAll(esDoc);
                }
                totalNum = fileDocuments.size();
                break;
            case CATEGORY:
                Category category = categoryServiceImpl.queryById(documentDTO.getCategoryId());
                if(category == null ) {
                    break;
                }
                List<String> fileIdList = categoryServiceImpl.queryDocListByCategory(category);
                fileDocuments = listAndFilterByPage(documentDTO.getPage(), documentDTO.getRows(), fileIdList);
                if(CollectionUtils.isEmpty(fileIdList)) {
                    break;
                }
                Query query1 = new Query().addCriteria(Criteria.where("_id").in(fileIdList));
                totalNum = countFileByQuery(query1);
                break;
            default:
                return ApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_IS_NOT_NULL);
        }
        documentVOS = convertDocuments(fileDocuments);
        Map<String, Object> result = new HashMap<>();
        result.put("totalNum", totalNum);
        result.put("documents", documentVOS);
        return ApiResult.success(result);
    }

    /**
     * @Author luojiarui
     * @Description // 查询文档的详细信息
     * @Date 9:27 下午 2022/6/23
     * @Param [id]
     * @return com.jiaruiblog.utils.ApiResult
     **/
    @Override
    public ApiResult detail(String id) {
        FileDocument fileDocument = queryById(id);
        if( fileDocument == null ) {
            return ApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.PARAMS_LENGTH_REQUIRED);
        } else {
            redisService.incrementScoreByUserId(id, RedisServiceImpl.DOC_KEY);
        }
        // 查询评论信息，查询分类信息，查询分类关系，查询标签信息，查询标签关系信息
        return ApiResult.success(convertDocument(null, fileDocument));
    }

    @Override
    public ApiResult remove(String id) {
        if( !isExist(id) ) {
            return ApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
        // 删除评论信息，删除分类关系，删除标签关系
        removeFile(id, true);
        commentServiceImpl.removeByDocId(id);
        categoryServiceImpl.removeRelateByDocId(id);
        collectServiceImpl.removeRelateByDocId(id);
        tagServiceImpl.removeRelateByDocId(id);

        return ApiResult.success(MessageConstant.SUCCESS);
    }

    /**
     * @Author luojiarui
     * @Description //TODO
     * @Date 10:16 下午 2022/6/21
     * @Param fileDocuments
     * @return java.util.List<com.jiaruiblog.entity.vo.DocumentVO>
     **/
    private List<DocumentVO> convertDocuments(List<FileDocument> fileDocuments) {
        if( fileDocuments == null) {
            return null;
        }
        List<DocumentVO> documentVOS = Lists.newArrayList();
        for(FileDocument fileDocument : fileDocuments) {
            DocumentVO documentVO = new DocumentVO();
            documentVO = convertDocument(documentVO, fileDocument);
            documentVOS.add(documentVO);
        }
        return documentVOS;
    }

    /**
     * @Author luojiarui
     * @Description //TODO
     * @Date 10:24 下午 2022/6/21
     * @Param [documentVO, fileDocument]
     * @return com.jiaruiblog.entity.vo.DocumentVO
     **/
    public DocumentVO convertDocument(DocumentVO documentVO, FileDocument fileDocument) {
        documentVO = Optional.ofNullable(documentVO).orElse(new DocumentVO());
        if(fileDocument == null ){
            return documentVO;
        }
        documentVO.setId(fileDocument.getId());
        documentVO.setSize(fileDocument.getSize());
        documentVO.setTitle(fileDocument.getName());
        documentVO.setDescription(fileDocument.getDescription());
        documentVO.setUserName("luojiarui");
        documentVO.setCreateTime(fileDocument.getUploadDate());
        documentVO.setThumbId(fileDocument.getThumbId());
        // 根据文档的id进行查询 评论， 收藏，分类， 标签
        String docId = fileDocument.getId();

        documentVO.setCommentNum(commentServiceImpl.commentNum(docId));
        documentVO.setCollectNum(collectServiceImpl.collectNum(docId));
        documentVO.setCategoryVO(categoryServiceImpl.queryByDocId(docId));
        documentVO.setTagVOList(tagServiceImpl.queryByDocId(docId));
        return documentVO;
    }

    /**
     * 模糊搜索
     * @param keyWord
     * @return
     */
    public List<String> fuzzySearchDoc(String keyWord) {
        if(keyWord == null || "".equalsIgnoreCase(keyWord)) {
            return null;
        }
        Pattern pattern = Pattern.compile("^.*"+keyWord+".*$", Pattern.CASE_INSENSITIVE);
        Query query = new Query();
        query.addCriteria(Criteria.where("name").regex(pattern));

        List<FileDocument> documents = mongoTemplate.find(query, FileDocument.class, collectionName);
        return documents.stream().map(FileDocument::getId).collect(Collectors.toList());
    }

    //Pattern pattern=Pattern.compile("^.*"+pattern_name+".*$", Pattern.CASE_INSENSITIVE);
    //query.addCriteria(Criteria.where("name").regex(pattern))；


    /**
     * 根据用户的主键id查询用户信息
     * @param docId
     * @return
     */
    public boolean isExist(String docId) {
        if(docId == null || "".equals(docId)) {
            return false;
        }
        FileDocument fileDocument = queryById(docId);
        if(fileDocument == null) {
            return false;
        }
        return true;
    }

    /**
     * 检索已经存在的user
     * @param docId
     * @return
     */
    public FileDocument queryById(String docId) {
        return mongoTemplate.findById(docId, FileDocument.class, collectionName);
    }


    /**
     * @Author luojiarui
     * @Description // 统计总数
     * @Date 4:40 下午 2022/6/26
     * @Param []
     * @return java.lang.Integer
     **/
    public long countAllFile() {
        return mongoTemplate.getCollection(collectionName).estimatedDocumentCount();
    }

    /**
     * @Author luojiarui
     * @Description //转换pdf文档的图片，然后保存
     * @Date 7:49 下午 2022/7/24
     * @Param [inputStream, fileDocument]
     * @return void
     **/
    @Async
    @Override
    public void updateFileThumb(InputStream inputStream, FileDocument fileDocument) throws FileNotFoundException {
        String path = "thumbnail";   // 新建pdf文件的路径
        String picPath = path + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + ".png";
        String gridfsId = IdUtil.simpleUUID();
        if(fileDocument.getSuffix().equals(".pdf")) {
            // 将pdf输入流转换为图片并临时保存下来
            PDFUtil.pdfThumbnail(inputStream, picPath);

            if(new File(picPath).exists()) {
                String contentType = "image/png";
                FileInputStream in = new FileInputStream(picPath);
                //文件，存储在GridFS
                gridFsTemplate.store(in, gridfsId, contentType);
                new File(picPath).delete();
            }
        }

        Query query = new Query().addCriteria(Criteria.where("_id").is(fileDocument.getId()));;
        Update update = new Update().set("thumbId", gridfsId);
        mongoTemplate.updateFirst(query, update, FileDocument.class, collectionName);

    }

    /**
     * @Author luojiarui
     * @Description //根据缩略图id返回图片信息
     * @Date 7:59 下午 2022/7/24
     * @Param [thumbId]
     * @return java.io.InputStream
     **/
    @Override
    public InputStream getFileThumb(String thumbId) {
        if ( StringUtils.hasText(thumbId)) {
            Query gridQuery = new Query().addCriteria(Criteria.where("filename").is(thumbId));
            try {
                GridFSFile fsFile = gridFsTemplate.findOne(gridQuery);
                if(fsFile == null) {
                    return null;
                }
                GridFSDownloadStream in = gridFSBucket.openDownloadStream(fsFile.getObjectId());
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

    /**
     * @Author luojiarui
     * @Description 根据查询条件查询总数量
     * @Date 12:09 2022/8/6
     * @Param [query]
     * @return long
     **/
    public long countFileByQuery(Query query) {
        return mongoTemplate.count(query, FileDocument.class, collectionName);
    }
}
