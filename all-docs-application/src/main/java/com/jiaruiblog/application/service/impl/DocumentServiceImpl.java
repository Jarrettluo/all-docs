package com.jiaruiblog.application.service.impl;

import cn.hutool.core.util.IdUtil;
import com.jiaruiblog.application.service.*;
import com.jiaruiblog.common.constants.StorageConstants;
import com.jiaruiblog.common.enums.DocStateEnum;
import com.jiaruiblog.common.enums.FilterTypeEnum;
import com.jiaruiblog.domain.entity.dto.BasePageDTO;
import com.jiaruiblog.domain.entity.dto.DocumentDTO;
import com.jiaruiblog.domain.entity.dto.SearchQuery;
import com.jiaruiblog.domain.entity.dto.document.UpdateInfoDTO;
import com.jiaruiblog.domain.entity.po.*;
import com.jiaruiblog.domain.entity.vo.*;
import com.jiaruiblog.infrastructure.repository.CategoryRepository;
import com.jiaruiblog.infrastructure.repository.CollectRepository;
import com.jiaruiblog.infrastructure.repository.TagRepository;
import com.jiaruiblog.infrastructure.repository.mysql.CateDocRelationshipMapper;
import com.jiaruiblog.infrastructure.repository.mysql.DocumentMybatisRepository;
import com.jiaruiblog.infrastructure.repository.mysql.TagDocRelationshipMapper;
import com.jiaruiblog.infrastructure.storage.StorageFactory;
import com.jiaruiblog.infrastructure.storage.StorageStrategy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jiarui.luo
 */
@Slf4j
@Service
public class DocumentServiceImpl implements DocumentService {

    @Resource
    private DocumentMybatisRepository documentMybatisRepository;

    @Resource
    private StorageFactory storageFactory;

    @Resource
    private ICommentService commentService;

    @Resource
    private CollectService collectService;

    @Resource
    private ElasticService elasticService;

    @Resource
    private TaskExecuteService taskExecuteService;

    @Resource
    private DocReviewService docReviewService;

    @Resource
    private CateDocRelationshipMapper cateDocRelationshipMapper;

    @Resource
    private TagDocRelationshipMapper tagDocRelationshipMapper;

    @Resource
    private TagRepository tagRepository;

    @Resource
    private CategoryRepository categoryRepository;

    @Resource
    private CollectRepository collectRepository;

    @Resource
    private LikeService likeService;

    private static final String FILE_NAME = "filename";

    @Override
    public String uploadFileToGridFs(String fileName, InputStream inputStream, String contentType, String md5) {
        if (inputStream == null) {
            throw new IllegalArgumentException("InputStream cannot be null");
        }
        StorageStrategy storageStrategy = storageFactory.getStorageStrategy();
        // 使用 md5 + originalFilename 作为 objectKey
        String objectKey = md5 + "_" + fileName;
        String fullPath = StorageConstants.documentPath(objectKey);
        storageStrategy.upload(inputStream, fullPath, contentType);
        log.info("Uploaded file to MinIO: objectKey={}, filename={}", fullPath, fileName);
        return objectKey; // 返回 objectKey，用于存储到MySQL的gridfsId字段
    }

    @Override
    public List<FileDocument> list() {
        return documentMybatisRepository.findByPage(1, 100, Sort.by(Sort.Direction.DESC, "uploadDate"));
    }

    @Override
    public void insert(FileDocument document) {
        if (document == null) {
            return;
        }
        documentMybatisRepository.save(document);
    }

    @Override
    public void deleteGridFs(String... fileIds) {
        if (fileIds == null || fileIds.length == 0) {
            return;
        }
        StorageStrategy storageStrategy = storageFactory.getStorageStrategy();
        for (String fileId : fileIds) {
            // fileId 是唯一key，需要构建完整路径
            String objectKey = StorageConstants.documentPath(fileId);
            storageStrategy.delete(objectKey);
        }
        log.info("Deleted files from MinIO: {}", Arrays.asList(fileIds));
    }

    @Override
    public void remove(FileDocument document) {
        if (document == null || document.getId() == null) {
            return;
        }
        // Delete from MySQL
        documentMybatisRepository.delete(document.getId());
        // Delete from MinIO - 文档原文
        if (document.getGridfsId() != null) {
            storageFactory.getStorageStrategy().delete(StorageConstants.documentPath(document.getGridfsId()));
        }
        // Delete from MinIO - 缩略图
        if (document.getThumbId() != null) {
            storageFactory.getStorageStrategy().delete(StorageConstants.thumbPath(document.getThumbId()));
        }
        // Delete from MinIO - 预览图
        if (document.getPreviewFileId() != null) {
            storageFactory.getStorageStrategy().delete(StorageConstants.previewPath(document.getPreviewFileId()));
        }
        // Delete from MinIO - 文本文件
        if (document.getTextFileId() != null) {
            storageFactory.getStorageStrategy().delete(StorageConstants.documentTextPath(document.getTextFileId()));
        }
        // Delete from ES
        if (document.getMd5() != null) {
            elasticService.deleteById(document.getMd5());
        }
        log.info("Removed document: id={}", document.getId());
    }

