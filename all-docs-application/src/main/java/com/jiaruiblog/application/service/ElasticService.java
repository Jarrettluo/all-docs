package com.jiaruiblog.application.service;

import com.jiaruiblog.domain.entity.dto.SearchQuery;
import com.jiaruiblog.domain.entity.dto.SearchResultItem;
import com.jiaruiblog.domain.entity.po.SearchDocument;
import com.jiaruiblog.domain.entity.vo.PageVO;
import com.jiaruiblog.domain.entity.vo.SearchResultVO;

import java.io.InputStream;
import java.util.List;

/**
 * @author Jarrett Luo
 * @Date 2022/6/7 11:38
 * @Version 1.0
 */
public interface ElasticService {

    /**
     * @author luojiarui
     * @Description 上传文件到es中
     * @Date 15:20 2022/11/5
     * @Param [fileObj]
     */
    void upload(SearchDocument searchDocument);

    /**
     * @author luojiarui
     * @Description 根据关键字搜索文档
     * @Date 15:20 2022/11/5
     * @Param [keyword, pageNum, pageSize]
     * @Return PageVO<String>
     */
    PageVO<String> search(String keyword, int pageNum, int pageSize);

    /**
     * @author luojiarui
     * @Description 根据多个文档id查询文档名称列表
     * @Date 15:20 2022/11/5
     * @Param [docIds]
     * @Return List<String>
     */
    List<String> queryFileNameListByIds(List<String> docIds);

    /**
     * @author luojiarui
     * @Description 根据多个文档id查询文档列表
     * @Date 15:20 2022/11/5
     * @Param [docIds]
     * @Return List<FileObj>
     */
    List<SearchDocument> queryFileObjListByIds(List<String> docIds);

    /**
     * @author luojiarui
     * @Description 根据文档id查询文档内容
     * @Date 15:20 2022/11/5
     * @Param [docId]
     * @Return String
     */
    String queryContentById(String docId);

    /**
     * @author luojiarui
     * @Description 根据关键字搜索文档并返回文档ID列表
     * @Date 15:20 2022/11/5
     * @Param [keyword]
     * @Return List<String>
     */
    List<String> searchIds(String keyword);

    /**
     * @author luojiarui
     * @Description 根据文件流上传到ES中
     * @Date 15:20 2022/11/5
     * @Param [inputStream, fileObj]
     */
    void uploadFileObj(InputStream inputStream, SearchDocument searchDocument);

    /**
     * @author luojiarui
     * @Description 根据id删除es中的文档
     * @Date 15:20 2022/11/5
     * @Param [id]
     */
    void deleteById(String id);

    /**
     * @author luojiarui
     * @Description 根据文件流更新文档
     * @Date 15:20 2022/11/5
     * @Param [inputStream, fileObj]
     */
    void updateFileObj(InputStream inputStream, SearchDocument searchDocument);

    /**
     * 获取词云统计数据
     */
    java.util.List<java.util.Map<String, Object>> getWordStat();

/**
     * 多条件文档检索
     * @param query 搜索参数
     * @return 匹配的文档ID列表
     */
    List<String> searchDocuments(SearchQuery query);

    /**
     * 多条件文档检索（带高亮）
     * @param query 搜索参数
     * @return 匹配的文档ID列表及高亮片段
     */
    java.util.List<SearchResultItem> searchDocumentsWithHighlight(SearchQuery query);

    /**
     * 全文检索 + 分页 + 多高亮片段
     * @param filterWord 关键词
     * @param tagId 标签ID（可选）
     * @param categoryId 分类ID（可选）
     * @param page 页码
     * @param rows 每页条数
     * @return SearchResultVO 含 total 和 items
     */
    SearchResultVO searchDocumentsFullText(String filterWord, String tagId, String categoryId, int page, int rows);
}