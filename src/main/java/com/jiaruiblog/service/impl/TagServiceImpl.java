package com.jiaruiblog.service.impl;

import com.google.common.collect.Maps;
import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.entity.*;
import com.jiaruiblog.entity.vo.TagVO;
import com.jiaruiblog.service.TagService;
import com.jiaruiblog.utils.ApiResult;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
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
 * @Author Jarrett Luo
 * @Date 2022/6/7 11:40
 * @Version 1.0
 */
@Slf4j
@Service
public class TagServiceImpl implements TagService {

    private static String COLLECTION_NAME = "tagCollection";

    private final static String RELATE_COLLECTION_NAME = "relateTagCollection";

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public ApiResult insert(Tag tag) {
        // 必须经过查重啊
        if(isTagExist(tag.getName())) {
            return ApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
        mongoTemplate.save(tag, COLLECTION_NAME);
        return ApiResult.success(MessageConstant.SUCCESS);
    }
    
    /**
     * @Author luojiarui
     * @Description 更新tag的信息
     * @Date 16:55 2022/9/3
     * @Param [tag]
     * @return com.jiaruiblog.utils.ApiResult
     **/
    @Override
    public ApiResult update(Tag tag) {
        // 必须经过查重啊
        if(isTagExist(tag.getName())) {
            return ApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
        Query query = new Query(Criteria.where("_id").is(tag.getId()));
        Update update  = new Update();
        update.set("name", tag.getName());
        update.set("updateTime",tag.getUpdateDate());
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Tag.class, COLLECTION_NAME);
        return ApiResult.success(MessageConstant.SUCCESS);
    }

    /**
     * @Author luojiarui
     * @Description 删除某个已经存在的tag信息
     * @Date 16:55 2022/9/3
     * @Param [tag]
     * @return com.jiaruiblog.utils.ApiResult
     **/
    @Override
    public ApiResult remove(Tag tag) {
        if(tag == null || !StringUtils.hasText(tag.getId())) {
            return ApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_FORMAT_ERROR);
        }
        Query query1 = new Query();
        query1.addCriteria(Criteria.where("_id").is(tag.getId()));
        mongoTemplate.remove(query1, Tag.class, COLLECTION_NAME);

        // 同时去除掉各种关系的数据
        Query query = new Query(Criteria.where("tagId").is(tag.getId()));
        mongoTemplate.remove(query, TagDocRelationship.class, RELATE_COLLECTION_NAME);
        return ApiResult.success(MessageConstant.SUCCESS);
    }

    /**
     * 根据文档的id查询tag数量
     * @param tag
     * @return
     */
    @Override
    public ApiResult queryById(Tag tag) {
        Query query = new Query(Criteria.where("_").is("1"));
        mongoTemplate.count(query, Tag.class);
        return ApiResult.success(MessageConstant.SUCCESS);
    }

    /**
     * @Author luojiarui
     * @Description 根据tag的id查询全部的tag列表
     * @Date 22:13 2022/9/17
     * @Param [tagIds]
     * @return java.util.List<com.jiaruiblog.entity.Tag>
     **/
    public List<Tag> queryByIds(List<String> tagIds) {
        Query query = new Query(Criteria.where("_id").in(tagIds));
        return Optional.ofNullable(mongoTemplate.find(query, Tag.class, COLLECTION_NAME)).orElse(Lists.newArrayList());
    }

    @Override
    public ApiResult search(Tag tag) {
        return null;
    }


    @Override
    public ApiResult list() {
        List<Tag> tags = mongoTemplate.findAll(Tag.class, COLLECTION_NAME);
        return ApiResult.success(tags);
    }

    /**
     * @Author luojiarui
     * @Description // 根据id进行检索
     * @Date 11:15 下午 2022/6/22
     * @Param [id]
     * @return com.jiaruiblog.entity.Tag
     **/
    public Tag queryByTagId(String id) {
        if( !StringUtils.hasText(id)) {
            return null;
        }
        return mongoTemplate.findById(id, Tag.class, COLLECTION_NAME);
    }