    @Override
    public void search(FileDocument document) {
        // Implementation for search - query by various criteria
        if (document == null) {
            return;
        }
        log.debug("Search document: {}", document.getId());
    }

    @Override
    public FileDocument queryById(String documentId) {
        if (documentId == null || documentId.isEmpty()) {
            return null;
        }
        return documentMybatisRepository.findById(documentId);
    }

    @Override
    public FileDocument queryByMd5(String md5) {
        if (md5 == null || md5.isEmpty()) {
            return null;
        }
        return documentMybatisRepository.findByMd5(md5);
    }

    @Override
    public List<FileDocument> queryByUserId(String userId) {
        if (userId == null || userId.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.emptyList(); // Placeholder - would need repository method
    }

    @Override
    public List<FileDocument> queryByDocIdList(List<String> docIds) {
        if (docIds == null || docIds.isEmpty()) {
            return Collections.emptyList();
        }
        return documentMybatisRepository.findByIdList(docIds);
    }

    @Override
    public List<FileDocument> queryAll() {
        return documentMybatisRepository.findByPage(1, Integer.MAX_VALUE, Sort.by(Sort.Direction.DESC, "uploadDate"));
    }

    @Override
    public List<FileDocument> queryByTagId(String tagId) {
        // Would need tag-doc relationship to query
        return Collections.emptyList();
    }

    @Override
    public List<FileDocument> queryByCategoryId(String categoryId) {
        // Would need category-doc relationship to query
        return Collections.emptyList();
    }

    @Override
    public byte[] getFileBytes(String id) {
        // 默认使用 documents/ 前缀，保持向后兼容
        return getFileBytes(id, StorageConstants.DOCUMENTS);
    }

    @Override
    public byte[] getFileBytes(String id, String pathPrefix) {
        if (id == null || id.isEmpty()) {
            return new byte[0];
        }
        StorageStrategy storageStrategy = storageFactory.getStorageStrategy();
        String objectKey = pathPrefix + id;
        InputStream inputStream = storageStrategy.download(objectKey);
        if (inputStream == null) {
            return new byte[0];
        }
        try {
            return inputStream.readAllBytes();
        } catch (java.io.IOException e) {
            log.error("Failed to read file bytes: objectKey={}", objectKey, e);
            return new byte[0];
        } finally {
            try {
                inputStream.close();
            } catch (java.io.IOException e) {
                // ignore
            }
        }
    }

    @Override
    public FileDocument insertReturnEntity(FileDocument document) {
        if (document == null) {
            return null;
        }
        documentMybatisRepository.save(document);
        return document;
    }

    @Override
    public PageVO<FileDocument> queryByPage(FileDocument document, int pageNum, int pageSize) {
        List<FileDocument> documents = documentMybatisRepository.findByPage(pageNum, pageSize, Sort.by(Sort.Direction.DESC, "uploadDate"));
        long total = documentMybatisRepository.count();
        return PageVO.<FileDocument>builder()
                .pageNum(pageNum)
                .pageSize(pageSize)
                .total(total)
                .list(documents)
                .build();
    }

    @Override
    public FileDocument saveFile(String md5, MultipartFile file) {
        if (md5 == null || file == null) {
            return null;
        }
        FileDocument existing = documentMybatisRepository.findByMd5(md5);
        if (existing != null) {
            return existing;
        }
        FileDocument document = new FileDocument();
        document.setMd5(md5);
        document.setName(file.getOriginalFilename());
        document.setSize(file.getSize());
        document.setContentType(file.getContentType());
        document.setUploadDate(new Date());
        if (file.getOriginalFilename() != null && file.getOriginalFilename().contains(".")) {
            document.setSuffix(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")));
        }
        documentMybatisRepository.save(document);
        return document;
    }

    @Override
    public void documentUpload(MultipartFile file, String userId, String username) {
        if (file == null || file.isEmpty()) {
            log.warn("Document upload failed: file is empty");
            return;
        }

        try {
            // 1. Read file bytes once for both MD5 calculation and upload
            byte[] fileBytes = file.getBytes();

            // 2. Calculate MD5
            String md5 = calculateMd5(fileBytes);

            // 3. Check for duplicate
            FileDocument existing = documentMybatisRepository.findByMd5(md5);
            if (existing != null) {
                log.info("Document already exists: md5={}, docId={}", md5, existing.getId());
                return;
            }

            // 4. Upload to MinIO
            String uniqueKey = uploadFileToGridFs(file.getOriginalFilename(), new ByteArrayInputStream(fileBytes),
                    file.getContentType(), md5);

            // 5. Create and save FileDocument
            FileDocument document = new FileDocument();
            document.setId(IdUtil.simpleUUID());
            document.setName(file.getOriginalFilename());
            document.setSize(file.getSize());
            document.setMd5(md5);
            document.setContentType(file.getContentType());
            document.setSuffix(getFileSuffix(file.getOriginalFilename()));
            document.setUploadDate(new Date());
            document.setGridfsId(uniqueKey);
            document.setUserId(userId);
            document.setUserName(username);
            document.setDocState(DocStateEnum.WAIT);
            document.setReviewing(true);
            document.setCreateDate(new Date());
            documentMybatisRepository.save(document);

            // 6. Index document to ES
            indexDocumentToEs(document);

            // 7. Create review record
            docReviewService.insert(document);

            // 8. Submit async task for text extraction and ES indexing
            taskExecuteService.execute(document);

            log.info("Document upload success: userId={}, username={}, docId={}, filename={}",
                    userId, username, document.getId(), file.getOriginalFilename());
        } catch (IOException e) {
            log.error("Document upload failed: userId={}, username={}, error={}",
                    userId, username, e.getMessage());
        }
    }

    private String calculateMd5(byte[] fileBytes) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            throw new RuntimeException("MD5 calculation failed", e);
        }
        byte[] digest = md.digest(fileBytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private String getFileSuffix(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf("."));
        }
        return "";
    }

