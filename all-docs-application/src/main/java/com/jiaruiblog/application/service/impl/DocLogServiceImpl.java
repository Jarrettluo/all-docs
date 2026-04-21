package com.jiaruiblog.application.service.impl;

import com.jiaruiblog.application.service.IDocLogService;
import com.jiaruiblog.domain.entity.DocLog;
import com.jiaruiblog.domain.entity.FileDocument;
import com.jiaruiblog.domain.entity.User;
import com.jiaruiblog.domain.entity.dto.BasePageDTO;
import com.jiaruiblog.domain.entity.vo.PageVO;
import com.jiaruiblog.infrastructure.repository.DocLogRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
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
 **/
@Slf4j
@Service
public class DocLogServiceImpl implements IDocLogService {

    public enum Action {
        PREVIEW, DOWNLOAD, UPLOAD, DELETE
    }

    @Resource
    private DocLogRepository docLogRepository;

    @Override
    public void insert(DocLog docLog) {
        if (docLog == null) {
            return;
        }
        docLog.setCreateDate(new Date());
        docLog.setUpdateDate(new Date());
        docLogRepository.save(docLog);
    }

    @Override
    public void remove(DocLog docLog) {
        if (docLog == null || docLog.getId() == null) {
            return;
        }
        docLogRepository.deleteById(docLog.getId());
    }

    @Override
    public void update(DocLog docLog) {
        if (docLog == null || docLog.getId() == null) {
            return;
        }
        docLog.setUpdateDate(new Date());
        docLogRepository.save(docLog);
    }

    @Override
    public void search(DocLog docLog) {
    }

    @Override
    public PageVO<DocLog> queryByPage(int pageNum, int pageSize) {
        Query query = new Query()
                .with(Sort.by(Sort.Direction.DESC, "createDate"))
                .skip((long) (pageNum - 1) * pageSize)
                .limit(pageSize);

        List<DocLog> docLogs = docLogRepository.findByQuery(query);
        long count = docLogRepository.countByQuery(new Query());

        return PageVO.<DocLog>builder()
                .total((int) count)
                .list(docLogs)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .build();
    }

    @Override
    public List<DocLog> queryByUserName(String userName) {
        if (userName == null || userName.isEmpty()) {
            return List.of();
        }
        Query query = new Query(Criteria.where("userName").is(userName))
                .with(Sort.by(Sort.Direction.DESC, "createDate"));
        return docLogRepository.findByQuery(query);
    }

    @Override
    public List<DocLog> queryByDocName(String docName) {
        if (docName == null || docName.isEmpty()) {
            return List.of();
        }
        Query query = new Query(Criteria.where("docName").is(docName))
                .with(Sort.by(Sort.Direction.DESC, "createDate"));
        return docLogRepository.findByQuery(query);
    }

    @Override
    public String addLog(User user, FileDocument document, DocLogServiceImpl.Action action) {
        if (user == null || document == null || action == null) {
            return null;
        }
        DocLog docLog = new DocLog();
        docLog.setUserId(user.getId());
        docLog.setUserName(user.getUsername());
        docLog.setDocId(document.getId());
        docLog.setDocName(document.getName());
        docLog.setAction(action.name());
        docLog.setCreateDate(new Date());
        docLog.setUpdateDate(new Date());
        return docLogRepository.save(docLog).getId();
    }

    @Override
    public Map<String, Object> queryDocLogs(BasePageDTO page) {
        Query query = new Query()
                .skip((long) (page.getPage() - 1) * page.getRows())
                .limit(page.getRows())
                .with(Sort.by(Sort.Direction.DESC, "createDate"));

        long count = docLogRepository.countByQuery(query);
        List<DocLog> docLogList = docLogRepository.findByQuery(query);

        Map<String, Object> result = new HashMap<>();
        result.put("total", count);
        result.put("data", docLogList);
        return result;
    }

    @Override
    public void deleteDocLogBatch(List<String> logIds, String userId) {
        if (logIds == null || logIds.isEmpty()) {
            return;
        }
        docLogRepository.deleteAllByIdIn(logIds);
    }
}