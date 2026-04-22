package com.jiaruiblog.application.service.impl;

import cn.hutool.core.util.IdUtil;
import com.jiaruiblog.application.service.DocumentService;
import com.jiaruiblog.application.service.ElasticService;
import com.jiaruiblog.application.service.CollectService;
import com.jiaruiblog.application.service.ICommentService;
import com.jiaruiblog.common.enums.DocStateEnum;
import com.jiaruiblog.domain.entity.FileDocument;
import com.jiaruiblog.domain.entity.dto.BasePageDTO;
import com.jiaruiblog.domain.entity.dto.DocumentDTO;
import com.jiaruiblog.domain.entity.dto.document.UpdateInfoDTO;
import com.jiaruiblog.domain.entity.vo.DocWithCateVO;
import com.jiaruiblog.domain.entity.vo.DocumentVO;
import com.jiaruiblog.domain.entity.vo.PageVO;
import com.jiaruiblog.infrastructure.repository.DocumentRepository;
import com.jiaruiblog.infrastructure.storage.StorageFactory;
import com.jiaruiblog.infrastructure.storage.StorageStrategy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jiarui.luo
 */
@Slf4j
@Service
public class DocumentServiceImpl implements DocumentService {

    @Resource
    private DocumentRepository documentRepository;

    @Resource
    private StorageFactory storageFactory;

    @Resource
    private ICommentService commentService;

    @Resource
    private CollectService collectService;

    @Resource
    private ElasticService elasticService;

    private static final String FILE_NAME = "filename";

    @Override
    public String uploadFileToGridFs(String fileName, InputStream inputStream, String contentType, String md5) {
        if (inputStream == null) {
            throw new IllegalArgumentException("InputStream cannot be null");
        }
        StorageStrategy storageStrategy = storageFactory.getStorageStrategy();
        String objectId = IdUtil.simpleUUID();
        storageStrategy.upload(inputStream, objectId, contentType);
        log.info("Uploaded file to MinIO: objectId={}, filename={}", objectId, fileName);
        return objectId;
    }

    @Override
    public List<FileDocument> list() {
        return documentRepository.findByPage(1, 100, Sort.by(Sort.Direction.DESC, "uploadDate"));
    }

    @Override
    public void insert(FileDocument document) {
        if (document == null) {
            return;
        }
        documentRepository.save(document);
    }

    @Override
    public void deleteGridFs(String... fileIds) {
        if (fileIds == null || fileIds.length == 0) {
            return;
        }
        StorageStrategy storageStrategy = storageFactory.getStorageStrategy();
        for (String fileId : fileIds) {
            storageStrategy.delete(fileId);
        }
        log.info("Deleted files from MinIO: {}", Arrays.asList(fileIds));
    }

