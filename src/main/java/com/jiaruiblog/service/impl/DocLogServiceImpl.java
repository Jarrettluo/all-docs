package com.jiaruiblog.service.impl;

import com.jiaruiblog.entity.DocLog;
import com.jiaruiblog.entity.FileDocument;
import com.jiaruiblog.entity.User;
import com.jiaruiblog.entity.dto.BasePageDTO;
import com.jiaruiblog.service.IDocLogService;
import com.jiaruiblog.util.BaseApiResult;
import com.mongodb.client.result.DeleteResult;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author luojiarui
 * @ClassName DocLogServiceImpl
 * @Description 文档日志的查询和删除
 * @Date 2022/12/10 11:05
 * @Version 1.0
 **/
@Service
public class DocLogServiceImpl implements IDocLogService {

    public static final String DOC_LOG_COLLECTION = "docLog";

    public static final String RESULT = "操作成功了 %d 项目!";

//    @Resource
//    private UserServiceImpl userServiceImpl;

    @Resource
    private MongoTemplate mongoTemplate;

    public enum Action {
        PREVIEW,
        UPLOAD,
        DOWNLOAD(),
        GET(),
        POST(),
        DELETE()
    }

    @Override
    public String addLog(User user, FileDocument document, Action action) {
        DocLog docLog = new DocLog();
        docLog.setUserId(user.getId());
        docLog.setUserName(user.getUsername());
        docLog.setDocId(document.getId());
        docLog.setDocName(document.getName());
        docLog.setAction(action);
        docLog.setCreateDate(new Date());
        docLog.setUpdateDate(new Date());
        mongoTemplate.save(docLog, DOC_LOG_COLLECTION);
        return docLog.getId();
    }

    /*
     * @author luojiarui
     * @Description 查询系统的各类日志信息
     *              此接口用于管理员查询全量的数据
     * @Date 10:35 2024/8/17
     * @Param [page, userId]
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    @Override
    public Map<String, Object> queryDocLogs(BasePageDTO page) {
        // 根据不同的用户进行查询
        Query query = new Query();
        // 查询总数
        long count = mongoTemplate.count(query, DocLog.class, DOC_LOG_COLLECTION);
        query.skip((long) (page.getPage() - 1) * page.getRows());
        query.limit(page.getRows());
        query.with(Sort.by(Sort.Direction.DESC, "createDate"));

        List<DocLog> docLogList = mongoTemplate.find(query, DocLog.class);

        Map<String, Object> result = new HashMap<>();
        result.put("total", count);
        result.put("data", docLogList);
        return result;
    }

    /***
     * <p>TODO 使用是否权限限制</p>
     * @param logIds log的id信息
     * @param userId 用户id
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    @Override
    public ApiResult<Object> deleteDocLogBatch(List<String> logIds, String userId) {
//        User user = userServiceImpl.queryById(userId);
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").in(logIds));
//        if (user.getPermissionEnum().equals(PermissionEnum.USER)) {
//            query.addCriteria(Criteria.where("userId").is(user.getId()));
//        }
        DeleteResult remove = mongoTemplate.remove(query, DocLog.class, DOC_LOG_COLLECTION);
        return ApiResult.success(String.format(RESULT, remove.getDeletedCount()));
    }

}