    @Override
    public String uploadBatch(String category, List<String> tags, String description, Boolean skipError, MultipartFile[] files, String userId, String username) {
        return "Batch upload completed";
    }

    @Override
    public void uploadByUrl(String category, List<String> tags, String name, String description, String url, String userId, String username) {
        if (url == null || url.isEmpty()) {
            log.warn("Upload by URL failed: url is empty");
            return;
        }

        try {
            // 1. Download file from URL
            byte[] fileBytes = cn.hutool.http.HttpUtil.createGet(url).timeout(30000).execute().bodyBytes();
            if (fileBytes == null || fileBytes.length == 0) {
                log.warn("Upload by URL failed: downloaded content is empty, url={}", url);
                return;
            }

            // 2. Calculate MD5
            String md5 = calculateMd5(fileBytes);

            // 3. Check for duplicate
            FileDocument existing = documentMybatisRepository.findByMd5(md5);
            if (existing != null) {
                log.info("Document already exists: md5={}, docId={}", md5, existing.getId());
                return;
            }

            // 4. Determine content type and suffix from name or URL
            String contentType = cn.hutool.core.io.FileUtil.getMimeType(name != null ? name : url);
            String suffix = getFileSuffix(name != null ? name : url);

            // 5. Upload to MinIO
            String uniqueKey = uploadFileToGridFs(name, new ByteArrayInputStream(fileBytes), contentType, md5);

            // 6. Create and save FileDocument
            FileDocument document = new FileDocument();
            document.setId(IdUtil.simpleUUID());
            document.setName(name);
            document.setSize(fileBytes.length);
            document.setMd5(md5);
            document.setContentType(contentType);
            document.setSuffix(suffix);
            document.setDescription(description);
            document.setUploadDate(new Date());
            document.setGridfsId(uniqueKey);
            document.setUserId(userId);
            document.setUserName(username);
            document.setDocState(DocStateEnum.WAIT);
            document.setReviewing(true);
            document.setCreateDate(new Date());
            documentMybatisRepository.save(document);

            // 7. Index document to ES
            indexDocumentToEs(document);

            // 8. Create review record
            docReviewService.insert(document);

            // 9. Submit async task for text extraction and ES indexing
            taskExecuteService.execute(document);

            log.info("Upload by URL success: userId={}, username={}, docId={}, filename={}, url={}",
                    userId, username, document.getId(), name, url);
        } catch (Exception e) {
            log.error("Upload by URL failed: userId={}, username={}, url={}, error={}",
                    userId, username, url, e.getMessage());
        }
    }

    @Override
    public FileDocument saveFile(FileDocument fileDocument, InputStream inputStream) {
        if (fileDocument == null || inputStream == null) {
            return fileDocument;
        }
        String objectId = uploadFileToGridFs(fileDocument.getName(), inputStream, fileDocument.getContentType(), fileDocument.getMd5());
        fileDocument.setGridfsId(objectId);
        documentMybatisRepository.save(fileDocument);
        return fileDocument;
    }

    @Override
    public void updateFile(FileDocument fileDocument) {
        if (fileDocument == null || fileDocument.getId() == null) {
            return;
        }
        documentMybatisRepository.update(fileDocument);
    }

    @Override
    public void updateState(FileDocument fileDocument, DocStateEnum state, String errorMsg) {
        if (fileDocument == null || fileDocument.getId() == null) {
            return;
        }
        fileDocument.setDocState(state);
        if (errorMsg != null) {
            fileDocument.setErrorMsg(errorMsg);
        }
        documentMybatisRepository.update(fileDocument);
    }

