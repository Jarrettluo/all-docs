package com.jiaruiblog.service.impl;

import com.jiaruiblog.entity.DocLog;
import com.jiaruiblog.entity.FileDocument;
import com.jiaruiblog.entity.User;
import com.jiaruiblog.entity.dto.BasePageDTO;
import com.jiaruiblog.repository.DocLogRepository;
import com.jiaruiblog.service.IDocLogService;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Sort;
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

    @Resource
    private DocLogRepository docLogRepository;

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
        return docLogRepository.save(docLog).getId();
    }

    @Override
    public Map<String, Object> queryDocLogs(BasePageDTO page) {
        Query query = new Query();
        query.skip((long) (page.getPage() - 1) * page.getRows());
        query.limit(page.getRows());
        query.with(Sort.by(Sort.Direction.DESC, "createDate"));

        long count = docLogRepository.countByQuery(query);
        List<DocLog> docLogList = docLogRepository.findByQuery(query);

        Map<String, Object> result = new HashMap<>();
        result.put("total", count);
        result.put("data", docLogList);
        return result;
    }

    @Override
    public void deleteDocLogBatch(List<String> logIds, String userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").in(logIds));
        docLogRepository.deleteAllByIdIn(logIds);
    }
}
