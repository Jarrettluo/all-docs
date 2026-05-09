# Document List 全文检索实现计划

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 为 `/api/v1/document/list` 接口添加全文检索能力，当 `filterWord` 不为空时使用 ES 检索，高亮片段分段存入 `description` 字段

**Architecture:** 基于现有 ES 检索能力，改造 `ElasticServiceImpl` 添加支持 total 返回的方法，改造 `DocumentServiceImpl.list()` 根据条件分流

**Tech Stack:** Spring Data Elasticsearch, MyBatis, Redis

---

## Chunk 1: ElasticServiceImpl 新增分页检索方法

**Files:**
- Modify: `all-docs-application/src/main/java/com/jiaruiblog/application/service/impl/ElasticServiceImpl.java`
- Test: `all-docs-application/src/test/java/com/jiaruiblog/application/service/impl/ElasticServiceImplTest.java`

- [ ] **Step 1: 添加 SearchResultItem 多高亮支持**

修改 `SearchResultItem.java`，将 `highlightFragment` 改为 `highlightFragments` (List<String>):

```java
// File: all-docs-domain/src/main/java/com/jiaruiblog/domain/entity/dto/SearchResultItem.java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResultItem {
    private String id;
    private List<String> highlightFragments;  // 改为 List
    private String highlightSource;
}
```

- [ ] **Step 2: 新增 ES 分页检索方法**

在 `ElasticServiceImpl.java` 新增方法 `searchDocumentsFullText(DocumentDTO dto)`:

```java
/**
 * 全文检索 + 分页 + 多高亮片段
 * @param filterWord 关键词
 * @param tagId 标签ID（可选）
 * @param categoryId 分类ID（可选）
 * @param page 页码
 * @param rows 每页条数
 * @return SearchResultVO 含 total 和 list
 */
public SearchResultVO searchDocumentsFullText(String filterWord, String tagId, String categoryId, int page, int rows) {
    try {
        // 1. 构建基础条件
        Criteria criteria = new Criteria("name").matches(filterWord)
                .or("content").matches(filterWord)
                .or("tagNames").matches(filterWord)
                .or("categoryName").matches(filterWord);

        // 2. 处理 tagId -> tagNames 过滤
        if (StringUtils.hasText(tagId)) {
            Tag tag = tagRepository.findById(tagId);
            if (tag != null) {
                criteria = criteria.and("tagNames").contains(tag.getName());
            }
        }

        // 3. 处理 categoryId -> categoryName 过滤
        if (StringUtils.hasText(categoryId)) {
            Category category = categoryRepository.findById(categoryId);
            if (category != null) {
                criteria = criteria.and("categoryName").contains(category.getName());
            }
        }

        // 4. ES 分页查询
        Query esQuery = new CriteriaQuery(criteria)
                .setPageable(PageRequest.of(page, rows));
        SearchHits<SearchDocument> searchHits = elasticsearchOperations.search(esQuery, SearchDocument.class);

        // 5. 构建结果
        List<SearchResultItem> items = new ArrayList<>();
        for (SearchHit<SearchDocument> hit : searchHits.getSearchHits()) {
            SearchDocument doc = hit.getContent();
            String keyword = filterWord;
            List<String> fragments = new ArrayList<>();

            // 从 content 中提取多个高亮
            if (doc.getContent() != null) {
                int lastIndex = 0;
                while (true) {
                    int idx = doc.getContent().toLowerCase().indexOf(keyword.toLowerCase(), lastIndex);
                    if (idx < 0) break;
                    String frag = extractHighlightFragment(doc.getContent(), keyword, idx);
                    if (frag != null) fragments.add(frag);
                    lastIndex = idx + 1;
                    if (fragments.size() >= 3) break;  // 最多3段
                }
            }

            // 如果 content 没匹配，尝试 name
            if (fragments.isEmpty() && doc.getName() != null && doc.getName().contains(keyword)) {
                fragments.add(wrapWithHighlight(doc.getName(), keyword));
            }

            items.add(SearchResultItem.builder()
                    .id(doc.getId())
                    .highlightFragments(fragments)
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
```

- [ ] **Step 3: 新增 SearchResultVO 类**

```java
// File: all-docs-domain/src/main/java/com/jiaruiblog/domain/entity/vo/SearchResultVO.java
@Data
@Builder
public class SearchResultVO {
    private long total;
    private List<SearchResultItem> items;
}
```

- [ ] **Step 4: 修改 extractHighlightFragment 支持指定位置**

```java
private String extractHighlightFragment(String content, String keyword, int index) {
    int start = Math.max(0, index - 30);
    int end = Math.min(content.length(), index + keyword.length() + 50);
    String fragment = content.substring(start, end);
    if (start > 0) fragment = "..." + fragment;
    if (end < content.length()) fragment = fragment + "...";
    return wrapWithHighlight(fragment, keyword);
}
```

- [ ] **Step 5: 运行测试**

```bash
cd all-docs-application && mvn test -Dtest=ElasticServiceImplTest -v
```

---

## Chunk 2: DocumentServiceImpl 改造 list 方法