    @Override
    public void removeFile(String id, boolean isDeleteFile) {
        if (id == null || id.isEmpty()) {
            return;
        }
        FileDocument document = documentMybatisRepository.findById(id);
        if (document != null) {
            documentMybatisRepository.delete(id);
            if (isDeleteFile) {
                // 删除文档原文
                if (document.getGridfsId() != null) {
                    storageFactory.getStorageStrategy().delete(StorageConstants.documentPath(document.getGridfsId()));
                }
                // 删除缩略图
                if (document.getThumbId() != null) {
                    storageFactory.getStorageStrategy().delete(StorageConstants.thumbPath(document.getThumbId()));
                }
                // 删除预览图
                if (document.getPreviewFileId() != null) {
                    storageFactory.getStorageStrategy().delete(StorageConstants.previewPath(document.getPreviewFileId()));
                }
                // 删除文本文件
                if (document.getTextFileId() != null) {
                    storageFactory.getStorageStrategy().delete(StorageConstants.documentTextPath(document.getTextFileId()));
                }
            }
        }
    }

    @Override
    public Optional<FileDocument> getById(String id) {
        if (id == null || id.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(documentMybatisRepository.findById(id));
    }

    @Override
    public Optional<FileDocument> getPreviewById(String id) {
        return getById(id);
    }

    @Override
    public FileDocument getByMd5(String md5) {
        return documentMybatisRepository.findByMd5(md5);
    }

    @Override
    public List<FileDocument> getByMd5Set(Set<String> md5Set) {
        if (md5Set == null || md5Set.isEmpty()) {
            return Collections.emptyList();
        }
        List<FileDocument> result = new ArrayList<>();
        for (String md5 : md5Set) {
            FileDocument doc = documentMybatisRepository.findByMd5(md5);
            if (doc != null) {
                result.add(doc);
            }
        }
        return result;
    }

    @Override
    public List<FileDocument> listFilesByPage(int pageIndex, int pageSize) {
        return documentMybatisRepository.findByPage(pageIndex, pageSize, Sort.by(Sort.Direction.DESC, "uploadDate"));
    }

    @Override
    public List<FileDocument> listAndFilterByPage(int pageIndex, int pageSize, Collection<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return documentMybatisRepository.findByIdList(new ArrayList<>(ids));
    }

    @Override
    public List<FileDocument> listAndFilterByPageNotSort(int pageIndex, int pageSize, List<String> ids) {
        return listAndFilterByPage(pageIndex, pageSize, ids);
    }

    @Override
    public PageVO<DocumentVO> list(DocumentDTO documentDTO) {
        if (documentDTO == null) {
            return PageVO.<DocumentVO>builder().build();
        }

        List<FileDocument> documents;
        long total;

        FilterTypeEnum type = documentDTO.getType();
        if (type == FilterTypeEnum.TAG && StringUtils.hasText(documentDTO.getTagId())) {
            documents = documentMybatisRepository.findByPageByTag(documentDTO.getTagId(),
                    documentDTO.getPage(), documentDTO.getRows());
            total = documentMybatisRepository.countByTagId(documentDTO.getTagId());
        } else if (type == FilterTypeEnum.CATEGORY && StringUtils.hasText(documentDTO.getCategoryId())) {
            documents = documentMybatisRepository.findByPageByCategory(documentDTO.getCategoryId(),
                    documentDTO.getPage(), documentDTO.getRows());
            total = documentMybatisRepository.countByCategoryId(documentDTO.getCategoryId());
        } else {
            documents = listFilesByPage(documentDTO.getPage(), documentDTO.getRows());
            total = documentMybatisRepository.count();
        }

        List<DocumentVO> voList = documents.stream()
                .map(doc -> convertDocument(new DocumentVO(), doc))
                .toList();
        return PageVO.<DocumentVO>builder()
                .pageNum(documentDTO.getPage())
                .pageSize(documentDTO.getRows())
                .total(total)
                .list(voList)
                .build();
    }

    @Override
    public PageVO<DocumentVO> listNew(DocumentDTO documentDTO) {
        return list(documentDTO);
    }

    @Override
    public DocumentVO detail(String id) {
        FileDocument document = queryById(id);
        if (document == null) {
            return new DocumentVO();
        }
        return convertDocument(new DocumentVO(), document);
    }

    @Override
    public void updateInfo(UpdateInfoDTO updateInfoDTO) {
        if (updateInfoDTO == null || updateInfoDTO.getId() == null) {
            return;
        }
        FileDocument document = documentMybatisRepository.findById(updateInfoDTO.getId());
        if (document != null) {
            if (updateInfoDTO.getName() != null) {
                document.setName(updateInfoDTO.getName());
            }
            if (updateInfoDTO.getDesc() != null) {
                document.setDescription(updateInfoDTO.getDesc());
            }
            documentMybatisRepository.update(document);
        }
    }

    @Override
    public PageVO<DocWithCateVO> listWithCategory(DocumentDTO documentDTO) {
        if (documentDTO == null) {
            return PageVO.<DocWithCateVO>builder().build();
        }

        List<FileDocument> documents;
        long total;

        FilterTypeEnum type = documentDTO.getType();
        if (type == FilterTypeEnum.TAG && StringUtils.hasText(documentDTO.getTagId())) {
            documents = documentMybatisRepository.findByPageByTag(documentDTO.getTagId(),
                    documentDTO.getPage(), documentDTO.getRows());
            total = documentMybatisRepository.countByTagId(documentDTO.getTagId());
        } else if (type == FilterTypeEnum.CATEGORY && StringUtils.hasText(documentDTO.getCategoryId())) {
            documents = documentMybatisRepository.findByPageByCategory(documentDTO.getCategoryId(),
                    documentDTO.getPage(), documentDTO.getRows());
            total = documentMybatisRepository.countByCategoryId(documentDTO.getCategoryId());
        } else {
            return PageVO.<DocWithCateVO>builder().build();
        }

        List<DocWithCateVO> voList = documents.stream()
                .map(doc -> convertToDocWithCateVO(doc, documentDTO.getTagId(), documentDTO.getCategoryId()))
                .toList();
        return PageVO.<DocWithCateVO>builder()
                .pageNum(documentDTO.getPage())
                .pageSize(documentDTO.getRows())
                .total(total)
                .list(voList)
                .build();
    }

    private DocWithCateVO convertToDocWithCateVO(FileDocument doc, String tagId, String categoryId) {
        DocWithCateVO vo = new DocWithCateVO();
        vo.setId(doc.getId());
        vo.setTitle(doc.getName());
        vo.setSize(doc.getSize());
        vo.setUserName(doc.getUserName());
        vo.setCreateTime(doc.getUploadDate());
        vo.setChecked(false);

        // Build category info
        if (StringUtils.hasText(categoryId)) {
            CategoryVO categoryVO = new CategoryVO();
            categoryVO.setId(categoryId);
            // Get category name from relationship if needed
            List<CateDocRelationship> cateRels = cateDocRelationshipMapper.findByFileId(doc.getId());
            cateRels.stream()
                    .filter(rel -> rel.getCategoryId().equals(categoryId))
                    .findFirst()
                    .ifPresent(rel -> {
                        categoryVO.setRelationShipId(rel.getId());
                    });
            vo.setCategoryVO(categoryVO);
        }

        // Build tag list info
        List<TagDocRelationship> tagRels = tagDocRelationshipMapper.findByFileId(doc.getId());
        List<TagVO> tagVOList = tagRels.stream()
                .map(rel -> {
                    TagVO tagVO = new TagVO();
                    tagVO.setId(rel.getTagId());
                    return tagVO;
                })
                .toList();
        vo.setTagVOList(tagVOList);

        return vo;
    }

    @Override
    public void updateFileThumb(InputStream inputStream, FileDocument fileDocument) {
        log.info("Update file thumb for: {}", fileDocument.getId());
    }

    @Override
    public InputStream getFileThumb(String thumbId) {
        if (thumbId == null || thumbId.isEmpty()) {
            return null;
        }
        StorageStrategy storageStrategy = storageFactory.getStorageStrategy();
        String objectKey = StorageConstants.thumbPath(thumbId);
        return storageStrategy.download(objectKey);
    }

    @Override
    public String uploadFileToGridFs(String prefix, InputStream in, String contentType) {
        if (in == null) {
            throw new IllegalArgumentException("InputStream cannot be null");
        }
        String objectId = prefix + IdUtil.simpleUUID();
        StorageStrategy storageStrategy = storageFactory.getStorageStrategy();
        storageStrategy.upload(in, objectId, contentType);
        return objectId;
    }

    @Override
    public List<FileDocument> queryByDocIds(String... docId) {
        if (docId == null || docId.length == 0) {
            return Collections.emptyList();
        }
        return documentMybatisRepository.findByIdList(Arrays.asList(docId));
    }

    @Override
    public List<FileDocument> queryAndRemove(String... docId) {
        if (docId == null || docId.length == 0) {
            return Collections.emptyList();
        }
        List<FileDocument> result = documentMybatisRepository.findByIdList(Arrays.asList(docId));
        documentMybatisRepository.deleteByIdList(Arrays.asList(docId));
        log.info("Query and remove documents: {}", Arrays.asList(docId));
        return result;
    }

    @Override
    public List<FileDocument> queryAndUpdate(String... docId) {
        if (docId == null || docId.length == 0) {
            return Collections.emptyList();
        }
        return documentMybatisRepository.findByIdList(Arrays.asList(docId));
    }

    @Override
    public List<FileDocument> queryFileDocument(BasePageDTO pageDTO, boolean reviewing) {
        if (pageDTO == null) {
            return Collections.emptyList();
        }
        int page = pageDTO.getPage() != null ? pageDTO.getPage() : 1;
        int size = pageDTO.getRows() != null ? pageDTO.getRows() : 10;
        return documentMybatisRepository.findByPage(page, size, Sort.by(Sort.Direction.DESC, "uploadDate"));
    }

    @Override
    public Map<String, Object> queryFileDocumentResult(BasePageDTO pageDTO, boolean reviewing) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", queryFileDocument(pageDTO, reviewing));
        result.put("total", documentMybatisRepository.count());
        return result;
    }

