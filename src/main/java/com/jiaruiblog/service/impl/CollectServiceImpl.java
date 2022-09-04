package com.jiaruiblog.service.impl;

import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.entity.CollectDocRelationship;
import com.jiaruiblog.entity.Comment;
import com.jiaruiblog.service.CollectService;
import com.jiaruiblog.utils.ApiResult;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

/**
 * @Author Jarrett Luo
 * @Date 2022/6/7 11:40
 * @Version 1.0
 */
@Slf4j
@Service
public class CollectServiceImpl implements CollectService {

    private static String collectionName = "collectCollection";

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    UserServiceImpl userServiceImpl;

    @Autowired
    FileServiceImpl fileServiceImpl;

    /**
     * @Author luojiarui
     * @Description // 对某个文档进行关注
     * @Date 9:31 下午 2022/6/23
     * @Param [collect]
     * @return com.jiaruiblog.utils.ApiResult
     **/
    @Override
    public ApiResult insert(CollectDocRelationship collect) {
        // 必须经过userId和docId的校验，否则不予关注
        if( !userServiceImpl.isExist(collect.getUserId()) || !fileServiceImpl.isExist(collect.getDocId())) {
            return ApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }

        CollectDocRelationship collectDb = getExistRelationship(collect);
        if(collectDb != null){
            return ApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
        mongoTemplate.save(collect, collectionName);
        return ApiResult.success(MessageConstant.SUCCESS);
    }

    /**
     * @Author luojiarui
     * @Description // 删除收藏关系
     * @Date 9:43 下午 2022/6/23
     * @Param [collect]
     * @return com.jiaruiblog.utils.ApiResult
     **/
    @Override
    public ApiResult remove(CollectDocRelationship collect) {
        collect = getExistRelationship(collect);
        while (collect != null ){
            mongoTemplate.remove(collect, collectionName);
            collect = getExistRelationship(collect);
        }
        return ApiResult.success(MessageConstant.SUCCESS);
    }

    /**
     * @Author luojiarui
     * @Description // 查询已经存在的关系
     * @Date 9:37 下午 2022/6/23
     * @Param []
     * @return com.jiaruiblog.entity.CollectDocRelationship
     **/
    private CollectDocRelationship getExistRelationship(CollectDocRelationship collect) {
        collect = Optional.ofNullable(collect).orElse(new CollectDocRelationship());

        Query query = new Query()
                .addCriteria(Criteria.where("docId").is(collect.getDocId())
                        .and("userId").is(collect.getUserId()));

        CollectDocRelationship relationship = mongoTemplate.findOne(
                query, CollectDocRelationship.class, collectionName
        );
        return relationship;
    }

    public Long collectNum(String docId) {
        Query query = new Query().addCriteria(Criteria.where("docId").is(docId));
        return mongoTemplate.count(query, CollectDocRelationship.class, collectionName);
    }

    /**
     * @Author luojiarui
     * @Description // 根据文档的id删除掉点赞的关系
     * @Date 11:17 上午 2022/6/25
     * @Param [docId]
     * @return void
     **/
    public void removeRelateByDocId(String docId) {
        Query query = new Query(Criteria.where("docId").is(docId));
        List<CollectDocRelationship> relationships = mongoTemplate.find(query, CollectDocRelationship.class,
                collectionName);
        relationships.forEach(item -> this.remove(item));
    }


}
