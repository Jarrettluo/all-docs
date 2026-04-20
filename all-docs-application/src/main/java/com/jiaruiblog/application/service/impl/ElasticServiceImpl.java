package com.jiaruiblog.application.service.impl;

import com.jiaruiblog.application.service.ElasticService;
import com.jiaruiblog.domain.entity.FileObj;
import com.jiaruiblog.domain.entity.vo.PageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

/**
 * @author luojiarui
 **/
@Slf4j
@Service
public class ElasticServiceImpl implements ElasticService {

    @Override
    public void upload(FileObj fileObj) {
        // TODO: Implement with infrastructure
    }

    @Override
    public PageVO<String> search(String keyword, int pageNum, int pageSize) {
        return PageVO.<String>builder().build();
    }

    @Override
    public List<String> queryFileNameListByIds(List<String> docIds) {
        return List.of();
    }

    @Override
    public List<FileObj> queryFileObjListByIds(List<String> docIds) {
        return List.of();
    }

    @Override
    public String queryContentById(String docId) {
        return "";
    }

    @Override
    public List<String> searchIds(String keyword) {
        return List.of();
    }

    @Override
    public void uploadFileObj(InputStream inputStream, FileObj fileObj) {
    }

    @Override
    public void deleteById(String id) {
    }

    @Override
    public void updateFileObj(InputStream inputStream, FileObj fileObj) {
    }

    @Override
    public List<java.util.Map<String, Object>> getWordStat() {
        return List.of();
    }
}