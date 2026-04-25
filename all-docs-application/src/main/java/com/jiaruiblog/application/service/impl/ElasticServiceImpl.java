package com.jiaruiblog.application.service.impl;

import com.jiaruiblog.application.service.ElasticService;
import com.jiaruiblog.domain.entity.po.SearchDocument;
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
import java.util.HashMap;
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

    @Resource
    private ElasticsearchOperations elasticsearchOperations;

    @Override
    public void upload(SearchDocument fileObj) {
        if (fileObj == null || fileObj.getId() == null) {
            log.warn("Cannot upload null or id-less SearchDocument to Elasticsearch");
            return;
        }
        try {
            elasticsearchOperations.save(fileObj);
            log.info("SearchDocument uploaded to ES: id={}, name={}", fileObj.getId(), fileObj.getName());
        } catch (Exception e) {
            log.error("Failed to upload SearchDocument to ES: id={}", fileObj.getId(), e);
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

            SearchHits<SearchDocument> searchHits = elasticsearchOperations.search(query, SearchDocument.class);

            List<String> results = searchHits.getSearchHits().stream()
                    .map(SearchHit::getContent)
                    .map(SearchDocument::getName)
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
            SearchHits<SearchDocument> searchHits = elasticsearchOperations.search(query, SearchDocument.class);
            return searchHits.getSearchHits().stream()
                    .map(SearchHit::getContent)
                    .map(SearchDocument::getName)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to query file names by ids", e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<SearchDocument> queryFileObjListByIds(List<String> docIds) {
        return List.of();
    }

    @Override
    public String queryContentById(String docId) {
        if (docId == null || docId.isEmpty()) {
            return "";
        }
        try {
            Query query = new CriteriaQuery(new Criteria("id").is(docId));
            SearchHits<SearchDocument> searchHits = elasticsearchOperations.search(query, SearchDocument.class);
            return searchHits.getSearchHits().stream()
                    .findFirst()
                    .map(SearchHit::getContent)
                    .map(SearchDocument::getContent)
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

            SearchHits<SearchDocument> searchHits = elasticsearchOperations.search(query, SearchDocument.class);
            return searchHits.getSearchHits().stream()
                    .map(SearchHit::getContent)
                    .map(SearchDocument::getId)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Elasticsearch searchIds failed for keyword: {}", keyword, e);
            return new ArrayList<>();
        }
    }

    @Override
    public void uploadFileObj(InputStream inputStream, SearchDocument searchDocument) {

    }

    @Override
    public void deleteById(String id) {
        if (id == null || id.isEmpty()) {
            return;
        }
        try {
            elasticsearchOperations.delete(id, SearchDocument.class);
            log.info("SearchDocument deleted from ES: id={}", id);
        } catch (Exception e) {
            log.error("Failed to delete from ES: id={}", id, e);
        }
    }

    @Override
    public void updateFileObj(InputStream inputStream, SearchDocument searchDocument) {
        if (searchDocument == null || searchDocument.getId() == null) {
            log.warn("Cannot update null or id-less SearchDocument in Elasticsearch");
            return;
        }
        try {
            // ES 中 SearchDocument 以 id 为主键，直接 save 即可覆盖
            elasticsearchOperations.save(searchDocument);
            log.info("SearchDocument updated in ES: id={}, name={}", searchDocument.getId(), searchDocument.getName());
        } catch (Exception e) {
            log.error("Failed to update SearchDocument in ES: id={}", searchDocument.getId(), e);
        }
    }

    @Override
    public List<Map<String, Object>> getWordStat() {
        try {
            // 查询所有文档，统计词频
            Criteria criteria = new Criteria("content").exists();
            Query query = new CriteriaQuery(criteria);
            SearchHits<SearchDocument> hits = elasticsearchOperations.search(query, SearchDocument.class);

            // 收集所有文档内容进行词频统计
            Map<String, Long> wordCount = new HashMap<>();
            for (SearchHit<SearchDocument> hit : hits.getSearchHits()) {
                String content = hit.getContent().getContent();
                if (content != null) {
                    // 简单分词，按空格/标点分割（生产环境建议用 IK 分词器的 analyze API）
                    String[] words = content.split("[\\s，。、！？；：\"『』（）(){},.!?;:'「」[\\]]+");
                    for (String word : words) {
                        if (word.length() >= 2) { // 过滤单字
                            wordCount.merge(word, 1L, Long::sum);
                        }
                    }
                }
            }

            // 返回 top 100 高频词
            return wordCount.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .limit(100)
                    .map(e -> Map.<String, Object>of("word", e.getKey(), "count", e.getValue()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to get word statistics", e);
            return new ArrayList<>();
        }
    }
}