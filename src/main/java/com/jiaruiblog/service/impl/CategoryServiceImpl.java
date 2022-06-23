package com.jiaruiblog.service.impl;

import com.jiaruiblog.common.MessageConstant;

import com.jiaruiblog.entity.CateDocRelationship;
import com.jiaruiblog.entity.Category;
import com.jiaruiblog.entity.vo.CategoryVO;
import com.jiaruiblog.service.CategoryService;
import com.jiaruiblog.service.FileDocumentService;

import com.jiaruiblog.utils.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Author Jarrett Luo
 * @Date 2022/6/7 11:39
 * @Version 1.0
 */
@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    private final static String COLLECTION_NAME = "categoryCollection";

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    FileDocumentService fileDocumentService;

    /**
     * 新增一条分类记录
     * @param category -> Category 实体
     * @return
     */
    @Override
    public ApiResult insert(Category category) {
        Query query = new Query(Criteria.where("name").is(category.getName()));
        List<Category> categories = mongoTemplate.find(query, Category.class, COLLECTION_NAME);
        if(!categories.isEmpty()) {
            ApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.PARAMS_IS_NOT_NULL);
        }
        log.info("=================准备插入：" + category);
        mongoTemplate.save(category, COLLECTION_NAME);
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
        mongoTemplate.remove(category, COLLECTION_NAME);
        // TODO 删除掉相关的分类关系
        Query query = new Query().addCriteria(new Criteria());
        mongoTemplate.remove(query, CateDocRelationship.class, COLLECTION_NAME);
        return ApiResult.success(MessageConstant.SUCCESS);
    }

    /**
     * 根据已知的种类的id反向检索文档信息
     * @param category -> Category 实体
     * @return
     */
    @Override
    public ApiResult queryById(Category category) {
        Category categoryDb = mongoTemplate.findById(category.getId(), Category.class, COLLECTION_NAME);
        if(category == null || category.getId() == null) {
            return ApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.PARAMS_IS_NOT_NULL);
        }
        Query query = new Query(Criteria.where("categoryId").is(categoryDb.getId()));
        List<CateDocRelationship> relationships = mongoTemplate.find(query, CateDocRelationship.class, COLLECTION_NAME);
//        List<Long> ids = relationships.stream().map(CateDocRelationship::getFileid).collect(Collectors.toList());
        return null;
    }

    @Override
    public ApiResult search(Category category) {
        return null;
    }

    @Override
    public ApiResult list() {
        return null;
    }

    /**
     * 增加某个文件的分类关系
     * @param relationship
     * @return
     */
    @Override
    public ApiResult addRelationShip(CateDocRelationship relationship) {
        // 先排查是否具有该链接关系，否则不予进行关联
        Query query = new Query(Criteria.where("categoryId").is(relationship.getCategoryId())
                .and("fileId").is(relationship.getFileId()));
        List<Map> result = mongoTemplate.find(query, Map.class, COLLECTION_NAME);

        if(result.isEmpty()) {
            return ApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.PARAMS_IS_NOT_NULL);
        }
        mongoTemplate.save(relationship);
        return ApiResult.success(MessageConstant.SUCCESS);
    }

    /**
     * 取消某个文件在分类下的关联关系
     * @param relationship
     * @return
     */
    @Override
    public ApiResult cancleCategoryRelationship(CateDocRelationship relationship) {
        Query query = new Query(Criteria.where("categoryId").is(relationship.getCategoryId())
                .and("fileId").is(relationship.getFileId()));
        mongoTemplate.remove(query, CateDocRelationship.class);
        return ApiResult.success(MessageConstant.SUCCESS);
    }

    /**
     * 根据category的id，查询相关连的文件id列表
     * @param categoryDb
     * @return
     */
    public List<Long> queryDocListByCategory(Category categoryDb) {
        Query query = new Query(Criteria.where("categoryId").is(categoryDb.getId()));
        List<CateDocRelationship> result = mongoTemplate.find(query, CateDocRelationship.class, COLLECTION_NAME);
        if(result.isEmpty()) {
            return null;
        }
        return result.stream().map(CateDocRelationship::getFileId).collect(Collectors.toList());
    }

    /**
     * 根据分类的id查询分类信息
     * @param id
     * @return
     */
    public Category queryById(Long id) {
        return mongoTemplate.findById(id, Category.class, COLLECTION_NAME);
    }

    /**
     * @Author luojiarui
     * @Description //根据文档的信息返回分类信息
     * @Date 10:52 下午 2022/6/22
     * @Param [docId]
     * @return com.jiaruiblog.entity.Category
     **/
    public CategoryVO queryByDocId(Long docId) {

        Query query1 = new Query().addCriteria(Criteria.where("docId").is(docId));
        CateDocRelationship relationship = mongoTemplate.findOne(query1, CateDocRelationship.class, COLLECTION_NAME);
        Category category = mongoTemplate.findById(relationship.getCategoryId(), Category.class, COLLECTION_NAME);

        relationship = Optional.ofNullable(relationship).orElse(new CateDocRelationship());
        category = Optional.ofNullable(category).orElse(new Category());

        CategoryVO categoryVO = new CategoryVO();
        categoryVO.setId(category.getId());
        categoryVO.setName(category.getName());
        categoryVO.setRelationShipId(relationship.getId());
        return categoryVO;
    }

    /**
     * 根据关键字模糊搜索相关的文档id
     * @param keyWord 关键字
     * @return 文档的id信息
     */
    public List<Long> fuzzySearchDoc(String keyWord) {
        if(keyWord == null || "".equalsIgnoreCase(keyWord)) {
            return null;
        }
        Pattern pattern = Pattern.compile("^.*"+keyWord+".*$", Pattern.CASE_INSENSITIVE);
        Query query = new Query();
        query.addCriteria(Criteria.where("name").regex(pattern));

        List<Category> categories = mongoTemplate.find(query, Category.class, COLLECTION_NAME);
        List<Long> ids = categories.stream().map(Category::getId).collect(Collectors.toList());

        Query query1 = new Query().addCriteria(Criteria.where("cateId").in(ids));
        List<CateDocRelationship> relationships = mongoTemplate.find(query, CateDocRelationship.class, COLLECTION_NAME);
        return relationships.stream().map(CateDocRelationship::getFileId).collect(Collectors.toList());
    }

}
