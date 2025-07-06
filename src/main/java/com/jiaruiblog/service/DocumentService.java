package com.jiaruiblog.service;

import com.jiaruiblog.entity.FileDocument;
import com.jiaruiblog.entity.dto.BasePageDTO;
import com.jiaruiblog.entity.dto.DocumentDTO;
import com.jiaruiblog.entity.dto.document.UpdateInfoDTO;
import com.jiaruiblog.entity.vo.DocWithCateVO;
import com.jiaruiblog.entity.vo.DocumentVO;
import com.jiaruiblog.entity.vo.PageVO;
import com.jiaruiblog.enums.DocStateEnum;
import com.jiaruiblog.task.exception.TaskRunException;
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


    void documentUpload(MultipartFile file, String userId, String username) throws AuthenticationException;

    String uploadBatch(String category, List<String> tags, String description,
                              Boolean skipError, MultipartFile[] files,
                              String userId, String username);

    void uploadByUrl(String category, List<String> tags, String name,
                              String description, String url,
                              String userId, String username);


    /**
     * 保存文件 - js文件流
     *
     * @param fileDocument FileDocument
     * @param inputStream InputStream
     * @return FileDocument FileDocument
     */
    FileDocument saveFile(FileDocument fileDocument, InputStream inputStream);

    /**
     * update file
     * @author luojiarui
     * @Description 重建索引和缩略图的时候专用的
     * @Date 18:05 2022/11/13
     * @Param fileDocument FileDocument
     **/
    void updateFile(FileDocument fileDocument);

    /**
     * @author luojiarui
     * @Description // 更新文档状态
     * @Date 15:41 2022/11/13
     * @Param [fileDocument, state]
     **/
    void updateState(FileDocument fileDocument, DocStateEnum state, String errorMsg) throws TaskRunException;

    /**
     * @author luojiarui
     * @Description // 删除GridFS系统中的文件
     * @Date 18:02 2022/11/13
     * @Param [id]
     **/
    void deleteGridFs(String ...id);

    /**
     * 删除文件
     *
     * @param id
     * @param isDeleteFile 是否删除文件
     * @return
     */
    void removeFile(String id, boolean isDeleteFile);

    /**
     * 根据id获取文件
     *
     * @param id
     * @return
     */
    Optional<FileDocument> getById(String id);

    /**
     * 根据id获取文件
     *
     * @param id
     * @return
     */
    Optional<FileDocument> getPreviewById(String id);

    /**
     * 根据md5获取文件对象
     *
     * @param md5
     * @return
     */
    FileDocument getByMd5(String md5);

    List<FileDocument> getByMd5Set(Set<String> md5Set);

    /**
     * queryById
     * @param docId String
     * @return result
     */
    FileDocument queryById(String docId);

    /**
     * 分页查询，按上传时间降序
     *
     * @param pageIndex 页数
     * @param pageSize 每页数量
     * @return List<FileDocument>
     */
    List<FileDocument> listFilesByPage(int pageIndex, int pageSize);

    /**
     * listAndFilterByPage
     * @param pageIndex int
     * @param pageSize int
     * @param ids Collection
     * @return result
     */
    List<FileDocument> listAndFilterByPage(int pageIndex, int pageSize, Collection<String> ids);

    /**
     * listAndFilterByPageNotSort
     * @param pageIndex int
     * @param pageSize int
     * @param ids List
     * @return result
     */
    List<FileDocument> listAndFilterByPageNotSort(int pageIndex, int pageSize, List<String> ids);

    /**
     * 分页检索目前的文档信息
     * @param documentDTO DocumentDTO
     * @return result
     */
    Object list(DocumentDTO documentDTO);

    /**
     * 分页检索目前的文档信息
     * @param documentDTO DocumentDTO
     * @return result
     */
    Object listNew(DocumentDTO documentDTO);

    /**
     *根据文档的详情，查询该文档的详细信息
     *
     * @param id ->Long
     * @return ApiResult
     */
    DocumentVO detail(String id);

    /**
     * 删除掉已经存在的文档
     *
     * @param fileDocument -> 文档信息
     * @return ApiResult
     */
    void remove(FileDocument fileDocument);

    /**
     * @author luojiarui
     * @Description 管理员对文档的基本信息进行修改
     * @Date 23:17 2023/6/28
     * @Param [updateInfoDTO]
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    void updateInfo(UpdateInfoDTO updateInfoDTO);


    PageVO<DocWithCateVO> listWithCategory(DocumentDTO documentDTO);

    /**
     * update file thumb
     * @param inputStream FileDocument
     * @param fileDocument InputStream
     * @throws FileNotFoundException file not found
     */
    void updateFileThumb(InputStream inputStream, FileDocument fileDocument) throws FileNotFoundException;

    /**
     * getFileThumb
     * @author luojiarui
     * @Description // 查询缩略图信息
     * @Date 8:00 下午 2022/7/24
     * @param thumbId String
     * @return java.io.InputStream
     **/
    InputStream getFileThumb(String thumbId);

    byte[] getFileBytes(String thumbId);

    /**
     * 保存文件流到dfs系统中
     **/
    String uploadFileToGridFs(String prefix, InputStream in, String contentType);

    /**
     * @author luojiarui
     * @Description // 通过文档id查询文档详情信息
     * @Date 22:28 2024/7/21
     * @Param [docId]
     * @return java.util.List<com.jiaruiblog.entity.FileDocument>
     **/
    List<FileDocument> queryByDocIds(String ...docId);

    /**
     * @author luojiarui
     * @Description 查询并删除某个文档
     * @Date 10:01 2022/12/10
     * @Param [docId]
     * @return com.jiaruiblog.entity.FileDocument
     **/
    void queryAndRemove(String ...docId);

    /**
     * @author luojiarui
     * @Description 文档查询并通过文档审批
     * @Date 10:27 2022/12/10
     * @Param [docId]
     * @return java.util.List<com.jiaruiblog.entity.FileDocument>
     **/
    List<FileDocument> queryAndUpdate(String ...docId);

    /**
     * @author luojiarui
     * @Description 查询是否在评审的文档
     * @Date 12:02 2022/12/10
     * @Param [pageDTO, reviewing]
     * @return java.util.List<com.jiaruiblog.entity.FileDocument>
     **/
    List<FileDocument> queryFileDocument(BasePageDTO pageDTO, boolean reviewing);

    /**
     * @author luojiarui
     * @Description 查询文档评审的列表, 实际是查询文档的信息
     * @Date 20:45 2022/11/30
     * @Param [page, user]
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    Map<String, Object> queryFileDocumentResult(BasePageDTO pageDTO, boolean reviewing);

    long countAllFile();

    boolean isExist(String docId);
}