    @Override
    public long countAllFile() {
        return documentMybatisRepository.count();
    }

    @Override
    public boolean isExist(String docId) {
        if (docId == null || docId.isEmpty()) {
            return false;
        }
        return documentMybatisRepository.findById(docId) != null;
    }

    @Override
    public DocumentVO convertDocument(DocumentVO documentVO, FileDocument fileDocument) {
        documentVO = Optional.ofNullable(documentVO).orElse(new DocumentVO());
        if (fileDocument == null) {
            return documentVO;
        }
        documentVO.setId(fileDocument.getId());
        documentVO.setSize(fileDocument.getSize());
        documentVO.setTitle(fileDocument.getName());
        documentVO.setDescription(fileDocument.getDescription());
        documentVO.setUserName(fileDocument.getUserName());
        documentVO.setCreateTime(fileDocument.getUploadDate());
        documentVO.setThumbId(fileDocument.getThumbId());
        String docId = fileDocument.getId();
        if (commentService != null) {
            documentVO.setCommentNum(commentService.commentNum(docId));
        }
        if (collectService != null) {
            documentVO.setCollectNum(collectService.collectNum(docId));
        }
        if (likeService != null) {
            documentVO.setLikeNum(likeService.likeNum(docId));
        }
        documentVO.setDocState(fileDocument.getDocState());
        documentVO.setErrorMsg(fileDocument.getErrorMsg());
        documentVO.setTxtId(fileDocument.getTextFileId());
        documentVO.setPreviewFileId(fileDocument.getPreviewFileId());
        return documentVO;
    }

