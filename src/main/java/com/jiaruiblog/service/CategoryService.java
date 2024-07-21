package com.jiaruiblog.service;

import com.jiaruiblog.entity.CateDocRelationship;
import com.jiaruiblog.entity.Category;
import com.jiaruiblog.entity.vo.CategoryVO;
import com.jiaruiblog.util.BaseApiResult;

import java.util.List;

/**
 * @Author Jarrett Luo
 * @Date 2022/6/7 11:38
 * @Version 1.0
 */
public interface CategoryService {

    /**
     * 新增分类
     * @param category -> Category 实体
     * @return -> ApiResult
     */
    BaseApiResult insert(Category category);

    /**
     * 更新分类信息
     * @param category -> Category 实体
     * @return -> ApiResult
     */
    BaseApiResult update(Category category);

    /**
     * 移除现有的分类
     * @param category -> Category 实体
     * @return -> ApiResult
     */
    BaseApiResult remove(Category category);


    /**
     * 根据分类的各种信息进行查询
     * @param category -> Category 实体
     * @return -> ApiResult
     */
    BaseApiResult search(Category category);

    /**
     * 查询分类的列表信息
     * @return BaseApiResult
     */
    BaseApiResult list();

    /**
     * 增加分类和文档的信息
     * @param relationship CateDocRelationship
     * @return BaseApiResult
     */
    BaseApiResult addRelationShip(CateDocRelationship relationship);

    /**
     * 取消分类和文档的关联
     * @param relationship CateDocRelationship
     * @return BaseApiResult
     */
    BaseApiResult cancelCategoryRelationship(CateDocRelationship relationship);

    /**
     * @Author luojiarui
     * @Description 排查某个分类和文档是否存在关系
     * @Date 22:20 2022/11/16
     * @Param [categoryId, fileId]
     * @return boolean
     **/
    boolean relateExist(String categoryId, String fileId);

    /**
     * @Author luojiarui
     * @Description 更具文档的分类和标签、关键字进行联合查询
     * @Date 23:20 2023/1/4
     * @Param []
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    BaseApiResult getDocByTagAndCate(String cateId, String tagId, String keyword,
                                     Long pageNum, Long pageSize);

    /**
     * @Author luojiarui
     * @Description 更具文档的分类和标签、关键字进行联合查询
     * @Date 23:20 2023/1/4
     * @Param []
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    BaseApiResult getMyCollection(String cateId, String tagId, String keyword,
                                     Long pageNum, Long pageSize, String userId);

    /**
     * @Author luojiarui
     * @Description 更具文档的分类和标签、关键字进行联合查询
     * @Date 23:20 2023/1/4
     * @Param []
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    BaseApiResult getMyUploaded(String cateId, String tagId, String keyword,
                                     Long pageNum, Long pageSize, String userId);

    List<Category> getRandom();

    void addRelationShipDefault(String categoryId, String docId);

    void addRelationShipDefault(String categoryId, List<String> docIds);



    List<CateDocRelationship> getRelateByCateId(String cateId);

    long countAllFile();

    String saveOrUpdateCate(String cateName);

    void removeRelateByDocId(String docId);

    List<String> fuzzySearchDoc(String keyWord);

    Category queryById(String id);

    List<String> queryDocListByCategory(Category categoryDb);

    CategoryVO queryByDocId(String docId);

}
