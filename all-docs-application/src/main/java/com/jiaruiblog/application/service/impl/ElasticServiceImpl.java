package com.jiaruiblog.application.service.impl;

import com.jiaruiblog.application.service.ElasticService;
import com.jiaruiblog.domain.entity.FileObj;
import com.jiaruiblog.domain.entity.vo.PageVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Elasticsearch service implementation for document indexing and search
 *
 * @author luojiarui
 **/
@Slf4j
@Service
public class ElasticServiceImpl implements ElasticService {

    private static final String INDEX_NAME = "docwrite";

    @Resource
    private ElasticsearchOperations elasticsearchOperations;

    @Override
    public void upload(FileObj fileObj) {
        if (fileObj == null || fileObj.getId() == null) {
            log.warn("Cannot upload null or id-less FileObj to Elasticsearch");
            return;
        }
        try {
            elasticsearchOperations.save(fileObj);
            log.info("FileObj uploaded to ES: id={}, name={}", fileObj.getId(), fileObj.getName());
        } catch (Exception e) {
            log.error("Failed to upload FileObj to ES: id={}", fileObj.getId(), e);
        }
    }

    @Override
    public PageVO<String> search(String keyword, int pageNum, int pageSize) {
        if (keyword == null || keyword.isEmpty()) {
            return PageVO.<String>builder()
                    .pageNum(pageNum)
                    .pageSize(pageSize)
                    .total(0)
                    .list(new ArrayList<>())
                    .build();
        }
        try {
            Criteria criteria = new Criteria("content").matches(keyword)
                    .or(new Criteria("name").matches(keyword));
            Query query = new CriteriaQuery(criteria)
                    .setPageable(org.springframework.data.domain.PageRequest.of(pageNum - 1, pageSize));

            SearchHits<FileObj> searchHits = elasticsearchOperations.search(query, FileObj.class);

            List<String> results = searchHits.getSearchHits().stream()
                    .map(SearchHit::getContent)
                    .map(FileObj::getName)
                    .collect(Collectors.toList());

            return PageVO.<String>builder()
                    .pageNum(pageNum)
                    .pageSize(pageSize)
                    .total(searchHits.getTotalHits())
                    .list(results)
                    .build();
        } catch (Exception e) {
            log.error("Elasticsearch search failed for keyword: {}", keyword, e);
            return PageVO.<String>builder()
                    .pageNum(pageNum)
                    .pageSize(pageSize)
                    .total(0)
                    .list(new ArrayList<>())
                    .build();
        }
    }

    @Override
    public List<String> queryFileNameListByIds(List<String> docIds) {
        if (docIds == null || docIds.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            Query query = new CriteriaQuery(new Criteria("id").in(docIds));
            SearchHits<FileObj> searchHits = elasticsearchOperations.search(query, FileObj.class);
            return searchHits.getSearchHits().stream()
                    .map(SearchHit::getContent)
                    .map(FileObj::getName)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to query file names by ids", e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<FileObj> queryFileObjListByIds(List<String> docIds) {
        if (docIds == null || docIds.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            Query query = new CriteriaQuery(new Criteria("id").in(docIds));
            SearchHits<FileObj> searchHits = elasticsearchOperations.search(query, FileObj.class);
            return searchHits.getSearchHits().stream()
                    .map(SearchHit::getContent)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to query file objects by ids", e);
            return new ArrayList<>();
        }
    }

    @Override
    public String queryContentById(String docId) {
        if (docId == null || docId.isEmpty()) {
            return "";
        }
        try {
            Query query = new CriteriaQuery(new Criteria("id").is(docId));
            SearchHits<FileObj> searchHits = elasticsearchOperations.search(query, FileObj.class);
            return searchHits.getSearchHits().stream()
                    .findFirst()
                    .map(SearchHit::getContent)
                    .map(FileObj::getContent)
                    .orElse("");
        } catch (Exception e) {
            log.error("Failed to query content by id: {}", docId, e);
            return "";
        }
    }

    @Override
    public List<String> searchIds(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            Criteria criteria = new Criteria("content").matches(keyword)
                    .or(new Criteria("name").matches(keyword));
            Query query = new CriteriaQuery(criteria);

            SearchHits<FileObj> searchHits = elasticsearchOperations.search(query, FileObj.class);
            return searchHits.getSearchHits().stream()
                    .map(SearchHit::getContent)
                    .map(FileObj::getId)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Elasticsearch searchIds failed for keyword: {}", keyword, e);
            return new ArrayList<>();
        }
    }

    @Override
    public void uploadFileObj(InputStream inputStream, FileObj fileObj) {
        if (inputStream == null || fileObj == null) {
            log.warn("Cannot upload null input stream or FileObj");
            return;
        }
        try {
            byte[] bytes = inputStream.readAllBytes();
            String content = java.util.Base64.getEncoder().encodeToString(bytes);
            fileObj.setContent(content);
            elasticsearchOperations.save(fileObj);
            log.info("FileObj with content uploaded to ES: id={}", fileObj.getId());
        } catch (Exception e) {
            log.error("Failed to upload file obj with content to ES", e);
        }
    }

    @Override
    public void deleteById(String id) {
        if (id == null || id.isEmpty()) {
            return;
        }
        try {
            elasticsearchOperations.delete(id, FileObj.class);
            log.info("FileObj deleted from ES: id={}", id);
        } catch (Exception e) {
            log.error("Failed to delete from ES: id={}", id, e);
        }
    }

    @Override
    public void updateFileObj(InputStream inputStream, FileObj fileObj) {
        if (fileObj == null || fileObj.getId() == null) {
            log.warn("Cannot update null or id-less FileObj");
            return;
        }
        try {
            if (inputStream != null) {
                byte[] bytes = inputStream.readAllBytes();
                String content = java.util.Base64.getEncoder().encodeToString(bytes);
                fileObj.setContent(content);
            }
            elasticsearchOperations.save(fileObj);
            log.info("FileObj updated in ES: id={}", fileObj.getId());
        } catch (Exception e) {
            log.error("Failed to update FileObj in ES", e);
        }
    }

    @Override
    public List<Map<String, Object>> getWordStat() {
        try {
            // Return basic index statistics
            Map<String, Object> stat = Map.of(
                    "index", INDEX_NAME,
                    "status", "available"
            );
            return List.of(stat);
        } catch (Exception e) {
            log.error("Failed to get word statistics", e);
            return new ArrayList<>();
        }
    }
}