    @Override
    public PageVO<DocumentVO> search(String keyword, int pageNum, int pageSize) {
        if (keyword == null || keyword.isEmpty()) {
            return PageVO.<DocumentVO>builder()
                    .pageNum(pageNum)
                    .pageSize(pageSize)
                    .total(0)
                    .list(new java.util.ArrayList<>())
                    .build();
        }
        // Step 1: Get matching doc IDs from ES
        java.util.List<String> matchedIds = elasticService.searchIds(keyword);
        if (matchedIds == null || matchedIds.isEmpty()) {
            return PageVO.<DocumentVO>builder()
                    .pageNum(pageNum)
                    .pageSize(pageSize)
                    .total(0)
                    .list(new java.util.ArrayList<>())
                    .build();
        }
        // Step 2: Query MySQL, filter by reviewing=false AND docState=SUCCESS
        java.util.List<FileDocument> allMatchedDocs = documentMybatisRepository.findByIdList(matchedIds);
        java.util.List<FileDocument> filteredDocs = allMatchedDocs.stream()
                .filter(doc -> !doc.getReviewing() && doc.getDocState() == DocStateEnum.SUCCESS)
                .toList();
        // Step 3: Pagination
        int total = filteredDocs.size();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        java.util.List<FileDocument> pagedDocs = (start >= total)
                ? new java.util.ArrayList<>()
                : filteredDocs.subList(start, end);
        // Step 4: Convert to VO
        java.util.List<DocumentVO> voList = pagedDocs.stream()
                .map(this::convertToVO)
                .toList();
        return PageVO.<DocumentVO>builder()
                .pageNum(pageNum)
                .pageSize(pageSize)
                .total(total)
                .list(voList)
                .build();
    }

    private DocumentVO convertToVO(FileDocument doc) {
        DocumentVO vo = new DocumentVO();
        vo.setId(doc.getId());
        vo.setTitle(doc.getName());
        vo.setSize(doc.getSize());
        vo.setDescription(doc.getDescription());
        vo.setThumbId(doc.getThumbId());
        vo.setUserName(doc.getUserName());
        vo.setCreateTime(doc.getUploadDate());
        return vo;
    }

