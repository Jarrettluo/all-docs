package com.jiaruiblog.service.impl;

import com.google.common.collect.Maps;
import com.jiaruiblog.auth.PermissionEnum;
import com.jiaruiblog.entity.BasePageDTO;
import com.jiaruiblog.entity.DocLog;
import com.jiaruiblog.entity.User;
import com.jiaruiblog.service.IDocLogService;
import com.jiaruiblog.util.BaseApiResult;
import com.mongodb.client.result.DeleteResult;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @ClassName DocLogServiceImpl
 * @Description 文档日志的查询和删除
 * @Author luojiarui
 * @Date 2022/12/10 11:05
 * @Version 1.0
 **/
@Service
public class DocLogServiceImpl implements IDocLogService {

    public static final String DOC_LOG_COLLECTION = "docLog";

    public static final String RESULT = "操作成功了 %d 项目";

    @Resource
    private UserServiceImpl userServiceImpl;

    @Resource
    private MongoTemplate mongoTemplate;


    @Override
    public BaseApiResult queryDocLogs(BasePageDTO page, String userId) {
        User user = userServiceImpl.queryById(userId);
        // 根据不同的用户进行查询
        Query query = new Query();
        if (user.getPermissionEnum().equals(PermissionEnum.USER)) {
            query.addCriteria(Criteria.where("userId").is(user.getId()));
        }
        query.skip((long) page.getPage() * page.getRows());
        query.limit(page.getRows());

        List<DocLog> docLogList = mongoTemplate.find(query, DocLog.class);
        long count = mongoTemplate.count(query, DocLog.class, DOC_LOG_COLLECTION);
        Map<String, Object> result = Maps.newHashMap();
        result.put("total", count);
        result.put("data", docLogList);
        return BaseApiResult.success(result);
    }

    @Override
    public BaseApiResult deleteDocLogBatch(List<String> logIds, String userId) {
        User user = userServiceImpl.queryById(userId);
        Query query = new Query();
        if (user.getPermissionEnum().equals(PermissionEnum.USER)) {
            query.addCriteria(Criteria.where("userId").is(user.getId()));
        }
        DeleteResult remove = mongoTemplate.remove(query, DocLog.class, DOC_LOG_COLLECTION);
        return BaseApiResult.success(String.format(RESULT, remove.getDeletedCount()));
    }

}
