package com.jiaruiblog.service.impl;

import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.entity.CateDocRelationship;
import com.jiaruiblog.entity.Category;
import com.jiaruiblog.service.CategoryService;
import com.jiaruiblog.utils.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

/**
 * @Author Jarrett Luo
 * @Date 2022/6/7 11:39
 * @Version 1.0
 */
@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * 新增一条分类记录
     * @param category -> Category 实体
     * @return
     */
    @Override
    public ApiResult insert(Category category) {
        log.info("=================准备插入：" + category);
        mongoTemplate.save(category, "category");
        return ApiResult.success(MessageConstant.SUCCESS);
    }

    /**
     * 更新一条已经存在的记录
     * @param category -> Category 实体
     * @return
     */
    @Override
    public ApiResult update(Category category) {
        log.info("=================准备更新：" + category);
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(category.getId()));
        Update update = new Update();
        update.set("name", category.getName());
        update.set("updateTime", category.getUpdateDate());
        mongoTemplate.updateFirst(query, update, Category.class);
        return ApiResult.success(MessageConstant.SUCCESS);
    }

    /**
     *
     * @param category -> Category 实体
     * @return
     */
    @Override
    public ApiResult remove(Category category) {
        mongoTemplate.remove(category, "category");
        return null;
    }

    @Override
    public ApiResult queryById(Category category) {
        return null;
    }

    @Override
    public ApiResult search(Category category) {
        return null;
    }

    /**
     *
     * @param relationship
     * @return
     */
    @Override
    public ApiResult addRelationShip(CateDocRelationship relationship) {
        mongoTemplate.save(relationship);
        return ApiResult.success(MessageConstant.SUCCESS);
    }

    /**
     *
     * @param relationship
     * @return
     */
    @Override
    public ApiResult cancleCategoryRelationship(CateDocRelationship relationship) {
        mongoTemplate.remove(relationship);
        return ApiResult.success(MessageConstant.SUCCESS);
    }

}
