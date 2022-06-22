package com.jiaruiblog.service.impl;

import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.entity.CollectDocRelationship;
import com.jiaruiblog.service.CollectService;
import com.jiaruiblog.utils.ApiResult;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

/**
 * @Author Jarrett Luo
 * @Date 2022/6/7 11:40
 * @Version 1.0
 */
@Slf4j
@Service
public class CollectServiceImpl implements CollectService {

    private static String collectionName = "fileDatas";

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public ApiResult insert(CollectDocRelationship collect) {
        log.info("======开始关注");
        mongoTemplate.save(collect, "collectDocRelationship");
        return ApiResult.success(MessageConstant.SUCCESS);
    }

    @Override
    public ApiResult remove(CollectDocRelationship collect) {
        log.info("=====取消关注");
        mongoTemplate.remove(collect);
        return ApiResult.success(MessageConstant.SUCCESS);
    }

    /**
     * @Author luojiarui
     * @Description //根据文档的id 查询评论的数量
     * @Date 10:47 下午 2022/6/22
     * @Param [docId]
     * @return java.lang.Long
     **/
    public Long collectionNum(Long docId) {
        Query query = new Query().addCriteria(Criteria.where("docId").is(docId));
        return mongoTemplate.count(query, collectionName);
    }
}
