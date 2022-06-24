package com.jiaruiblog.service.impl;

import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.entity.*;
import com.jiaruiblog.entity.vo.TagVO;
import com.jiaruiblog.service.TagService;
import com.jiaruiblog.utils.ApiResult;
import com.mongodb.client.result.UpdateResult;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public ApiResult insert(Tag tag) {
        mongoTemplate.save(tag, "tag");
        return ApiResult.success(MessageConstant.SUCCESS);
    }

    @Override
    public ApiResult update(Tag tag) {
        Query query = new Query(Criteria.where("_id").is(tag.getId()));
        Update update  = new Update();
        update.set("hobby", tag.getName());
        update.set("updateTime",tag.getUpdateDate());
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Tag.class);
        return ApiResult.success(MessageConstant.SUCCESS);
    }

    @Override
    public ApiResult remove(Tag tag) {
        return null;
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

    @Override
    public ApiResult search(Tag tag) {
        return null;
    }


    @Override
    public ApiResult list() {
        List<Tag> tags = mongoTemplate.findAll(Tag.class, COLLECTION_NAME);
        log.info(">>>>>>>" + tags.toString());
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
        return mongoTemplate.findById(id, Tag.class, COLLECTION_NAME);
    }

    /**
     *
     * @param relationship
     * @return
     */
    @Override
    public ApiResult addRelationShip(TagDocRelationship relationship) {
        mongoTemplate.save(relationship);
        return ApiResult.success(MessageConstant.SUCCESS);
    }

    @Override
    public ApiResult cancleTagRelationship(TagDocRelationship relationship) {
        mongoTemplate.remove(relationship);
        return ApiResult.success(MessageConstant.SUCCESS);
    }

    /**
     * @Author luojiarui
     * @Description // 根据文档的信息找到全部的tag信息
     * @Date 11:05 下午 2022/6/22
     * @Param [id]
     * @return java.util.List<com.jiaruiblog.entity.vo.TagVO>
     **/
    public List<TagVO> queryByDocId(Long id) {
        Query query = new Query().addCriteria(Criteria.where("docId").is(id));
        List<TagDocRelationship> relationships = mongoTemplate.find(query, TagDocRelationship.class, COLLECTION_NAME);

        List<TagVO> tagVOList = new ArrayList<>();

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
     * @Description // 根据tag的id 查询所有的相关的文档id列表
     * @Date 11:19 下午 2022/6/22
     * @Param [tagId]
     * @return java.util.List<java.lang.Long>
     **/
    public List<String> queryDocIdListByTagId(String tagId) {
        Query query = new Query().addCriteria(Criteria.where("tagId").is(tagId));
        List<TagDocRelationship> relationships = mongoTemplate.find(query, TagDocRelationship.class, COLLECTION_NAME);
        return relationships.stream().map(TagDocRelationship::getFileId).collect(Collectors.toList());
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

        List<Tag> categories = mongoTemplate.find(query, Tag.class, COLLECTION_NAME);
        List<String> ids = categories.stream().map(Tag::getId).collect(Collectors.toList());

        Query query1 = new Query().addCriteria(Criteria.where("cateId").in(ids));
        List<TagDocRelationship> relationships = mongoTemplate.find(query, TagDocRelationship.class, COLLECTION_NAME);
        return relationships.stream().map(TagDocRelationship::getFileId).collect(Collectors.toList());

    }

}
