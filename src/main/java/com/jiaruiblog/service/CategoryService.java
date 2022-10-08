package com.jiaruiblog.service;

import com.jiaruiblog.entity.CateDocRelationship;
import com.jiaruiblog.entity.Category;

import com.jiaruiblog.util.BaseApiResult;

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
     * 根据分类的id进行查询
     * @param category -> Category 实体
     * @return -> ApiResult
     */
    @Deprecated
    BaseApiResult queryById(Category category);

    /**
     * 根据分类的各种信息进行查询
     * @param category -> Category 实体
     * @return -> ApiResult
     */
    BaseApiResult search(Category category);

    /**
     * 查询分类的列表信息
     * @return
     */
    BaseApiResult list();

    /**
     * 增加分类和文档的信息
     * @param relationship
     * @return
     */
    BaseApiResult addRelationShip(CateDocRelationship relationship);

    /**
     * 取消分类和文档的关联
     * @param relationship
     * @return
     */
    BaseApiResult cancleCategoryRelationship(CateDocRelationship relationship);

}
