package com.jiaruiblog.service.impl;

import com.jiaruiblog.entity.CateDocRelationship;
import com.jiaruiblog.entity.Category;
import com.jiaruiblog.entity.dto.FileDocumentDTO;
import com.jiaruiblog.entity.vo.CateOrTagVO;
import com.jiaruiblog.entity.vo.CategoryVO;
import com.jiaruiblog.entity.vo.PageVO;
import com.jiaruiblog.enums.RedisActionEnum;
import com.jiaruiblog.exception.BusinessExceptionBuilder;
import com.jiaruiblog.exception.ErrorCode;
import com.jiaruiblog.service.CategoryService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ConvertOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Jarrett Luo
 * @Date 2022/6/7 11:39
 * @Version 1.0
 */
@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    private static final String COLLECTION_NAME = "categoryCollection";

    private static final String RELATE_COLLECTION_NAME = "relateCateCollection";

    private static final String CATEGORY_ID = "categoryId";

    private static final String UPDATE_DATE = "uploadDate";

    private static final String FILE_ID = "fileId";
    public static final String DOC_ID = "docId";

    @Resource
    MongoTemplate mongoTemplate;

    /**
     * 新增一条分类记录
     * 这里需要考虑并发插入的事务问题
     *
     * @param category -> Category 实体
     */
    @Override
    public void insert(Category category) {
        if (!isNameExist(category.getName()).isEmpty()) {
            throw BusinessExceptionBuilder.of(ErrorCode.OPERATE_FAILED).build();
        }
        mongoTemplate.save(category, COLLECTION_NAME);
    }

    /**
     * 更新一条已经存在的记录
     *
     * @param category -> Category 实体
     */
    @Override
    public void update(Category category) {
        if (isNameExist(category.getName()).isEmpty()) {
            throw BusinessExceptionBuilder.of(ErrorCode.CATEGORY_NOT_FOUND).build();
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(category.getId()));
        Update update = new Update();
        update.set("name", category.getName());
        update.set("updateTime", category.getUpdateDate());
        mongoTemplate.updateFirst(query, update, Category.class, COLLECTION_NAME);
        // TODO
        // 异步更新该分类下的文本信息，避免出现已经被删除的文档还放在该分类中
        // 联合查询关系表和文档表；如果类型下的文档是存在的，则返回true，否则进行删除分类下的文档信息
    }

    /**
     * 有就返回分类的id；没有的话就新增后返回id
     *
     * @return java.lang.String
     * @author luojiarui
     **/
    @Override
    public String saveOrUpdateCate(String cateName) {
        if (!StringUtils.hasText(cateName)) {
            return null;
        }
        List<Category> nameExist = isNameExist(cateName);
        if (nameExist.isEmpty()) {
            Category category = new Category();
            category.setUpdateDate(new Date());
            category.setCreateDate(new Date());
            category.setName(cateName);
            Category save = mongoTemplate.save(category, COLLECTION_NAME);
            return save.getId();
        } else {
            return Optional.ofNullable(nameExist.get(0))
                    .flatMap(category -> Optional.of(category.getId()))
                    .orElse(null);
        }
    }

    /**
     * 判断该名字是否存在，如果是存在的则返回true，否则返回false
     *
     * @return boolean
     * @author luojiarui
     **/
    private List<Category> isNameExist(String name) {
        Query query = new Query(Criteria.where("name").is(name));
        List<Category> categories = mongoTemplate.find(query, Category.class, COLLECTION_NAME);
        return Optional.of(categories).orElse(new ArrayList<>());
    }

    /**
     * @param category -> Category 实体
     */
    @Override
    public void remove(Category category) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(category.getId()));
        mongoTemplate.remove(query, Category.class, COLLECTION_NAME);
        // 删除掉相关的分类关系
        Query query1 = new Query().addCriteria(Criteria.where(CATEGORY_ID).is(category.getId()));
        mongoTemplate.remove(query1, CateDocRelationship.class, RELATE_COLLECTION_NAME);
    }


    @Override
    public void search(Category category) {
        // TODO 待开发
//        return null;
    }

    @Override
    public List<CateOrTagVO> list() {
        // 需要查询全部的信息
        Aggregation aggregation = Aggregation.newAggregation(
                // 选择某些字段
                Aggregation.project("id", "name", "createDate", "updateDate")
                        .and(ConvertOperators.Convert.convertValue("$_id").to("string"))//将主键Id转换为objectId
                        .as("id"),//新字段名称,
                Aggregation.lookup(RELATE_COLLECTION_NAME, "id", "categoryId", "abc"),
                Aggregation.project("id", "name", "createDate", "updateDate")
                        .and("abc")
                        .size()
                        .as("num"),
                Aggregation.sort(Sort.Direction.ASC, "updateDate")
        );

        AggregationResults<CateOrTagVO> result = mongoTemplate.aggregate(
                aggregation, COLLECTION_NAME, CateOrTagVO.class);
        return result.getMappedResults();
    }

    /**
     * 增加某个文件的分类关系
     *
     * @param relationship -> CateDocRelationship
     */
    @Override
    public void addRelationShip(CateDocRelationship relationship) {
        if (relationship.getCategoryId() == null || relationship.getFileId() == null) {
            throw BusinessExceptionBuilder.of(ErrorCode.PARAMS_ERROR).build();
        }
        // 先排查一个文章只能有一个分类关系，不能有多个分类信息
        Query query1 = new Query(Criteria.where(FILE_ID).is(relationship.getFileId()));
        List<CateDocRelationship> relationships = mongoTemplate.find(query1, CateDocRelationship.class,
                RELATE_COLLECTION_NAME);
        if (!CollectionUtils.isEmpty(relationships)) {
            throw BusinessExceptionBuilder.of(ErrorCode.PARAMS_ERROR).build();
        }

        // 先排查是否具有该链接关系，否则不予进行关联
        Query query = new Query(Criteria.where(CATEGORY_ID).is(relationship.getCategoryId())
                .and(FILE_ID).is(relationship.getFileId()));
        List<CateDocRelationship> result = mongoTemplate.find(query, CateDocRelationship.class, RELATE_COLLECTION_NAME);

        if (!result.isEmpty()) {
            throw BusinessExceptionBuilder.of(ErrorCode.OPERATE_FAILED).build();
        }
        mongoTemplate.save(relationship, RELATE_COLLECTION_NAME);
    }

    private void addDocRelate(CateDocRelationship relationship) {
        if (relationship.getCategoryId() == null || relationship.getFileId() == null) {
            throw BusinessExceptionBuilder.of(ErrorCode.PARAMS_ERROR).build();
        }
        // 先排查一个文章只能有一个分类关系，不能有多个分类信息
        Query query1 = new Query(Criteria.where(FILE_ID).is(relationship.getFileId()));
        List<CateDocRelationship> relationships = mongoTemplate.find(query1, CateDocRelationship.class,
                RELATE_COLLECTION_NAME);
        if (!CollectionUtils.isEmpty(relationships)) {
            throw BusinessExceptionBuilder.of(ErrorCode.PARAMS_ERROR).build();
        }

        // 先排查是否具有该链接关系，否则不予进行关联
        Query query = new Query(Criteria.where(CATEGORY_ID).is(relationship.getCategoryId())
                .and(FILE_ID).is(relationship.getFileId()));
        List<CateDocRelationship> result = mongoTemplate.find(query, CateDocRelationship.class, RELATE_COLLECTION_NAME);

        if (!result.isEmpty()) {
            throw BusinessExceptionBuilder.of(ErrorCode.PARAMS_ERROR).build();
        }
        mongoTemplate.save(relationship, RELATE_COLLECTION_NAME);
    }

    @Async
    @Override
    public void addRelationShipDefault(String categoryId, String docId) {
        if (categoryId == null) {
            return;
        }
        CateDocRelationship relationship = new CateDocRelationship();
        relationship.setCategoryId(categoryId);
        relationship.setCreateDate(new Date());
        relationship.setFileId(docId);
        relationship.setUpdateDate(new Date());
        addDocRelate(relationship);
    }

    @Override
    public void addRelationShipDefault(String categoryId, List<String> docIds) {
        for (String docId : docIds) {
            addRelationShipDefault(categoryId, docId);
        }
    }

    /**
     * 取消某个文件在分类下的关联关系
     *
     * @param relationship -> CateDocRelationship
     */
    @Override
    public void cancelCategoryRelationship(CateDocRelationship relationship) {
        Query query = new Query(Criteria.where(CATEGORY_ID).is(relationship.getCategoryId())
                .and(FILE_ID).is(relationship.getFileId()));
        mongoTemplate.remove(query, CateDocRelationship.class, RELATE_COLLECTION_NAME);
    }

    /**
     * 根据category的id，查询相关连的文件id列表
     *
     * @param categoryDb -> Category
     * @return -> List<String>
     */
    @Override
    public List<String> queryDocListByCategory(Category categoryDb) {
        Query query = new Query(Criteria.where(CATEGORY_ID).is(categoryDb.getId()));
        List<CateDocRelationship> result = mongoTemplate.find(query, CateDocRelationship.class, RELATE_COLLECTION_NAME);
        if (result.isEmpty()) {
            return Lists.newArrayList();
        }
        return result.stream().map(CateDocRelationship::getFileId).collect(Collectors.toList());
    }

    /**
     * 根据分类的id查询分类信息
     *
     * @param id -> String
     * @return -> Category
     */
    @Override
    public Category queryById(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        return mongoTemplate.findById(id, Category.class, COLLECTION_NAME);
    }

    /**
     * @return com.jiaruiblog.entity.Category
     * @author luojiarui
     * 根据文档的信息返回分类信息
     **/
    @Override
    public CategoryVO queryByDocId(String docId) {

        Query query1 = new Query().addCriteria(Criteria.where(FILE_ID).is(docId));
        CateDocRelationship relationship = mongoTemplate.findOne(query1, CateDocRelationship.class, RELATE_COLLECTION_NAME);

        if (relationship == null || relationship.getCategoryId() == null) {
            throw BusinessExceptionBuilder.of(ErrorCode.OPERATE_FAILED).build();
        }
        Category category = mongoTemplate.findById(relationship.getCategoryId(), Category.class, COLLECTION_NAME);
        category = Optional.ofNullable(category).orElse(new Category());

        CategoryVO categoryVO = new CategoryVO();
        categoryVO.setId(category.getId());
        categoryVO.setName(category.getName());
        categoryVO.setRelationShipId(relationship.getId());
        return categoryVO;
    }

    /**
     * 根据关键字模糊搜索相关的文档id
     *
     * @param keyWord 关键字
     * @return 文档的id信息
     */
    @Override
    public List<String> fuzzySearchDoc(String keyWord) {
        if (!StringUtils.hasText(keyWord)) {
            return Lists.newArrayList();
        }
        Pattern pattern = Pattern.compile("^.*" + keyWord + ".*$", Pattern.CASE_INSENSITIVE);
        Query query = new Query();
        query.addCriteria(Criteria.where("name").regex(pattern));
        List<Category> categories = mongoTemplate.find(query, Category.class, COLLECTION_NAME);

        List<String> ids = categories.stream().map(Category::getId).collect(Collectors.toList());
        Query query1 = new Query().addCriteria(Criteria.where(CATEGORY_ID).in(ids));
        List<CateDocRelationship> relationships = mongoTemplate.find(query1, CateDocRelationship.class, RELATE_COLLECTION_NAME);

        return relationships.stream().map(CateDocRelationship::getFileId).collect(Collectors.toList());
    }

    /**
     * @author luojiarui
     * 根据文档的id进行分类和文档的关系删除，这里文档的id是fileId
     **/
    @Override
    public void removeRelateByDocId(String docId) {
        Query query = new Query(Criteria.where("fileId").is(docId));
        // 根据文档id进行文档关系的删除
        mongoTemplate.remove(query, CateDocRelationship.class, RELATE_COLLECTION_NAME);
    }

    /**
     * @return java.util.List<com.jiaruiblog.entity.Category>
     * @author luojiarui
     * 热度随机产生22/6/26
     **/
    @Override
    public List<Category> getRandom() {
        int pageIndex = 1;
        int pageSize = 3;
        Query query = new Query().with(Sort.by(Sort.Direction.DESC, UPDATE_DATE));
        long skip = 0;
        query.skip(skip);
        query.limit(pageSize);
        return mongoTemplate.find(query, Category.class, COLLECTION_NAME);
    }

    /**
     * @return java.util.List<com.jiaruiblog.entity.CateDocRelationship>
     * @author luojiarui
     * 根据总类查询关系
     **/
    @Override
    public List<CateDocRelationship> getRelateByCateId(String cateId) {
        long pageIndex = 0;
        int pageSize = 7;
        Query query = new Query().with(Sort.by(Sort.Direction.DESC, UPDATE_DATE));
        long skip = (pageIndex - 1) * pageSize;
        query.skip(skip);
        query.limit(pageSize);
        query.addCriteria(Criteria.where(CATEGORY_ID).is(cateId));
        return mongoTemplate.find(query, CateDocRelationship.class, RELATE_COLLECTION_NAME);
    }

    /**
     * @return java.lang.Integer
     * @author luojiarui
     * 统计总数
     **/
    @Override
    public long countAllFile() {
        return mongoTemplate.getCollection(COLLECTION_NAME).estimatedDocumentCount();
    }

    /**
     * @return boolean
     * @author luojiarui
     * 某个分类和文档是否存在关系
     **/
    @Override
    public boolean relateExist(String categoryId, String fileId) {
        // 先排查是否具有该链接关系，否则不予进行关联
        Query query = new Query(Criteria.where(CATEGORY_ID).is(categoryId)
                .and(FILE_ID).is(fileId));
        List<CateDocRelationship> result = mongoTemplate.find(query, CateDocRelationship.class, RELATE_COLLECTION_NAME);
        return !CollectionUtils.isEmpty(result);
    }

    /**
     * @return com.jiaruiblog.util.BaseApiResult
     * @author luojiarui
     * 根据分类id， 标签id，搜索内容联合查询文档
     **/
    @Override
    public PageVO<FileDocumentDTO> getDocByTagAndCate(String cateId, String tagId, String keyword, Long pageNum, Long pageSize) {
        Criteria criteria = new Criteria();
        if (StringUtils.hasText(cateId) && StringUtils.hasText(tagId)) {
            criteria = Criteria.where("abc.categoryId").is(cateId).and("xyz.tagId").is(tagId);
        } else if (StringUtils.hasText(cateId) && !StringUtils.hasText(tagId)) {
            criteria = Criteria.where("abc.categoryId").is(cateId);
        } else if (StringUtils.hasText(tagId) && !StringUtils.hasText(cateId)) {
            criteria = Criteria.where("xyz.tagId").is(tagId);
        }


        if (StringUtils.hasText(keyword)) {
            criteria.andOperator(Criteria.where("name").regex(Pattern.compile(keyword, Pattern.CASE_INSENSITIVE)));
        }


        Aggregation countAggregation = Aggregation.newAggregation(
                // 选择某些字段
                Aggregation.project("id", "name", UPDATE_DATE, "thumbId", "reviewing")
                        .and(ConvertOperators.Convert.convertValue("$_id").to("string"))//将主键Id转换为objectId
                        .as("id"),//新字段名称,
                Aggregation.lookup(RELATE_COLLECTION_NAME, "id", FILE_ID, "abc"),
                Aggregation.lookup(TagServiceImpl.RELATE_COLLECTION_NAME, "id", FILE_ID, "xyz"),
                Aggregation.match(criteria),
                Aggregation.match(Criteria.where("reviewing").is(false))
        );


        Aggregation aggregation = Aggregation.newAggregation(
                // 选择某些字段
                Aggregation.project("id", "name", UPDATE_DATE, "thumbId", "reviewing")
                        .and(ConvertOperators.Convert.convertValue("$_id").to("string"))//将主键Id转换为objectId
                        .as("id"),//新字段名称,
                Aggregation.lookup(RELATE_COLLECTION_NAME, "id", FILE_ID, "abc"),
                Aggregation.lookup(TagServiceImpl.RELATE_COLLECTION_NAME, "id", FILE_ID, "xyz"),
                Aggregation.match(criteria),
                Aggregation.match(Criteria.where("reviewing").is(false)),
                Aggregation.sort(Sort.Direction.DESC, "uploadDate"),
                Aggregation.skip(pageNum * pageSize),
                Aggregation.limit(pageSize)
        );

        int count = mongoTemplate.aggregate(countAggregation, FileServiceImpl.COLLECTION_NAME, FileDocumentDTO.class)
                .getMappedResults().size();

        AggregationResults<FileDocumentDTO> aggregate = mongoTemplate.aggregate(aggregation,
                FileServiceImpl.COLLECTION_NAME, FileDocumentDTO.class);
        List<FileDocumentDTO> mappedResults = aggregate.getMappedResults();

        return PageVO.<FileDocumentDTO>builder().pageSize(pageSize.intValue()).pageNum(pageNum.intValue()).total(count).list(mappedResults).build();
    }

    @Override
    public PageVO<FileDocumentDTO>  getMyCollection(String cateId, String tagId, String keyword, Long pageNum, Long pageSize, String userId) {
        Criteria criteria = new Criteria();
        if (StringUtils.hasText(cateId) && StringUtils.hasText(tagId)) {
            criteria = Criteria.where("abc.categoryId").is(cateId).and("xyz.tagId").is(tagId);
        } else if (StringUtils.hasText(cateId) && !StringUtils.hasText(tagId)) {
            criteria = Criteria.where("abc.categoryId").is(cateId);
        } else if (StringUtils.hasText(tagId) && !StringUtils.hasText(cateId)) {
            criteria = Criteria.where("xyz.tagId").is(tagId);
        }


        if (StringUtils.hasText(keyword)) {
            criteria.andOperator(Criteria.where("name").regex(Pattern.compile(keyword, Pattern.CASE_INSENSITIVE)));
        }


        Aggregation countAggregation = Aggregation.newAggregation(
                // 选择某些字段
                Aggregation.project("id", "name", UPDATE_DATE, "thumbId", "reviewing")
                        .and(ConvertOperators.Convert.convertValue("$_id").to("string"))//将主键Id转换为objectId
                        .as("id"),//新字段名称,
                Aggregation.lookup(RELATE_COLLECTION_NAME, "id", FILE_ID, "abc"),
                Aggregation.lookup(TagServiceImpl.RELATE_COLLECTION_NAME, "id", FILE_ID, "xyz"),
                Aggregation.lookup(CollectServiceImpl.COLLECTION_NAME, "id", "docId", "collect"),
                Aggregation.match(criteria),
                Aggregation.match(Criteria.where("reviewing").is(false)),
                Aggregation.match(Criteria.where("collect.userId").is(userId)
                        .and("collect.redisActionEnum").is(RedisActionEnum.COLLECT))
        );


        Aggregation aggregation = Aggregation.newAggregation(
                // 选择某些字段
                Aggregation.project("id", "name", UPDATE_DATE, "thumbId", "reviewing")
                        .and(ConvertOperators.Convert.convertValue("$_id").to("string"))//将主键Id转换为objectId
                        .as("id"),//新字段名称,
                Aggregation.lookup(RELATE_COLLECTION_NAME, "id", FILE_ID, "abc"),
                Aggregation.lookup(TagServiceImpl.RELATE_COLLECTION_NAME, "id", FILE_ID, "xyz"),
                Aggregation.lookup(CollectServiceImpl.COLLECTION_NAME, "id", "docId", "collect"),
                Aggregation.match(criteria),
                Aggregation.match(Criteria.where("reviewing").is(false)),
                Aggregation.match(Criteria.where("collect.userId").is(userId)
                        .and("collect.redisActionEnum").is(RedisActionEnum.COLLECT)),
                Aggregation.sort(Sort.Direction.DESC, "uploadDate"),
                Aggregation.skip(pageNum * pageSize),
                Aggregation.limit(pageSize)
        );

        int count = mongoTemplate.aggregate(countAggregation, FileServiceImpl.COLLECTION_NAME, FileDocumentDTO.class)
                .getMappedResults().size();

        AggregationResults<FileDocumentDTO> aggregate = mongoTemplate.aggregate(aggregation,
                FileServiceImpl.COLLECTION_NAME, FileDocumentDTO.class);
        List<FileDocumentDTO> mappedResults = aggregate.getMappedResults();

        return PageVO.<FileDocumentDTO>builder().pageSize(pageSize.intValue()).pageNum(pageNum.intValue()).total(count).list(mappedResults).build();
    }

    @Override
    public PageVO<FileDocumentDTO> getMyUploaded(String cateId, String tagId, String keyword, Long pageNum, Long pageSize,
                                             String userId) {
        Criteria criteria = new Criteria();
        if (StringUtils.hasText(cateId) && StringUtils.hasText(tagId)) {
            criteria = Criteria.where("abc.categoryId").is(cateId)
                    .and("xyz.tagId").is(tagId);
        } else if (StringUtils.hasText(cateId) && !StringUtils.hasText(tagId)) {
            criteria = Criteria.where("abc.categoryId").is(cateId);
        } else if (StringUtils.hasText(tagId) && !StringUtils.hasText(cateId)) {
            criteria = Criteria.where("xyz.tagId").is(tagId);
        }


        if (StringUtils.hasText(keyword)) {
            criteria.andOperator(Criteria.where("name").regex(Pattern.compile(keyword, Pattern.CASE_INSENSITIVE)));
        }

        Aggregation countAggregation = Aggregation.newAggregation(
                // 选择某些字段
                Aggregation.project("id", "name", UPDATE_DATE, "thumbId", "userId", "reviewing")
                        .and(ConvertOperators.Convert.convertValue("$_id").to("string"))//将主键Id转换为objectId
                        .as("id"),//新字段名称,
                Aggregation.lookup(RELATE_COLLECTION_NAME, "id", FILE_ID, "abc"),
                Aggregation.lookup(TagServiceImpl.RELATE_COLLECTION_NAME, "id", FILE_ID, "xyz"),
                Aggregation.match(criteria),
                Aggregation.match(Criteria.where("reviewing").is(false)),
                Aggregation.match(Criteria.where("userId").is(userId))
        );


        Aggregation aggregation = Aggregation.newAggregation(
                // 选择某些字段
                Aggregation.project("id", "name", UPDATE_DATE, "thumbId", "userId", "reviewing")
                        .and(ConvertOperators.Convert.convertValue("$_id").to("string"))//将主键Id转换为objectId
                        .as("id"),//新字段名称,
                Aggregation.lookup(RELATE_COLLECTION_NAME, "id", FILE_ID, "abc"),
                Aggregation.lookup(TagServiceImpl.RELATE_COLLECTION_NAME, "id", FILE_ID, "xyz"),
                Aggregation.match(criteria),
                Aggregation.match(Criteria.where("reviewing").is(false)),
                Aggregation.match(Criteria.where("userId").is(userId)),
                Aggregation.sort(Sort.Direction.DESC, "uploadDate"),
                Aggregation.skip(pageNum * pageSize),
                Aggregation.limit(pageSize)
        );

        int count = mongoTemplate.aggregate(countAggregation, FileServiceImpl.COLLECTION_NAME, FileDocumentDTO.class)
                .getMappedResults().size();

        AggregationResults<FileDocumentDTO> aggregate = mongoTemplate.aggregate(aggregation,
                FileServiceImpl.COLLECTION_NAME, FileDocumentDTO.class);
        List<FileDocumentDTO> mappedResults = aggregate.getMappedResults();

        return PageVO.<FileDocumentDTO>builder()
                .total(count)
                .list(mappedResults)
                .pageNum(pageNum.intValue())
                .pageSize(pageSize.intValue())
                .build();

    }
}
