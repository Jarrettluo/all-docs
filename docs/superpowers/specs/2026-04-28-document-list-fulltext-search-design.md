# Document List 全文检索改造设计

## 背景

`/api/v1/document/list` 接口需要支持全文检索能力：
- 用户进行过滤筛选时默认进行全文检索
- 检索结果中高亮片段分段存入 `description` 字段
- 全文检索与 category/tag 过滤分离，互不影响

## 现有接口对比

| 接口 | 搜索方式 | 分页方式 | 返回类型 |
|------|----------|----------|----------|
| `/document/list` | MySQL LIKE / ES（改造后） | MySQL / ES | `DocumentVO` |
| `/document/searchList` | ES 多条件 | MySQL | `DocSearchVO` |

## 改造方案

### 1. 入口判断逻辑

`DocumentServiceImpl.list(DocumentDTO)` 方法入口增加判断：

```
filterWord 不为空?
  → 调用 searchFullText() 方法
  → 否则调用 filterByCategoryOrTag() 方法
```

### 2. 全文检索方法 `searchFullText()`

#### 2.1 ES 查询

使用现有 `ElasticServiceImpl.searchDocumentsWithHighlight()` 或新增分页版本：

```java
// 查询条件构建
Criteria criteria = new Criteria("content").contains(filterWord)
    .or("name").contains(filterWord)
    .or("tagNames").contains(filterWord)
    .or("categoryName").contains(filterWord);

// ES 分页
SearchDocument repository.findAll(criteria, PageRequest.of(page, rows));
```

#### 2.2 tag/category 过滤

ES 中已有 `tagNames`（List<String>）和 `categoryName`（String）字段，可直接在 ES 查询中附加条件：

```java
if (tagId != null && !tagId.isEmpty()) {
    // 需要先查 Tag 获取 name，再用 name 查询 ES
    Tag tag = tagRepository.findById(tagId);
    criteria = criteria.and("tagNames").contains(tag.getName());
}

if (categoryId != null && !categoryId.isEmpty()) {
    // 需要先查 Category 获取 name，再用 name 查询 ES
    Category category = categoryRepository.findById(categoryId);
    criteria = criteria.and("categoryName").contains(category.getName());
}
```

> 注意：ES 存的是 name，当前请求传的是 ID，所以需要先查 MySQL 获取 name 再查 ES。

#### 2.3 高亮片段处理

ES 返回高亮片段后，拼接存入 `DocumentVO.description`：

```java
List<String> highlights = searchResultItem.getHighlightFragments();
String combinedHighlights = String.join("\n---\n", highlights);
documentVO.setDescription(combinedHighlights);
```

前端可通过 `\n---\n` 分割符解析各段高亮。

#### 2.4 返回结构

返回 `PageVO<DocumentVO>`：
- `total`: ES 命中总数
- `pageNum`: 当前页码
- `pageSize`: 每页条数
- `list`: 文档列表，`description` 字段含高亮片段

### 3. category/tag 过滤方法 `filterByCategoryOrTag()`

现有 MySQL 查询逻辑不变，作为独立方法存在。

### 4. 全文检索优先级

当 `filterWord` 和 `categoryId`/`tagId` 同时存在时：
1. 先通过 ES 查询全文（附带 tagNames/categoryName 过滤）
2. 获取匹配的文档 ID 列表
3. 用 ID 列表查 MySQL 获取文档详情

### 5. 数据流图

```
filterWord 非空?
  ├── 是: ES分页检索 + tagNames/categoryName过滤
  │       → 获取文档ID列表
  │       → MySQL批量查文档详情
  │       → 填充高亮到description
  │       → 返回PageVO<DocumentVO>
  │
  └── 否: MySQL过滤查询(categoryId/tagId)
          → 返回PageVO<DocumentVO>
```

## 关键文件改动

| 文件 | 改动内容 |
|------|----------|
| `DocumentServiceImpl.java` | 新增 `searchFullText()` 方法，修改 `list()` 入口逻辑 |
| `DocumentController.java` | 可能需要调整参数传递 |
| `DocumentVO.java` | 确认 `description` 用于存储高亮（已有字段） |

## 分页参数

- `page`: 页码（从 0 开始）
- `rows`: 每页条数

## 高亮格式

```
第一段匹配内容
---
第二段匹配内容
---
第三段匹配内容
```

前端可通过 `description.split("\n---\n")` 解析。

## 已确认事项

1. **`tagId` / `categoryId` 与 `filterWord` 的关系**：
   - `filterWord` 独立控制全文检索
   - `tagId` / `categoryId` 独立控制分类/标签过滤
   - 两者可同时存在：先 ES 全文检索（附带 tagNames/categoryName 过滤），再 MySQL 过滤
   - 如果只有 `tagId`/`categoryId` 没有 `filterWord`，走现有 MySQL 逻辑

2. **分页统计 `total`**：返回 ES 命中的文档总数（而非 MySQL 查询结果数）

3. **空值处理**：
   - `tagId` 对应的 Tag 不存在时：跳过 tag 过滤条件
   - `categoryId` 对应的 Category 不存在时：跳过 category 过滤条件