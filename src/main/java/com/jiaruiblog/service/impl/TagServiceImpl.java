package com.jiaruiblog.service.impl;

import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.entity.CateDocRelationship;
import com.jiaruiblog.entity.Tag;
import com.jiaruiblog.entity.TagDocRelationship;
import com.jiaruiblog.entity.User;
import com.jiaruiblog.service.TagService;
import com.jiaruiblog.utils.ApiResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

/**
 * @Author Jarrett Luo
 * @Date 2022/6/7 11:40
 * @Version 1.0
 */
@Service
public class TagServiceImpl implements TagService {

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

}
