package com.jiaruiblog.service.impl;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.entity.Category;
import com.jiaruiblog.entity.DTO.DocumentDTO;
import com.jiaruiblog.service.CategoryService;
import com.jiaruiblog.service.IFileService;
import com.jiaruiblog.utils.ApiResult;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.result.DeleteResult;
import com.jiaruiblog.entity.FileDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Field;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
        if (fileDocument != null) {
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
            System.out.println("result:" + result.getDeletedCount());

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
        Query query = new Query().addCriteria(Criteria.where("md5").is(md5));
        FileDocument fileDocument = mongoTemplate.findOne(query, FileDocument.class, collectionName);
        return fileDocument;
    }

    @Override
    public List<FileDocument> listFilesByPage(int pageIndex, int pageSize) {
        Query query = new Query().with(Sort.by(Sort.Direction.DESC, "uploadDate"));
        long skip = (pageIndex - 1) * pageSize;
        query.skip(skip);
        query.limit(pageSize);
        Field field = query.fields();
        field.exclude("content");
        List<FileDocument> files = mongoTemplate.find(query, FileDocument.class, collectionName);
        return files;
    }

    @Override
    public ApiResult list(DocumentDTO documentDTO) {
        switch (documentDTO.getType()) {
            case ALL:
                break;
            case TAG:
                break;
            case FILTER:
                break;
            case CATEGORY:
                Category category = categoryServiceImpl.queryById(documentDTO.getCategoryId());
                if(category == null ) {
                    System.out.println("直接进行返回空");
                }
                List<Long> fileListId = categoryServiceImpl.queryDocListByCategory(category);
                List<FileDocument> fileDocuments = mongoTemplate.findById(fileListId.get(0));

                break;
            default:
                return ApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_IS_NOT_NULL);
        }
    }

    @Override
    public ApiResult detail(Long id) {
        FileDocument fileDocument = mongoTemplate.findById(id, FileDocument.class, collectionName);
        if( fileDocument == null ) {
            return ApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.PARAMS_LENGTH_REQUIRED);
        }
        // 查询评论信息，查询分类信息，查询分类关系，查询标签信息，查询标签关系信息

        return null;
    }

    @Override
    public ApiResult remove(Long id) {
        FileDocument fileDocument = mongoTemplate.findById(id, FileDocument.class, collectionName);
        if( fileDocument == null ) {
            return ApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.PARAMS_LENGTH_REQUIRED);
        }
        // 删除评论信息，删除分类关系，删除标签关系

        removeFile(id.toString(), true);
        return ApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.PARAMS_LENGTH_REQUIRED);
    }
}
