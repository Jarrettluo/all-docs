package com.jiaruiblog.application.service.impl;

import com.jiaruiblog.application.service.IDocLogService;
import com.jiaruiblog.common.exception.BusinessException;
import com.jiaruiblog.common.exception.ErrorCode;
import com.jiaruiblog.domain.entity.po.DocLog;
import com.jiaruiblog.domain.entity.po.FileDocument;
import com.jiaruiblog.domain.entity.po.User;
import com.jiaruiblog.domain.entity.dto.BasePageDTO;
import com.jiaruiblog.domain.entity.vo.PageVO;
import com.jiaruiblog.infrastructure.repository.DocLogRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
        // Note: Simplified implementation - would need pagination support in repository
        List<DocLog> docLogs = docLogRepository.findByAction("CREATE_DATE"); // Placeholder
        long count = docLogRepository.count();
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
        return docLogRepository.findByUserId(userName);
    }

    @Override
    public List<DocLog> queryByDocName(String docName) {
        // Note: Would need a specific query method for doc name
        return List.of();
    }

    @Override
    public String addLog(User user, FileDocument document, DocLogServiceImpl.Action action) {
        if (user == null || document == null || action == null) {
            return null;
        }
        DocLog docLog = new DocLog();
        docLog.setId(UUID.randomUUID().toString());
        docLog.setUserId(user.getId());
        docLog.setUserName(user.getUsername());
        docLog.setDocId(document.getId());
        docLog.setDocName(document.getName());
        docLog.setAction(action.name());
        docLog.setCreateDate(new Date());
        docLog.setUpdateDate(new Date());
        int save = docLogRepository.save(docLog);
        if (save < 1) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR);
        }
        return docLog.getId();
    }

    @Override
    public Map<String, Object> queryDocLogs(BasePageDTO page) {
        List<DocLog> docLogList = docLogRepository.findAll();
        long count = docLogList.size();

        // Pagination: page is 1-indexed, convert to 0-indexed for skip
        int pageNum = page.getPage();
        int pageSize = page.getRows();
        int skip = (pageNum - 1) * pageSize;
        docLogList = docLogList.stream()
                .skip(skip)
                .limit(pageSize)
                .toList();

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