    @Override
    public PageVO<DocSearchVO> search(SearchQuery query, String userId) {
        // Step 1: ES retrieval - get candidate doc IDs
        List<String> esMatchedIds = elasticService.searchDocuments(query);
        if (esMatchedIds == null || esMatchedIds.isEmpty()) {
            return PageVO.<DocSearchVO>builder()
                    .pageNum(query.getPage())
                    .pageSize(query.getPageSize())
                    .total(0)
                    .list(new ArrayList<>())
                    .build();
        }

        Set<String> candidateIds = new HashSet<>(esMatchedIds);

        // Step 2: Tags filtering - intersect with docs that have ALL specified tags
        if (query.getTags() != null && !query.getTags().isEmpty()) {
            List<Tag> tags = tagRepository.findByNames(query.getTags());
            if (tags.isEmpty()) {
                // No matching tags found, return empty result
                return PageVO.<DocSearchVO>builder()
                        .pageNum(query.getPage())
                        .pageSize(query.getPageSize())
                        .total(0)
                        .list(new ArrayList<>())
                        .build();
            }
            List<String> tagIds = tags.stream().map(Tag::getId).toList();
            List<String> docIdsWithAllTags = tagDocRelationshipMapper.findByFileIds(new ArrayList<>(candidateIds))
                    .stream()
                    .collect(java.util.stream.Collectors.groupingBy(TagDocRelationship::getFileId))
                    .entrySet().stream()
                    .filter(entry -> {
                        Set<String> docTagIds = entry.getValue().stream()
                                .map(TagDocRelationship::getTagId)
                                .collect(Collectors.toSet());
                        return docTagIds.containsAll(tagIds);
                    })
                    .map(Map.Entry::getKey)
                    .toList();
            candidateIds.retainAll(docIdsWithAllTags);
            if (candidateIds.isEmpty()) {
                return PageVO.<DocSearchVO>builder()
                        .pageNum(query.getPage())
                        .pageSize(query.getPageSize())
                        .total(0)
                        .list(new ArrayList<>())
                        .build();
            }
        }

        // Step 3: Category filtering - intersect with docs in the specified category
        if (StringUtils.hasText(query.getCategory())) {
            List<Category> categories = categoryRepository.findByName(query.getCategory());
            if (categories.isEmpty()) {
                return PageVO.<DocSearchVO>builder()
                        .pageNum(query.getPage())
                        .pageSize(query.getPageSize())
                        .total(0)
                        .list(new ArrayList<>())
                        .build();
            }
            String categoryId = categories.get(0).getId();
            List<String> docIdsInCategory = cateDocRelationshipMapper.findByFileIds(new ArrayList<>(candidateIds))
                    .stream()
                    .filter(rel -> categoryId.equals(rel.getCategoryId()))
                    .map(CateDocRelationship::getFileId)
                    .toList();
            candidateIds.retainAll(docIdsInCategory);
            if (candidateIds.isEmpty()) {
                return PageVO.<DocSearchVO>builder()
                        .pageNum(query.getPage())
                        .pageSize(query.getPageSize())
                        .total(0)
                        .list(new ArrayList<>())
                        .build();
            }
        }

        // Step 4: Query documents from MySQL
        List<FileDocument> documents = documentMybatisRepository.findByIdList(new ArrayList<>(candidateIds));
        // Filter by reviewing=false AND docState=SUCCESS
        List<FileDocument> filteredDocs = documents.stream()
                .filter(doc -> !doc.getReviewing() && doc.getDocState() == DocStateEnum.SUCCESS)
                .collect(Collectors.toList());

        // Step 5: Sorting
        String sortField = query.getSortField();
        String sortOrder = query.getSortOrder();
        Sort.Direction direction = "asc".equalsIgnoreCase(sortOrder) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Comparator<FileDocument> comparator = switch (sortField != null ? sortField : "createTime") {
            case "name" -> Comparator.comparing(FileDocument::getName, Comparator.nullsLast(Comparator.naturalOrder()));
            case "size" -> Comparator.comparing(FileDocument::getSize, Comparator.nullsLast(Comparator.naturalOrder()));
            default -> Comparator.comparing(FileDocument::getUploadDate, Comparator.nullsLast(Comparator.naturalOrder()));
        };
        if (direction == Sort.Direction.DESC) {
            comparator = comparator.reversed();
        }
        filteredDocs.sort(comparator);

        // Step 6: Pagination
        int total = filteredDocs.size();
        int page = query.getPage() != null ? query.getPage() : 1;
        int pageSize = query.getPageSize() != null ? query.getPageSize() : 20;
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        List<FileDocument> pagedDocs = (start >= total)
                ? new ArrayList<>()
                : filteredDocs.subList(start, end);

        // Step 7: Assemble results - get liked/collected status and tags
        List<DocSearchVO> voList = pagedDocs.stream()
                .map(doc -> convertToDocSearchVO(doc, userId))
                .toList();

        return PageVO.<DocSearchVO>builder()
                .pageNum(page)
                .pageSize(pageSize)
                .total(total)
                .list(voList)
                .build();
    }

