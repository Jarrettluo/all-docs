package com.jiaruiblog.application.service.impl;

import com.jiaruiblog.application.service.ElasticService;
import com.jiaruiblog.domain.entity.dto.SearchQuery;
import com.jiaruiblog.domain.entity.dto.SearchResultItem;
import com.jiaruiblog.domain.entity.po.Category;
import com.jiaruiblog.domain.entity.po.SearchDocument;
import com.jiaruiblog.domain.entity.po.Tag;
import com.jiaruiblog.domain.entity.vo.PageVO;
import com.jiaruiblog.domain.entity.vo.SearchResultVO;
import com.jiaruiblog.infrastructure.repository.CategoryRepository;
import com.jiaruiblog.infrastructure.repository.TagRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Resource
    private TagRepository tagRepository;

    @Resource
    private CategoryRepository categoryRepository;

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

    @Override
    public List<String> searchDocuments(SearchQuery query) {
        try {
            String keyword = query.getKeyword();
            Boolean fullText = query.getFullText();
            Boolean segment = query.getSegment();
            String searchType = query.getSearchType();

            log.debug("searchDocuments - keyword: {}, fullText: {}, segment: {}, searchType: {}",
                    keyword, fullText, segment, searchType);

            // Determine fields to search based on fullText and searchType
            List<Criteria> fieldCriteria = buildFieldCriteria(keyword, fullText, searchType);

            // Build final criteria
            Criteria criteria;
            if (fieldCriteria.isEmpty()) {
                // Empty keyword - return all documents with pagination
                criteria = new Criteria("id").exists();
            } else {
                // Combine field criteria with OR
                criteria = fieldCriteria.get(0);
                for (int i = 1; i < fieldCriteria.size(); i++) {
                    criteria = criteria.or(fieldCriteria.get(i));
                }
            }

            // Build query with pagination
            int page = query.getPage() != null ? query.getPage() : 1;
            int pageSize = query.getPageSize() != null ? query.getPageSize() : 20;
            Query esQuery = new CriteriaQuery(criteria)
                    .setPageable(org.springframework.data.domain.PageRequest.of(page - 1, pageSize));

            // Execute search
            SearchHits<SearchDocument> searchHits = elasticsearchOperations.search(esQuery, SearchDocument.class);

            List<String> docIds = searchHits.getSearchHits().stream()
                    .map(SearchHit::getContent)
                    .map(SearchDocument::getId)
                    .collect(Collectors.toList());

            log.info("searchDocuments - found {} documents", docIds.size());
            return docIds;

        } catch (Exception e) {
            log.error("searchDocuments failed", e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<SearchResultItem> searchDocumentsWithHighlight(SearchQuery query) {
        try {
            String keyword = query.getKeyword();
            Boolean fullText = query.getFullText();
            String searchType = query.getSearchType();

            log.debug("searchDocumentsWithHighlight - keyword: {}, fullText: {}, searchType: {}",
                    keyword, fullText, searchType);

            // Determine fields to search based on fullText and searchType
            List<Criteria> fieldCriteria = buildFieldCriteria(keyword, fullText, searchType);

            // Build final criteria
            Criteria criteria;
            if (fieldCriteria.isEmpty()) {
                criteria = new Criteria("id").exists();
            } else {
                criteria = fieldCriteria.get(0);
                for (int i = 1; i < fieldCriteria.size(); i++) {
                    criteria = criteria.or(fieldCriteria.get(i));
                }
            }

            // Build query with pagination
            int page = query.getPage() != null ? query.getPage() : 1;
            int pageSize = query.getPageSize() != null ? query.getPageSize() : 20;
            Query esQuery = new CriteriaQuery(criteria)
                    .setPageable(org.springframework.data.domain.PageRequest.of(page - 1, pageSize));

            // Execute search
            SearchHits<SearchDocument> searchHits = elasticsearchOperations.search(esQuery, SearchDocument.class);

            // Build result items with manual highlights
            List<SearchResultItem> resultItems = new ArrayList<>();
            for (SearchHit<SearchDocument> hit : searchHits.getSearchHits()) {
                String docId = hit.getContent().getId();
                SearchDocument doc = hit.getContent();

                // Manually generate highlight fragments
                List<String> highlightFragments = new ArrayList<>();
                String highlightSource = null;

                // Priority: content > name > tagNames > categoryName
                if (doc.getContent() != null && doc.getContent().contains(keyword)) {
                    String fragment = extractHighlightFragment(doc.getContent(), keyword);
                    if (fragment != null) {
                        highlightFragments.add(fragment);
                    }
                    highlightSource = "content";
                } else if (doc.getName() != null && doc.getName().contains(keyword)) {
                    highlightFragments.add(wrapWithHighlight(doc.getName(), keyword));
                    highlightSource = "name";
                } else if (doc.getTagNames() != null) {
                    for (String tag : doc.getTagNames()) {
                        if (tag != null && tag.contains(keyword)) {
                            highlightFragments.add(wrapWithHighlight(tag, keyword));
                            highlightSource = "tagNames";
                            break;
                        }
                    }
                } else if (doc.getCategoryName() != null && doc.getCategoryName().contains(keyword)) {
                    highlightFragments.add(wrapWithHighlight(doc.getCategoryName(), keyword));
                    highlightSource = "categoryName";
                }

                resultItems.add(SearchResultItem.builder()
                        .id(docId)
                        .highlightFragments(highlightFragments.isEmpty() ? null : highlightFragments)
                        .highlightSource(highlightSource)
                        .build());
            }

            log.info("searchDocumentsWithHighlight - found {} documents", resultItems.size());
            return resultItems;

        } catch (Exception e) {
            log.error("searchDocumentsWithHighlight failed", e);
            return new ArrayList<>();
        }
    }

    /**
     * Extract a fragment of text around the keyword with highlighting
     */
    private String extractHighlightFragment(String content, String keyword) {
        if (content == null || keyword == null) {
            return null;
        }
        int index = content.indexOf(keyword);
        if (index < 0) {
            return null;
        }
        // Get surrounding context
        int start = Math.max(0, index - 30);
        int end = Math.min(content.length(), index + keyword.length() + 50);
        String fragment = content.substring(start, end);
        // Add ellipsis if truncated
        if (start > 0) {
            fragment = "..." + fragment;
        }
        if (end < content.length()) {
            fragment = fragment + "...";
        }
        return wrapWithHighlight(fragment, keyword);
    }

    /**
     * Wrap keyword matches with highlight tags
     */
    private String wrapWithHighlight(String text, String keyword) {
        if (text == null || keyword == null) {
            return text;
        }
        return text.replace(keyword, "<em>" + keyword + "</em>");
    }

    /**
     * Build criteria for searchable fields based on fullText and searchType settings
     */
    private List<Criteria> buildFieldCriteria(String keyword, Boolean fullText, String searchType) {
        List<Criteria> criteriaList = new ArrayList<>();

        // If keyword is empty, no field criteria needed
        if (keyword == null || keyword.trim().isEmpty()) {
            return criteriaList;
        }

        if (Boolean.TRUE.equals(fullText)) {
            // fullText=true: search all relevant fields based on searchType
            if ("all".equalsIgnoreCase(searchType)) {
                // Search name + content + tagNames + categoryName
                criteriaList.add(new Criteria("name").matches(keyword));
                criteriaList.add(new Criteria("content").matches(keyword));
                criteriaList.add(new Criteria("tagNames").matches(keyword));
                criteriaList.add(new Criteria("categoryName").matches(keyword));
            } else if ("name".equalsIgnoreCase(searchType)) {
                // Search only name
                criteriaList.add(new Criteria("name").matches(keyword));
            } else if ("description".equalsIgnoreCase(searchType)) {
                // Search only content
                criteriaList.add(new Criteria("content").matches(keyword));
            } else {
                // Default to all fields
                criteriaList.add(new Criteria("name").matches(keyword));
                criteriaList.add(new Criteria("content").matches(keyword));
                criteriaList.add(new Criteria("tagNames").matches(keyword));
                criteriaList.add(new Criteria("categoryName").matches(keyword));
            }
        } else {
            // fullText=false: only search name
            criteriaList.add(new Criteria("name").matches(keyword));
        }

        return criteriaList;
    }

    /**
     * Full-text search with pagination, tag/category filtering, and multiple highlight fragments
     *
     * @param filterWord  the keyword to search for
     * @param tagId       optional tag ID for filtering
     * @param categoryId  optional category ID for filtering
     * @param page        page number (0-based)
     * @param rows        page size
     * @return SearchResultVO containing total count and result items
     */
    public SearchResultVO searchDocumentsFullText(String filterWord, String tagId, String categoryId, int page, int rows) {
        try {
            // 1. Build base criteria - full-text search on name, content, tagNames, categoryName
            Criteria criteria = new Criteria("name").matches(filterWord)
                    .or("content").matches(filterWord)
                    .or("tagNames").matches(filterWord)
                    .or("categoryName").matches(filterWord);

            // 2. Handle tagId -> tagNames filtering
            if (StringUtils.hasText(tagId)) {
                Tag tag = tagRepository.findById(tagId);
                if (tag != null) {
                    criteria = criteria.and("tagNames").contains(tag.getName());
                }
            }

            // 3. Handle categoryId -> categoryName filtering
            if (StringUtils.hasText(categoryId)) {
                Category category = categoryRepository.findById(categoryId).orElse(null);
                if (category != null) {
                    criteria = criteria.and("categoryName").contains(category.getName());
                }
            }

            // 4. ES paginated query
            Query esQuery = new CriteriaQuery(criteria)
                    .setPageable(PageRequest.of(page, rows));
            SearchHits<SearchDocument> searchHits = elasticsearchOperations.search(esQuery, SearchDocument.class);

            // 5. Build results
            List<SearchResultItem> items = new ArrayList<>();
            for (SearchHit<SearchDocument> hit : searchHits.getSearchHits()) {
                SearchDocument doc = hit.getContent();
                List<String> fragments = new ArrayList<>();

                // Extract multiple highlight fragments from content (max 3)
                if (doc.getContent() != null) {
                    int lastIndex = 0;
                    while (fragments.size() < 3) {
                        int idx = doc.getContent().toLowerCase().indexOf(filterWord.toLowerCase(), lastIndex);
                        if (idx < 0) break;
                        String frag = extractHighlightFragment(doc.getContent(), filterWord, idx);
                        if (frag != null) fragments.add(frag);
                        lastIndex = idx + 1;
                    }
                }

                // If no content match, try name
                if (fragments.isEmpty() && doc.getName() != null && doc.getName().contains(filterWord)) {
                    fragments.add(wrapWithHighlight(doc.getName(), filterWord));
                }

                items.add(SearchResultItem.builder()
                        .id(doc.getId())
                        .highlightFragments(fragments.isEmpty() ? null : fragments)
                        .highlightSource(fragments.isEmpty() ? null : "content")
                        .build());
            }

            long total = searchHits.getTotalHits();
            return SearchResultVO.builder().total(total).items(items).build();

        } catch (Exception e) {
            log.error("searchDocumentsFullText failed", e);
            return SearchResultVO.builder().total(0).items(new ArrayList<>()).build();
        }
    }

    /**
     * Extract a fragment of text around the keyword at the specified index with highlighting
     */
    private String extractHighlightFragment(String content, String keyword, int index) {
        int start = Math.max(0, index - 30);
        int end = Math.min(content.length(), index + keyword.length() + 50);
        String fragment = content.substring(start, end);
        if (start > 0) fragment = "..." + fragment;
        if (end < content.length()) fragment = fragment + "...";
        return wrapWithHighlight(fragment, keyword);
    }
}