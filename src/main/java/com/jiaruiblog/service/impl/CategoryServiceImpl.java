package com.jiaruiblog.service.impl;

import com.jiaruiblog.common.MessageConstant;

import com.jiaruiblog.entity.CateDocRelationship;
import com.jiaruiblog.entity.Category;

import com.jiaruiblog.entity.vo.CategoryVO;
import com.jiaruiblog.service.CategoryService;
import com.jiaruiblog.util.BaseApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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

    private final static String RELATE_COLLECTION_NAME = "relateCateCollection";

    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * 新增一条分类记录
     * @param category -> Category 实体
     * @return
     */
    @Override
    public BaseApiResult insert(Category category) {
        if(isNameExist(category.getName())) {
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
        mongoTemplate.save(category, COLLECTION_NAME);
        return BaseApiResult.success(MessageConstant.SUCCESS);
    }

    /**
     * 更新一条已经存在的记录
     * @param category -> Category 实体
     * @return
     */
    @Override
    public BaseApiResult update(Category category) {
        if(isNameExist(category.getName())) {
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(category.getId()));
        Update update = new Update();
        update.set("name", category.getName());
        update.set("updateTime", category.getUpdateDate());
        mongoTemplate.updateFirst(query, update, Category.class, COLLECTION_NAME);
        return BaseApiResult.success(MessageConstant.SUCCESS);
    }

    /**
     * @Author luojiarui
     * @Description // 判断该名字是否存在，如果是存在的则返回true，否则返回false
     * @Date 11:47 上午 2022/6/25
     * @Param [name]
     * @return boolean
     **/
    private boolean isNameExist(String name) {
        Query query = new Query(Criteria.where("name").is(name));
        List<Category> categories = mongoTemplate.find(query, Category.class, COLLECTION_NAME);
        if( categories.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     *
     * @param category -> Category 实体
     * @return
     */
    @Override
    public BaseApiResult remove(Category category) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(category.getId()));
        mongoTemplate.remove(query, Category.class, COLLECTION_NAME);
        // 删除掉相关的分类关系
        Query query1 = new Query().addCriteria(Criteria.where("categoryId").is(category.getId()));
        mongoTemplate.remove(query1, CateDocRelationship.class, RELATE_COLLECTION_NAME);
        return BaseApiResult.success(MessageConstant.SUCCESS);
    }


    @Override
    public BaseApiResult search(Category category) {
        return null;
    }

    @Override
    public BaseApiResult list() {
        // 需要查询全部的信息
        Query query = new Query().with(Sort.by(Sort.Direction.DESC, "uploadDate"));
        List<Category> categories = mongoTemplate.find(query, Category.class, COLLECTION_NAME);
        return BaseApiResult.success(categories);
    }

    /**
     * 增加某个文件的分类关系
     * @param relationship
     * @return
     */
    @Override
    public BaseApiResult addRelationShip(CateDocRelationship relationship) {
        if(relationship.getCategoryId() == null || relationship.getFileId() == null){
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
        // 先排查一个文章只能有一个分类关系，不能有多个分类信息
        Query query1 = new Query(Criteria.where("fileId").is(relationship.getFileId()));
        List<CateDocRelationship> relationships = mongoTemplate.find(query1, CateDocRelationship.class,
                RELATE_COLLECTION_NAME);
        if( !CollectionUtils.isEmpty(relationships)) {
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }

        // 先排查是否具有该链接关系，否则不予进行关联
        Query query = new Query(Criteria.where("categoryId").is(relationship.getCategoryId())
                .and("fileId").is(relationship.getFileId()));
        List<Map> result = mongoTemplate.find(query, Map.class, RELATE_COLLECTION_NAME);

        if(!result.isEmpty()) {
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.PARAMS_IS_NOT_NULL);
        }
        mongoTemplate.save(relationship, RELATE_COLLECTION_NAME);
        return BaseApiResult.success(MessageConstant.SUCCESS);
    }

    /**
     * 取消某个文件在分类下的关联关系
     * @param relationship
     * @return
     */
    @Override
    public BaseApiResult cancleCategoryRelationship(CateDocRelationship relationship) {
        Query query = new Query(Criteria.where("categoryId").is(relationship.getCategoryId())
                .and("fileId").is(relationship.getFileId()));
        mongoTemplate.remove(query, CateDocRelationship.class, RELATE_COLLECTION_NAME);
        return BaseApiResult.success(MessageConstant.SUCCESS);
    }

    /**
     * 根据category的id，查询相关连的文件id列表
     * @param categoryDb
     * @return
     */
    public List<String> queryDocListByCategory(Category categoryDb) {
        Query query = new Query(Criteria.where("categoryId").is(categoryDb.getId()));
        List<CateDocRelationship> result = mongoTemplate.find(query, CateDocRelationship.class, RELATE_COLLECTION_NAME);
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
    public Category queryById(String id) {
        if(id == null || "".equals(id)) {
            return null;
        }
        return mongoTemplate.findById(id, Category.class, COLLECTION_NAME);
    }

    /**
     * @Author luojiarui
     * @Description //根据文档的信息返回分类信息
     * @Date 10:52 下午 2022/6/22
     * @Param [docId]
     * @return com.jiaruiblog.entity.Category
     **/
    public CategoryVO queryByDocId(String docId) {

        Query query1 = new Query().addCriteria(Criteria.where("fileId").is(docId));
        CateDocRelationship relationship = mongoTemplate.findOne(query1, CateDocRelationship.class, RELATE_COLLECTION_NAME);

        if(relationship == null || relationship.getCategoryId()==null) {
            return null;
        }
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
    public List<String> fuzzySearchDoc(String keyWord) {
        if(keyWord == null || "".equalsIgnoreCase(keyWord)) {
            return null;
        }
        Pattern pattern = Pattern.compile("^.*"+keyWord+".*$", Pattern.CASE_INSENSITIVE);
        Query query = new Query();
        query.addCriteria(Criteria.where("name").regex(pattern));
        List<Category> categories = mongoTemplate.find(query, Category.class, COLLECTION_NAME);

        List<String> ids = categories.stream().map(Category::getId).collect(Collectors.toList());
        Query query1 = new Query().addCriteria(Criteria.where("categoryId").in(ids));
        List<CateDocRelationship> relationships = mongoTemplate.find(query1, CateDocRelationship.class, RELATE_COLLECTION_NAME);

        return relationships.stream().map(CateDocRelationship::getFileId).collect(Collectors.toList());
    }

    /**
     * @Author luojiarui
     * @Description // 根据文档的id进行分类和文档的关系删除
     * @Date 11:20 上午 2022/6/25
     * @Param [docId]
     * @return void
     **/
    public void removeRelateByDocId(String docId) {
        Query query = new Query(Criteria.where("docId").is(docId));
        List<CateDocRelationship> relationships = mongoTemplate.find(query, CateDocRelationship.class,
                RELATE_COLLECTION_NAME);
        relationships.forEach(item -> this.cancleCategoryRelationship(item));
    }

    /**
     * @Author luojiarui
     * @Description //热度随机产生
     * @Date 4:58 下午 2022/6/26
     * @Param []
     * @return java.util.List<com.jiaruiblog.entity.Category>
     **/
    public List<Category> getRandom() {
        Integer pageIndex = 1;
        Integer pageSize = 3;
        Query query = new Query().with(Sort.by(Sort.Direction.DESC, "uploadDate"));
        long skip = (pageIndex - 1) * pageSize;
        query.skip(skip);
        query.limit(pageSize);
        List<Category> files = mongoTemplate.find(query, Category.class, COLLECTION_NAME);
        return files;
    }

    /**
     * @Author luojiarui
     * @Description // 根据总类查询关系
     * @Date 5:00 下午 2022/6/26
     * @Param [cateId]
     * @return java.util.List<com.jiaruiblog.entity.CateDocRelationship>
     **/
    public List<CateDocRelationship> getRelateByCateId(String cateId) {
        Integer pageIndex = 0;
        Integer pageSize = 7;
        Query query = new Query().with(Sort.by(Sort.Direction.DESC, "uploadDate"));
        long skip = (pageIndex - 1) * pageSize;
        query.skip(skip);
        query.limit(pageSize);
        query.addCriteria(Criteria.where("categoryId").is(cateId));
        return mongoTemplate.find(query, CateDocRelationship.class, RELATE_COLLECTION_NAME);
    }

    /**
     * @Author luojiarui
     * @Description // 统计总数
     * @Date 4:40 下午 2022/6/26
     * @Param []
     * @return java.lang.Integer
     **/
    public long countAllFile() {
        return mongoTemplate.getCollection(COLLECTION_NAME).estimatedDocumentCount();
    }

}