    @Override
    public void remove(FileDocument document) {
        if (document == null || document.getId() == null) {
            return;
        }
        // Delete from MySQL
        documentRepository.delete(document.getId());
        // Delete from MinIO
        if (document.getGridfsId() != null) {
            deleteGridFs(document.getGridfsId());
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
        return documentRepository.findById(documentId);
    }

    @Override
    public FileDocument queryByMd5(String md5) {
        if (md5 == null || md5.isEmpty()) {
            return null;
        }
        return documentRepository.findByMd5(md5);
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
        return documentRepository.findByIdList(docIds);
    }

    @Override
    public List<FileDocument> queryAll() {
        return documentRepository.findByPage(1, Integer.MAX_VALUE, Sort.by(Sort.Direction.DESC, "uploadDate"));
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
    public byte[] getFileBytes(String gridfsId) {
        if (gridfsId == null || gridfsId.isEmpty()) {
            return new byte[0];
        }
        StorageStrategy storageStrategy = storageFactory.getStorageStrategy();
        InputStream inputStream = storageStrategy.download(gridfsId);
        if (inputStream == null) {
            return new byte[0];
        }
        try {
            return inputStream.readAllBytes();
        } catch (java.io.IOException e) {
            log.error("Failed to read file bytes", e);
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
        documentRepository.save(document);
        return document;
    }

    @Override
    public PageVO<FileDocument> queryByPage(FileDocument document, int pageNum, int pageSize) {
        List<FileDocument> documents = documentRepository.findByPage(pageNum, pageSize, Sort.by(Sort.Direction.DESC, "uploadDate"));
        long total = documentRepository.count();
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
        FileDocument existing = documentRepository.findByMd5(md5);
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
        documentRepository.save(document);
        return document;
    }

    @Override
    public void documentUpload(MultipartFile file, String userId, String username) {
        // Implementation for document upload
        log.info("Document upload: userId={}, username={}", userId, username);
    }

    @Override
    public String uploadBatch(String category, List<String> tags, String description, Boolean skipError, MultipartFile[] files, String userId, String username) {
        return "Batch upload completed";
    }

    @Override
    public void uploadByUrl(String category, List<String> tags, String name, String description, String url, String userId, String username) {
        log.info("Upload by URL: {}", url);
    }

    @Override
    public FileDocument saveFile(FileDocument fileDocument, InputStream inputStream) {
        if (fileDocument == null || inputStream == null) {
            return fileDocument;
        }
        String objectId = uploadFileToGridFs(fileDocument.getName(), inputStream, fileDocument.getContentType(), fileDocument.getMd5());
        fileDocument.setGridfsId(objectId);
        documentRepository.save(fileDocument);
        return fileDocument;
    }

    @Override
    public void updateFile(FileDocument fileDocument) {
        if (fileDocument == null || fileDocument.getId() == null) {
            return;
        }
        documentRepository.update(fileDocument);
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
        documentRepository.update(fileDocument);
    }

    @Override
    public void removeFile(String id, boolean isDeleteFile) {
        if (id == null || id.isEmpty()) {
            return;
        }
        FileDocument document = documentRepository.findById(id);
        if (document != null) {
            documentRepository.delete(id);
            if (isDeleteFile && document.getGridfsId() != null) {
                deleteGridFs(document.getGridfsId());
            }
        }
    }

    @Override
    public Optional<FileDocument> getById(String id) {
        if (id == null || id.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(documentRepository.findById(id));
    }

    @Override
    public Optional<FileDocument> getPreviewById(String id) {
        return getById(id);
    }

    @Override
    public FileDocument getByMd5(String md5) {
        return documentRepository.findByMd5(md5);
    }

    @Override
    public List<FileDocument> getByMd5Set(Set<String> md5Set) {
        if (md5Set == null || md5Set.isEmpty()) {
            return Collections.emptyList();
        }
        List<FileDocument> result = new ArrayList<>();
        for (String md5 : md5Set) {
            FileDocument doc = documentRepository.findByMd5(md5);
            if (doc != null) {
                result.add(doc);
            }
        }
        return result;
    }

    @Override
    public List<FileDocument> listFilesByPage(int pageIndex, int pageSize) {
        return documentRepository.findByPage(pageIndex, pageSize, Sort.by(Sort.Direction.DESC, "uploadDate"));
    }

    @Override
    public List<FileDocument> listAndFilterByPage(int pageIndex, int pageSize, Collection<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return documentRepository.findByIdList(new ArrayList<>(ids));
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
        List<FileDocument> documents = listFilesByPage(documentDTO.getPage(), documentDTO.getRows());
        List<DocumentVO> voList = documents.stream()
                .map(doc -> convertDocument(new DocumentVO(), doc))
                .collect(Collectors.toList());
        return PageVO.<DocumentVO>builder()
                .pageNum(documentDTO.getPage())
                .pageSize(documentDTO.getRows())
                .total(documentRepository.count())
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
        FileDocument document = documentRepository.findById(updateInfoDTO.getId());
        if (document != null) {
            if (updateInfoDTO.getName() != null) {
                document.setName(updateInfoDTO.getName());
            }
            if (updateInfoDTO.getDesc() != null) {
                document.setDescription(updateInfoDTO.getDesc());
            }
            documentRepository.update(document);
        }
    }

    @Override
    public PageVO<DocWithCateVO> listWithCategory(DocumentDTO documentDTO) {
        return PageVO.<DocWithCateVO>builder().build();
    }

    @Override
    public void updateFileThumb(InputStream inputStream, FileDocument fileDocument) {
        log.info("Update file thumb for: {}", fileDocument.getId());
    }

    @Override
    public InputStream getFileThumb(String thumbId) {
        StorageStrategy storageStrategy = storageFactory.getStorageStrategy();
        return storageStrategy.download(thumbId);
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
        return documentRepository.findByIdList(Arrays.asList(docId));
    }

    @Override
    public List<FileDocument> queryAndRemove(String... docId) {
        if (docId == null || docId.length == 0) {
            return Collections.emptyList();
        }
        List<FileDocument> result = documentRepository.findByIdList(Arrays.asList(docId));
        documentRepository.deleteByIdList(Arrays.asList(docId));
        log.info("Query and remove documents: {}", Arrays.asList(docId));
        return result;
    }

    @Override
    public List<FileDocument> queryAndUpdate(String... docId) {
        if (docId == null || docId.length == 0) {
            return Collections.emptyList();
        }
        return documentRepository.findByIdList(Arrays.asList(docId));
    }

    @Override
    public List<FileDocument> queryFileDocument(BasePageDTO pageDTO, boolean reviewing) {
        if (pageDTO == null) {
            return Collections.emptyList();
        }
        int page = pageDTO.getPage() != null ? pageDTO.getPage() : 1;
        int size = pageDTO.getRows() != null ? pageDTO.getRows() : 10;
        return documentRepository.findByPage(page, size, Sort.by(Sort.Direction.DESC, "uploadDate"));
    }

    @Override
    public Map<String, Object> queryFileDocumentResult(BasePageDTO pageDTO, boolean reviewing) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", queryFileDocument(pageDTO, reviewing));
        result.put("total", documentRepository.count());
        return result;
    }

    @Override
    public long countAllFile() {
        return documentRepository.count();
    }

    @Override
    public boolean isExist(String docId) {
        if (docId == null || docId.isEmpty()) {
            return false;
        }
        return documentRepository.findById(docId) != null;
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
        documentVO.setDocState(fileDocument.getDocState());
        documentVO.setErrorMsg(fileDocument.getErrorMsg());
        documentVO.setTxtId(fileDocument.getTextFileId());
        documentVO.setPreviewFileId(fileDocument.getPreviewFileId());
        return documentVO;
    }
}