**Files:**
- Modify: `all-docs-application/src/main/java/com/jiaruiblog/application/service/impl/DocumentServiceImpl.java`
- Test: `all-docs-application/src/test/java/com/jiaruiblog/application/service/impl/DocumentServiceImplTest.java`

- [ ] **Step 1: 修改 DocumentServiceImpl.list() 入口逻辑**

在 `list(DocumentDTO documentDTO)` 方法开头添加判断:

```java
@Override
public PageVO<DocumentVO> list(DocumentDTO documentDTO) {
    if (documentDTO == null) {
        return PageVO.<DocumentVO>builder().build();
    }

    String filterWord = documentDTO.getFilterWord();

    // filterWord 不为空时走 ES 全文检索
    if (StringUtils.hasText(filterWord)) {
        return searchFullText(documentDTO);
    }

    // 否则走现有 MySQL 逻辑
    // ... 现有代码保持不变
}
```

- [ ] **Step 2: 新增 searchFullText() 方法**

```java
/**
 * ES 全文检索
 */
private PageVO<DocumentVO> searchFullText(DocumentDTO documentDTO) {
    String filterWord = documentDTO.getFilterWord();
    String tagId = documentDTO.getTagId();
    String categoryId = documentDTO.getCategoryId();
    int page = documentDTO.getPage();
    int rows = documentDTO.getRows();

    // 1. ES 检索
    SearchResultVO searchResult = elasticService.searchDocumentsFullText(
            filterWord, tagId, categoryId, page, rows);

    if (searchResult.getItems().isEmpty()) {
        return PageVO.<DocumentVO>builder()
                .pageNum(page)
                .pageSize(rows)
                .total(0)
                .list(new ArrayList<>())
                .build();
    }

    // 2. 获取文档 ID 列表
    List<String> docIds = searchResult.getItems().stream()
            .map(SearchResultItem::getId)
            .toList();

    // 3. 批量查询 MySQL 获取文档详情
    List<FileDocument> documents = documentMybatisRepository.findByIdList(docIds);

    // 4. 构建 ID -> 高亮片段 的映射
    Map<String, List<String>> highlightMap = searchResult.getItems().stream()
            .collect(Collectors.toMap(
                    SearchResultItem::getId,
                    SearchResultItem::getHighlightFragments,
                    (existing, replacement) -> replacement
            ));

    // 5. 转换为 DocumentVO，高亮片段存入 description
    List<DocumentVO> voList = documents.stream()
            .map(doc -> {
                DocumentVO vo = convertDocument(new DocumentVO(), doc);
                List<String> highlights = highlightMap.get(doc.getId());
                if (highlights != null && !highlights.isEmpty()) {
                    vo.setDescription(String.join("\n---\n", highlights));
                }
                return vo;
            })
            .toList();

    return PageVO.<DocumentVO>builder()
            .pageNum(page)
            .pageSize(rows)
            .total(searchResult.getTotal())
            .list(voList)
            .build();
}
```

- [ ] **Step 3: 注入 RedisService（如果尚未注入）**

确认 `ElasticServiceImpl` 已注入 `TagRepository` 和 `CategoryRepository`。如果构造方法需要调整，修改构造方法注入。

- [ ] **Step 4: 运行测试**

```bash
cd all-docs-application && mvn test -Dtest=DocumentServiceImplTest -v
```

---

## Chunk 3: 集成测试

**Files:**
- Test: `all-docs-api/src/test/java/com/jiaruiblog/api/controller/DocumentControllerTest.java`

- [ ] **Step 1: 启动应用测试**

```bash
cd all-docs-bootstrap && mvn spring-boot:run
```

- [ ] **Step 2: 测试全文检索**

```bash
curl -X POST http://localhost:8082/api/v1/document/list \
  -H "Content-Type: application/json" \
  -d '{"filterWord":"mcp","page":0,"rows":6,"type":"FILTER","userId":"ac92d2e5-d092-45eb-a8db-4cb7d9ae74c1"}'
```

预期返回：documents 中 description 字段包含高亮片段，格式为 `"第一段\n---\n第二段"`

- [ ] **Step 3: 测试 category/tag 过滤（不走 ES）**

```bash
curl -X POST http://localhost:8082/api/v1/document/list \
  -H "Content-Type: application/json" \
  -d '{"categoryId":"xxx","page":0,"rows":6,"type":"CATEGORY"}'
```

预期返回：description 为空（不走 ES），正常返回文档列表

---

## 关键文件清单

| 文件 | 改动 |
|------|------|
| `SearchResultItem.java` | `highlightFragment` → `highlightFragments List<String>` |
| `SearchResultVO.java` | 新增，含 `total` 和 `items` |
| `ElasticServiceImpl.java` | 新增 `searchDocumentsFullText()` 方法 |
| `DocumentServiceImpl.java` | `list()` 加分流，新增 `searchFullText()` |

## 注意事项

1. ES 检索时 tagId/categoryId 需要先查 MySQL 获取 name
2. 高亮片段用 `\n---\n` 分隔，前端可通过 `split("\n---\n")` 解析
3. `filterWord` 为空时保持现有 MySQL 逻辑不变