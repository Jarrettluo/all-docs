package com.jiaruiblog.application.service.impl;

import com.jiaruiblog.application.service.IDocLogService;
import com.jiaruiblog.domain.entity.DocLog;
import com.jiaruiblog.domain.entity.FileDocument;
import com.jiaruiblog.domain.entity.User;
import com.jiaruiblog.domain.entity.dto.BasePageDTO;
import com.jiaruiblog.domain.entity.vo.PageVO;
import com.jiaruiblog.infrastructure.repository.DocLogRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author luojiarui
 **/
@Service
public class DocLogServiceImpl implements IDocLogService {

    public enum Action {
        PREVIEW, DOWNLOAD, UPLOAD, DELETE
    }

    @Resource
    private DocLogRepository docLogRepository;

    @Override
    public void insert(DocLog docLog) {
        docLog.setCreateDate(new Date());
        docLog.setUpdateDate(new Date());
        docLogRepository.save(docLog);
    }

    @Override
    public void remove(DocLog docLog) {
        docLogRepository.deleteById(docLog.getId());
    }

    @Override
    public void update(DocLog docLog) {
        docLogRepository.save(docLog);
    }

    @Override
    public void search(DocLog docLog) {
    }

    @Override
    public PageVO<DocLog> queryByPage(int pageNum, int pageSize) {
        return PageVO.<DocLog>builder().build();
    }

    @Override
    public List<DocLog> queryByUserName(String userName) {
        return List.of();
    }

    @Override
    public List<DocLog> queryByDocName(String docName) {
        return List.of();
    }

    @Override
    public String addLog(User user, FileDocument document, DocLogServiceImpl.Action action) {
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
        return Map.of();
    }

    @Override
    public void deleteDocLogBatch(List<String> logIds, String userId) {
        docLogRepository.deleteAllByIdIn(logIds);
    }
}