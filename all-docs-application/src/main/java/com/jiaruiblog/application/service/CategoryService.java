package com.jiaruiblog.application.service;

import com.jiaruiblog.domain.entity.po.CateDocRelationship;
import com.jiaruiblog.domain.entity.po.Category;
import com.jiaruiblog.domain.entity.dto.FileDocumentDTO;
import com.jiaruiblog.domain.entity.vo.CateOrTagVO;
import com.jiaruiblog.domain.entity.vo.CategoryVO;
import com.jiaruiblog.domain.entity.vo.PageVO;

import java.util.List;

/**
 * @author Jarrett Luo
 * @Date 2022/6/7 11:38
 * @Version 1.0
 */
public interface CategoryService {

    /**
     * 新增分类
     * @param category -> Category 实体
     */
    void insert(Category category);

    /**
     * 更新分类信息
     * @param category -> Category 实体
     */
    void update(Category category);

    String saveOrUpdateCate(String cateName);


    /**
     * 移除现有的分类
     * @param category -> Category 实体
     */
    void remove(Category category);


    /**
     * 根据分类的各种信息进行查询
     * @param category -> Category 实体
     */
    void search(Category category);

    /**
     * 查询分类的列表信息
     * @return BaseApiResult
     */
    List<CateOrTagVO> list();

    /**
     * 增加分类和文档的信息
     * @param relationship CateDocRelationship
     */
    void addRelationShip(CateDocRelationship relationship);

    /**
     * 取消分类和文档的关联
     * @param relationship CateDocRelationship
     */
    void cancelCategoryRelationship(CateDocRelationship relationship);

    /**
     * @author luojiarui
     * @Description 排查某个分类和文档是否存在关系
     * @Date 22:20 2022/11/16
     * @Param [categoryId, fileId]
     * @return boolean
     **/
    boolean relateExist(String categoryId, String fileId);

    /**
     * @author luojiarui
     * @Description 更具文档的分类和标签、关键字进行联合查询
     * @Date 23:20 2023/1/4
     * @Param []
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    PageVO<FileDocumentDTO> getDocByTagAndCate(String cateId, String tagId, String keyword,
                                           Long pageNum, Long pageSize);

    /**
     * @author luojiarui
     * @Description 更具文档的分类和标签、关键字进行联合查询
     * @Date 23:20 2023/1/4
     * @Param []
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    PageVO<FileDocumentDTO>  getMyCollection(String cateId, String tagId, String keyword,
                                     Long pageNum, Long pageSize, String userId);

    /**
     * @author luojiarui
     * @Description 更具文档的分类和标签、关键字进行联合查询
     * @Date 23:20 2023/1/4
     * @Param []
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    PageVO<FileDocumentDTO> getMyUploaded(String cateId, String tagId, String keyword,
                                          Long pageNum, Long pageSize, String userId);

    List<Category> getRandom();

    void addRelationShipDefault(String categoryId, String docId);

    void addRelationShipDefault(String categoryId, List<String> docIds);



    List<CateDocRelationship> getRelateByCateId(String cateId);

    long countAllFile();


    void removeRelateByDocId(String docId);

    List<String> fuzzySearchDoc(String keyWord);

    Category queryById(String id);

    List<String> queryDocListByCategory(Category categoryDb);

    CategoryVO queryByDocId(String docId);

}