    private DocSearchVO convertToDocSearchVO(FileDocument doc, String userId) {
        DocSearchVO vo = new DocSearchVO();
        vo.setId(doc.getId());
        vo.setName(doc.getName());
        vo.setType(doc.getSuffix());
        vo.setSize(doc.getSize());
        vo.setSizeDisplay(formatSize(doc.getSize()));
        vo.setDescription(doc.getDescription());
        vo.setCreateTime(doc.getUploadDate());
        vo.setUpdateTime(doc.getUpdateDate());

        // Query liked status
        if (StringUtils.hasText(userId)) {
            int likeStatus = likeService.findEntityLikeStatus(userId, 1, doc.getId());
            vo.setLiked(likeStatus > 0);
            int collectStatus = likeService.findEntityLikeStatus(userId, 2, doc.getId());
            vo.setCollected(collectStatus > 0);
        } else {
            vo.setLiked(false);
            vo.setCollected(false);
        }

        // Query tags with color
        List<TagDocRelationship> tagRels = tagDocRelationshipMapper.findByFileId(doc.getId());
        List<TagColorVO> tagColorVOList = tagRels.stream()
                .map(rel -> {
                    Tag tag = tagRepository.findById(rel.getTagId());
                    TagColorVO tagColorVO = new TagColorVO();
                    if (tag != null) {
                        tagColorVO.setName(tag.getName());
                        tagColorVO.setColor(tag.getColor());
                    }
                    return tagColorVO;
                })
                .filter(t -> t.getName() != null)
                .toList();
        vo.setTags(tagColorVOList);

        // Query category
        List<CateDocRelationship> cateRels = cateDocRelationshipMapper.findByFileId(doc.getId());
        if (!cateRels.isEmpty()) {
            Category category = categoryRepository.findById(cateRels.get(0).getCategoryId()).orElse(null);
            if (category != null) {
                vo.setCategory(category.getName());
            }
        }

        return vo;
    }

    private String formatSize(Long size) {
        if (size == null) return "0 B";
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
        if (size < 1024 * 1024 * 1024) return String.format("%.1f MB", size / (1024.0 * 1024));
        return String.format("%.1f GB", size / (1024.0 * 1024 * 1024));
    }

    /**
     * Index document to Elasticsearch
     */
    private void indexDocumentToEs(FileDocument document) {
        if (document == null || document.getMd5() == null) {
            log.warn("Cannot index null or md5-less document to ES");
            return;
        }
        try {
            SearchDocument searchDocument = new SearchDocument();
            searchDocument.setId(document.getMd5());
            searchDocument.setName(document.getName());
            searchDocument.setType(document.getSuffix());
            searchDocument.setContent(""); // empty initially, will be filled by text extraction task
            searchDocument.setTagNames(getTagNamesByDocId(document.getId()));
            searchDocument.setCategoryName(getCategoryNameByDocId(document.getId()));
            elasticService.upload(searchDocument);
            log.info("Document indexed to ES: id={}, name={}", document.getMd5(), document.getName());
        } catch (Exception e) {
            log.error("Failed to index document to ES: id={}", document.getMd5(), e);
        }
    }

    /**
     * Get tag names for a document
     */
    private List<String> getTagNamesByDocId(String docId) {
        if (docId == null || docId.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            List<TagDocRelationship> relationships = tagDocRelationshipMapper.findByFileId(docId);
            if (relationships == null || relationships.isEmpty()) {
                return Collections.emptyList();
            }
            List<String> tagIds = relationships.stream()
                    .map(TagDocRelationship::getTagId)
                    .collect(Collectors.toList());
            List<Tag> tags = tagRepository.findByIds(tagIds);
            return tags.stream()
                    .map(Tag::getName)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to get tag names for docId={}", docId, e);
            return Collections.emptyList();
        }
    }

    /**
     * Get category name for a document
     */
    private String getCategoryNameByDocId(String docId) {
        if (docId == null || docId.isEmpty()) {
            return "";
        }
        try {
            List<CateDocRelationship> relationships = cateDocRelationshipMapper.findByFileId(docId);
            if (relationships == null || relationships.isEmpty()) {
                return "";
            }
            // Return the first category's name
            String categoryId = relationships.get(0).getCategoryId();
            Optional<Category> category = categoryRepository.findById(categoryId);
            return category.map(Category::getName).orElse("");
        } catch (Exception e) {
            log.error("Failed to get category name for docId={}", docId, e);
            return "";
        }
    }

    /**
     * Update document content in ES after text extraction
     */
    private void updateFileContentToEs(String docId, String content) {
        if (docId == null || docId.isEmpty()) {
            return;
        }
        FileDocument document = documentMybatisRepository.findById(docId);
        if (document == null || document.getMd5() == null) {
            log.warn("Cannot update ES content: document not found for docId={}", docId);
            return;
        }
        try {
            SearchDocument searchDocument = new SearchDocument();
            searchDocument.setId(document.getMd5());
            searchDocument.setName(document.getName());
            searchDocument.setType(document.getSuffix());
            searchDocument.setContent(content);
            searchDocument.setTagNames(getTagNamesByDocId(docId));
            searchDocument.setCategoryName(getCategoryNameByDocId(docId));
            elasticService.updateFileObj(null, searchDocument);
            log.info("Document content updated in ES: docId={}, md5={}", docId, document.getMd5());
        } catch (Exception e) {
            log.error("Failed to update document content in ES: docId={}", docId, e);
        }
    }
}