    /**
     *
     * @param relationship
     * @return
     */
    @Override
    public ApiResult addRelationShip(TagDocRelationship relationship) {
        if( relationship == null || !StringUtils.hasText(relationship.getTagId())) {
            return ApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.PARAMS_IS_NOT_NULL);
        }
        // 判断以下是否存在这个关系
        Query query = new Query(Criteria.where("tagId").is(relationship.getTagId())
                .and("fileId").is(relationship.getFileId()));
        List<TagDocRelationship> result = mongoTemplate.find(query, TagDocRelationship.class, RELATE_COLLECTION_NAME);
        if( !result.isEmpty() ) {
            return ApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.PARAMS_IS_NOT_NULL);
        }
        mongoTemplate.save(relationship, RELATE_COLLECTION_NAME);
        return ApiResult.success(MessageConstant.SUCCESS);
    }

    @Override
    public ApiResult cancelTagRelationship(TagDocRelationship relationship) {
        Query query = new Query(Criteria.where("tagId").is(relationship.getTagId())
                .and("fileId").is(relationship.getFileId()));
        mongoTemplate.remove(query, TagDocRelationship.class, RELATE_COLLECTION_NAME);
        return ApiResult.success(MessageConstant.SUCCESS);
    }

    /**
     * @Author luojiarui
     * @Description // 根据文档的信息找到全部的tag信息
     * @Date 11:05 下午 2022/6/22
     * @Param [id]
     * @return java.util.List<com.jiaruiblog.entity.vo.TagVO>
     **/
    public List<TagVO> queryByDocId(String id) {
        List<TagVO> tagVOList = new ArrayList<>();
        Query query = new Query().addCriteria(Criteria.where("fileId").is(id));
        List<TagDocRelationship> relationships = mongoTemplate.find(query, TagDocRelationship.class, RELATE_COLLECTION_NAME);

        if(relationships == null || relationships.isEmpty()) {
            return tagVOList;
        }

        for (TagDocRelationship relationship : relationships) {
            // Query query1 = new Query().addCriteria(Criteria.where("_id").is(relationship.getTagId()));
            Tag tag = mongoTemplate.findById(relationship.getTagId(), Tag.class, COLLECTION_NAME);
            TagVO tagVO = new TagVO();
            tagVO.setId(tag.getId());
            tagVO.setName(tag.getName());
            tagVO.setRelationshipId(relationship.getTagId());
            tagVOList.add(tagVO);
        }
        return tagVOList;
    }

    /**
     * @Author luojiarui
     * @Description 查询最近的tag分页
     * @Date 22:23 2022/9/17
     * @Param []
     * @return java.util.Map<com.jiaruiblog.entity.Tag,java.util.List<com.jiaruiblog.entity.TagDocRelationship>>
     **/
    public Map<Tag, List<TagDocRelationship>> getRecentTagRelationship(Integer tagNum) {
        Map<Tag, List<TagDocRelationship>> result = Maps.newHashMap();
        List<TagDocRelationship> files = getTagRelationshipByPage(0, tagNum, null);
        System.out.println(files);
        if( CollectionUtils.isEmpty(files)) {
            return result;
        }
        List<String> tagIds = files.stream().map(TagDocRelationship::getTagId).collect(Collectors.toList());
        List<Tag> tags = queryByIds(tagIds);
        for (Tag tag : tags) {
            result.put(tag, getTagRelationshipByPage(0, 12, tag.getId()));
        }
        return result;
    }

    /**
     * @Author luojiarui
     * @Description 默认查询两个最近的tag
     * @Date 22:24 2022/9/17
     * @Param []
     * @return java.util.Map<com.jiaruiblog.entity.Tag,java.util.List<com.jiaruiblog.entity.TagDocRelationship>>
     **/
    @Override
    public Map<Tag, List<TagDocRelationship>> getRecentTagRelationship() {
        return getRecentTagRelationship(2);
    }
    /**
     * @Author luojiarui
     * @Description 分页查询相关的关系列表
     * 1、tagId 为null 的时候不进行tag相关检索；2、page和size 进行分页
     * @Date 22:22 2022/9/17
     * @Param [pageIndex, pageSize, tagId]
     * @return java.util.List<com.jiaruiblog.entity.TagDocRelationship>
     **/
    public List<TagDocRelationship> getTagRelationshipByPage(int pageIndex, int pageSize,  String tagId) {
        Query query = new Query().with(Sort.by(Sort.Direction.DESC, "createDate"));
        long skip = (pageIndex) * pageSize;
        query.skip(skip);
        query.limit(pageSize);
        if ( tagId != null) {
            query.addCriteria(Criteria.where("tagId").is(tagId));
        }
        return Optional.ofNullable(mongoTemplate.find(query, TagDocRelationship.class, RELATE_COLLECTION_NAME)).orElse(Lists.newArrayList());
    }

    /**
     * @Author luojiarui
     * @Description // 根据tag的id 查询所有的相关的文档id列表
     * @Date 11:19 下午 2022/6/22
     * @Param [tagId]
     * @return java.util.List<java.lang.Long>
     **/
    public List<String> queryDocIdListByTagId(String tagId) {
        Query query = new Query().addCriteria(Criteria.where("tagId").is(tagId));
        List<TagDocRelationship> relationships = mongoTemplate.find(query, TagDocRelationship.class, RELATE_COLLECTION_NAME);
        return relationships.stream().map(TagDocRelationship::getFileId).collect(Collectors.toList());
    }

    /**
     * 根据关键字模糊搜索相关的文档id
     * @param keyWord 关键字
     * @return 文档的id信息
     */
    public List<String> fuzzySearchDoc(String keyWord) {
        if( keyWord == null || "".equalsIgnoreCase(keyWord)) {
            return null;
        }
        Pattern pattern = Pattern.compile("^.*"+keyWord+".*$", Pattern.CASE_INSENSITIVE);
        Query query = new Query();
        query.addCriteria(Criteria.where("name").regex(pattern));
        List<Tag> categories = mongoTemplate.find(query, Tag.class, COLLECTION_NAME);
        if (CollectionUtils.isEmpty(categories)) {
            return Lists.newArrayList();
        }
        List<String> ids = categories.stream().map(Tag::getId).collect(Collectors.toList());
        Query query1 = new Query().addCriteria(Criteria.where("tagId").in(ids));
        List<TagDocRelationship> relationships = mongoTemplate.find(query1, TagDocRelationship.class, RELATE_COLLECTION_NAME);

        return relationships.stream().map(TagDocRelationship::getFileId).collect(Collectors.toList());

    }

    /**
     * 判断某个tag名字是否已经存在？
     * @param tagName
     * @return
     */
    private boolean isTagExist(String tagName) {
        List<Tag> tags = queryTagByName(tagName);
        if(tags == null || tags.isEmpty()){
            return false;
        }
        return true;
    }

    /**
     * 根据tag的名字检索tag信息
     * @param name tag 名称
     * @return 查询回来的tag列表
     */
    private List<Tag> queryTagByName(String name) {
        if( !StringUtils.hasText(name) ) {
            return null;
        }
        Query query = new Query().addCriteria(Criteria.where("name").is(name));
        return mongoTemplate.find(query, Tag.class, COLLECTION_NAME);
    }

    /**
     * 查询关系是否存在
     * @param relationship
     * @return
     */
    private boolean isRelateExist(TagDocRelationship relationship) {
        List<TagDocRelationship> tagDocRelationships = tagDocRelationships(relationship);
        if(tagDocRelationships == null || tagDocRelationships.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     *
     * @param tagDocRelationship
     * @return
     */
    private List<TagDocRelationship> tagDocRelationships(TagDocRelationship tagDocRelationship) {
        tagDocRelationship = Optional.ofNullable(tagDocRelationship).orElse(new TagDocRelationship());
        Query query = new Query().addCriteria(Criteria.where("tagId").is(tagDocRelationship.getTagId())
        .and("fileId").is(tagDocRelationship.getFileId()));
        return mongoTemplate.find(query, TagDocRelationship.class, RELATE_COLLECTION_NAME);
    }

    /**
     * @Author luojiarui
     * @Description // 根据文档的id解除掉标签和文档的关系
     * @Date 11:22 上午 2022/6/25
     * @Param [docId]
     * @return void
     **/
    public void removeRelateByDocId(String docId) {
        Query query = new Query(Criteria.where("docId").is(docId));
        List<TagDocRelationship> relationships = mongoTemplate.find(query, TagDocRelationship.class,
                RELATE_COLLECTION_NAME);
        relationships.forEach(item -> this.cancelTagRelationship(item));
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

    @Async
    public void saveTagWhenSaveDoc(FileDocument fileDocument) {
        if(fileDocument == null || !StringUtils.hasText(fileDocument.getSuffix())) {
            return;
        }
        String suffix = fileDocument.getSuffix();
        String tagName = suffix.substring(suffix.lastIndexOf(".") + 1);

        if ( tagName == null || tagName.length() == 0) {
            return;
        }

        List<Tag> tags = queryTagByName(tagName.toUpperCase(Locale.ROOT));
        Tag tag;

        if(CollectionUtils.isEmpty(tags)) {
            tag = new Tag();
            tag.setName(tagName.toUpperCase(Locale.ROOT));
            tag.setCreateDate(new Date());
            tag.setUpdateDate(new Date());
            tag = mongoTemplate.save(tag, COLLECTION_NAME);
        } else {
            tag = tags.get(0);
        }
        if (tag.getId() != null) {
            TagDocRelationship tagDocRelationship = new TagDocRelationship();
            tagDocRelationship.setTagId(tag.getId());
            tagDocRelationship.setFileId(fileDocument.getId());
            tagDocRelationship.setCreateDate(new Date());
            tagDocRelationship.setUpdateDate(new Date());
            addRelationShip(tagDocRelationship);
        }
    }

}
