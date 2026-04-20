package com.jiaruiblog.application.service;

import com.jiaruiblog.domain.entity.FileDocument;
import com.jiaruiblog.domain.entity.dto.BasePageDTO;
import com.jiaruiblog.domain.entity.dto.DocumentDTO;
import com.jiaruiblog.domain.entity.dto.document.UpdateInfoDTO;
import com.jiaruiblog.domain.entity.vo.DocWithCateVO;
import com.jiaruiblog.domain.entity.vo.DocumentVO;
import com.jiaruiblog.domain.entity.vo.PageVO;
import com.jiaruiblog.common.enums.DocStateEnum;
import org.apache.http.auth.AuthenticationException;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

/**
 * @author jiarui.luo
 */
public interface DocumentService {

    /**
     * 保存文件 - 表单
     *
     * @param md5 文件md5
     * @param file 问价数据
     * @return FileDocument
     */
    FileDocument saveFile(String md5, MultipartFile file);

    /**
     * 用户上传文档
     */
    void documentUpload(MultipartFile file, String userId, String username) throws AuthenticationException;

    /**
     * 批量上传
     */
    String uploadBatch(String category, List<String> tags, String description,
                              Boolean skipError, MultipartFile[] files,
                              String userId, String username);

    /**
     * 通过URL上传
     */
    void uploadByUrl(String category, List<String> tags, String name,
                              String description, String url,
                              String userId, String username);

    /**
     * 保存文件 - js文件流
     */
    FileDocument saveFile(FileDocument fileDocument, InputStream inputStream);

    /**
     * update file
     */
    void updateFile(FileDocument fileDocument);

    /**
     * 更新文档状态
     */
    void updateState(FileDocument fileDocument, DocStateEnum state, String errorMsg);

    /**
     * 删除GridFS系统中的文件
     */
    void deleteGridFs(String ...id);

    /**
     * 删除文件
     */
    void removeFile(String id, boolean isDeleteFile);

    /**
     * 根据id获取文件
     */
    Optional<FileDocument> getById(String id);

    /**
     * 根据id获取预览文件
     */
    Optional<FileDocument> getPreviewById(String id);

    /**
     * 根据md5获取文件对象
     */
    FileDocument getByMd5(String md5);

    /**
     * 根据md5集合获取文件列表
     */
    List<FileDocument> getByMd5Set(Set<String> md5Set);

    /**
     * queryById
     */
    FileDocument queryById(String docId);

    /**
     * 分页查询，按上传时间降序
     */
    List<FileDocument> listFilesByPage(int pageIndex, int pageSize);

    /**
     * 分页查询并过滤
     */
    List<FileDocument> listAndFilterByPage(int pageIndex, int pageSize, Collection<String> ids);

    /**
     * 分页查询并过滤（不排序）
     */
    List<FileDocument> listAndFilterByPageNotSort(int pageIndex, int pageSize, List<String> ids);

    /**
     * 分页检索目前的文档信息
     */
    PageVO<DocumentVO> list(DocumentDTO documentDTO);

    /**
     * 分页检索目前的文档信息（新）
     */
    PageVO<DocumentVO> listNew(DocumentDTO documentDTO);

    /**
     * 根据文档的详情，查询该文档的详细信息
     */
    DocumentVO detail(String id);

    /**
     * 删除掉已经存在的文档
     */
    void remove(FileDocument fileDocument);

    /**
     * 管理员对文档的基本信息进行修改
     */
    void updateInfo(UpdateInfoDTO updateInfoDTO);

    /**
     * 根据分类查询文档列表
     */
    PageVO<DocWithCateVO> listWithCategory(DocumentDTO documentDTO);

    /**
     * 更新文件缩略图
     */
    void updateFileThumb(InputStream inputStream, FileDocument fileDocument) throws FileNotFoundException;

    /**
     * 获取文件缩略图
     */
    InputStream getFileThumb(String thumbId);

    /**
     * 获取文件字节数组
     */
    byte[] getFileBytes(String thumbId);

    /**
     * 保存文件流到dfs系统中
     */
    String uploadFileToGridFs(String prefix, InputStream in, String contentType);

    /**
     * 通过文档id查询文档详情信息
     */
    List<FileDocument> queryByDocIds(String ...docId);

    /**
     * 查询并删除某个文档
     */
    void queryAndRemove(String ...docId);

    /**
     * 查询并更新文档
     */
    List<FileDocument> queryAndUpdate(String ...docId);

    /**
     * 查询评审文档
     */
    List<FileDocument> queryFileDocument(BasePageDTO pageDTO, boolean reviewing);

    /**
     * 查询文档评审的结果
     */
    Map<String, Object> queryFileDocumentResult(BasePageDTO pageDTO, boolean reviewing);

    /**
     * 统计文档总数
     */
    long countAllFile();

    /**
     * 检查文档是否存在
     */
    boolean isExist(String docId);

    List<FileDocument> list();
    void insert(FileDocument document);
    void search(FileDocument document);
    FileDocument queryByMd5(String md5);
    List<FileDocument> queryByUserId(String userId);
    List<FileDocument> queryByDocIdList(List<String> docIds);
    List<FileDocument> queryAll();
    List<FileDocument> queryByTagId(String tagId);
    List<FileDocument> queryByCategoryId(String categoryId);
    String uploadFileToGridFs(String fileName, InputStream inputStream, String contentType, String md5);
    FileDocument insertReturnEntity(FileDocument document);
    PageVO<FileDocument> queryByPage(FileDocument document, int pageNum, int pageSize);

    /**
     * 转换文档对象为VO
     */
    DocumentVO convertDocument(DocumentVO documentVO, FileDocument fileDocument);
}
