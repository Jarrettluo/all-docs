package com.jiaruiblog.application.service.impl;

import com.jiaruiblog.application.service.DocumentService;
import com.jiaruiblog.domain.entity.FileDocument;
import com.jiaruiblog.domain.entity.dto.BasePageDTO;
import com.jiaruiblog.domain.entity.dto.DocumentDTO;
import com.jiaruiblog.domain.entity.dto.document.UpdateInfoDTO;
import com.jiaruiblog.domain.entity.vo.DocWithCateVO;
import com.jiaruiblog.domain.entity.vo.DocumentVO;
import com.jiaruiblog.domain.entity.vo.PageVO;
import com.jiaruiblog.common.enums.DocStateEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

/**
 * @author jiarui.luo
 */
@Slf4j
@Service
public class DocumentServiceImpl implements DocumentService {

    @jakarta.annotation.Resource
    private com.jiaruiblog.application.service.ICommentService commentService;

    @jakarta.annotation.Resource
    private com.jiaruiblog.application.service.CollectService collectService;

    @Override
    public String uploadFileToGridFs(String fileName, InputStream inputStream, String contentType, String md5) {
        return "";
    }

    @Override
    public List<FileDocument> list() {
        return List.of();
    }

    @Override
    public void insert(FileDocument document) {
    }

    @Override
    public void deleteGridFs(String... fileIds) {
    }

    @Override
    public void remove(FileDocument document) {
    }

    @Override
    public void search(FileDocument document) {
    }

    @Override
    public FileDocument queryById(String documentId) {
        return null;
    }

    @Override
    public FileDocument queryByMd5(String md5) {
        return null;
    }

    @Override
    public List<FileDocument> queryByUserId(String userId) {
        return List.of();
    }

    @Override
    public List<FileDocument> queryByDocIdList(List<String> docIds) {
        return List.of();
    }

    @Override
    public List<FileDocument> queryAll() {
        return List.of();
    }

    @Override
    public List<FileDocument> queryByTagId(String tagId) {
        return List.of();
    }

    @Override
    public List<FileDocument> queryByCategoryId(String categoryId) {
        return List.of();
    }

    @Override
    public byte[] getFileBytes(String gridfsId) {
        return new byte[0];
    }

    @Override
    public FileDocument insertReturnEntity(FileDocument document) {
        return document;
    }

    @Override
    public PageVO<FileDocument> queryByPage(FileDocument document, int pageNum, int pageSize) {
        return PageVO.<FileDocument>builder().build();
    }

    @Override
    public FileDocument saveFile(String md5, MultipartFile file) {
        return new FileDocument();
    }

    @Override
    public void documentUpload(MultipartFile file, String userId, String username) {
    }

    @Override
    public String uploadBatch(String category, List<String> tags, String description, Boolean skipError, MultipartFile[] files, String userId, String username) {
        return "";
    }

    @Override
    public void uploadByUrl(String category, List<String> tags, String name, String description, String url, String userId, String username) {
    }

    @Override
    public FileDocument saveFile(FileDocument fileDocument, InputStream inputStream) {
        return fileDocument;
    }

    @Override
    public void updateFile(FileDocument fileDocument) {
    }

    @Override
    public void updateState(FileDocument fileDocument, DocStateEnum state, String errorMsg) {
    }

    @Override
    public void removeFile(String id, boolean isDeleteFile) {
    }

    @Override
    public Optional<FileDocument> getById(String id) {
        return Optional.empty();
    }

    @Override
    public Optional<FileDocument> getPreviewById(String id) {
        return Optional.empty();
    }

    @Override
    public FileDocument getByMd5(String md5) {
        return null;
    }

    @Override
    public List<FileDocument> getByMd5Set(Set<String> md5Set) {
        return List.of();
    }

    @Override
    public List<FileDocument> listFilesByPage(int pageIndex, int pageSize) {
        return List.of();
    }

    @Override
    public List<FileDocument> listAndFilterByPage(int pageIndex, int pageSize, Collection<String> ids) {
        return List.of();
    }

    @Override
    public List<FileDocument> listAndFilterByPageNotSort(int pageIndex, int pageSize, List<String> ids) {
        return List.of();
    }

    @Override
    public PageVO<DocumentVO> list(DocumentDTO documentDTO) {
        return PageVO.<DocumentVO>builder().build();
    }

    @Override
    public PageVO<DocumentVO> listNew(DocumentDTO documentDTO) {
        return PageVO.<DocumentVO>builder().build();
    }

    @Override
    public DocumentVO detail(String id) {
        return new DocumentVO();
    }

    @Override
    public void updateInfo(UpdateInfoDTO updateInfoDTO) {
    }

    @Override
    public PageVO<DocWithCateVO> listWithCategory(DocumentDTO documentDTO) {
        return PageVO.<DocWithCateVO>builder().build();
    }

    @Override
    public void updateFileThumb(InputStream inputStream, FileDocument fileDocument) {
    }

    @Override
    public InputStream getFileThumb(String thumbId) {
        return null;
    }

    @Override
    public String uploadFileToGridFs(String prefix, InputStream in, String contentType) {
        return "";
    }

    @Override
    public List<FileDocument> queryByDocIds(String... docId) {
        return List.of();
    }

    @Override
    public void queryAndRemove(String... docId) {
    }

    @Override
    public List<FileDocument> queryAndUpdate(String... docId) {
        return List.of();
    }

    @Override
    public List<FileDocument> queryFileDocument(BasePageDTO pageDTO, boolean reviewing) {
        return List.of();
    }

    @Override
    public Map<String, Object> queryFileDocumentResult(BasePageDTO pageDTO, boolean reviewing) {
        return new HashMap<>();
    }

    @Override
    public long countAllFile() {
        return 0;
    }

    @Override
    public boolean isExist(String docId) {
        return false;
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
