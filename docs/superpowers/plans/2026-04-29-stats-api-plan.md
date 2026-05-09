# Statistics API 扩展实现计划

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 扩展统计后端 API，完成统计卡片数据扩展及 5 个新增接口

**Architecture:** 在现有 `StatisticsService` / `StatisticsController` 基础上扩展，新增 5 个 VO 类，新增 MyBatis mapper 查询，复用 Redis ZSet 获取热词和热门文档数据

**Tech Stack:** Spring Boot, MyBatis, Redis (StringRedisTemplate ZSet), Java

---

## Chunk 1: Domain 层 - 新增 VO 类

**Files:**
- Create: `all-docs-domain/src/main/java/com/jiaruiblog/domain/entity/vo/DocTypeDistVO.java`
- Create: `all-docs-domain/src/main/java/com/jiaruiblog/domain/entity/vo/CategoryDistVO.java`
- Create: `all-docs-domain/src/main/java/com/jiaruiblog/domain/entity/vo/HotDocVO.java`
- Create: `all-docs-domain/src/main/java/com/jiaruiblog/domain/entity/vo/SearchHotWordVO.java`
- Create: `all-docs-domain/src/main/java/com/jiaruiblog/domain/entity/vo/UserActivityVO.java`
- Modify: `all-docs-domain/src/main/java/com/jiaruiblog/domain/entity/vo/StatsVO.java`

- [ ] **Step 1: 创建 DocTypeDistVO**

```java
package com.jiaruiblog.domain.entity.vo;

import lombok.Data;

@Data
public class DocTypeDistVO {
    private String type;
    private Long count;
}
```

- [ ] **Step 2: 创建 CategoryDistVO**

```java
package com.jiaruiblog.domain.entity.vo;

import lombok.Data;

@Data
public class CategoryDistVO {
    private String category;
    private Long count;
}
```

- [ ] **Step 3: 创建 HotDocVO**

```java
package com.jiaruiblog.domain.entity.vo;

import lombok.Data;

@Data
public class HotDocVO {
    private String id;
    private String title;
    private Long viewCount;
}
```

- [ ] **Step 4: 创建 SearchHotWordVO**

```java
package com.jiaruiblog.domain.entity.vo;

import lombok.Data;

@Data
public class SearchHotWordVO {
    private String keyword;
    private Long count;
}
```

- [ ] **Step 5: 创建 UserActivityVO**

```java
package com.jiaruiblog.domain.entity.vo;

import lombok.Data;

@Data
public class UserActivityVO {
    private String month;
    private Long activeUsers;
    private Long totalUsers;
}
```

- [ ] **Step 6: 扩展 StatsVO**

在 `StatsVO.java` 中新增 4 个字段：

```java
private Long userNum;
private Long downloadNum;
private Long searchNum;
private Long viewNum;
```

---

## Chunk 2: Infrastructure 层 - MyBatis Mapper 扩展

**Files:**
- Modify: `all-docs-infrastructure/src/main/java/com/jiaruiblog/infrastructure/repository/mysql/DocumentMapper.java`
- Modify: `all-docs-infrastructure/src/main/resources/mapper/DocumentMapper.xml`
- Modify: `all-docs-infrastructure/src/main/java/com/jiaruiblog/infrastructure/repository/mysql/DocumentMybatisRepository.java`
- Modify: `all-docs-infrastructure/src/main/java/com/jiaruiblog/infrastructure/repository/DocumentRepository.java`

- [ ] **Step 1: 在 DocumentMapper.java 新增方法声明**

```java
List<Map<String, Object>> countByDocType();
List<Map<String, Object>> countByCategory();
```

- [ ] **Step 2: 在 DocumentMapper.xml 新增 SQL**

在 `<mapper>` 中添加：

```xml
<select id="countByDocType" resultType="java.util.Map">
    SELECT suffix AS type, COUNT(*) AS count
    FROM file_document
    WHERE suffix IS NOT NULL AND suffix != ''
    GROUP BY suffix
</select>

<select id="countByCategory" resultType="java.util.Map">
    SELECT c.name AS category, COUNT(cdr.file_id) AS count
    FROM cate_doc_relationship cdr
    LEFT JOIN category c ON cdr.category_id = c.id
    GROUP BY c.id, c.name
</select>
```

- [ ] **Step 3: 在 DocumentMybatisRepository.java 实现方法**

```java
@Override
public List<Map<String, Object>> countByDocType() {
    return documentMapper.countByDocType();
}

@Override
public List<Map<String, Object>> countByCategory() {
    return documentMapper.countByCategory();
}
```

- [ ] **Step 4: 在 DocumentRepository.java 接口新增方法声明**

```java
List<Map<String, Object>> countByDocType();
List<Map<String, Object>> countByCategory();
```

---

## Chunk 3: Application 层 - Service 接口与实现

**Files:**
- Modify: `all-docs-application/src/main/java/com/jiaruiblog/application/service/StatisticsService.java`
- Modify: `all-docs-application/src/main/java/com/jiaruiblog/application/service/impl/StatisticsServiceImpl.java`

- [ ] **Step 1: 在 StatisticsService.java 新增方法声明**

```java
List<DocTypeDistVO> docTypeDist();
List<CategoryDistVO> categoryDist();
List<HotDocVO> hotDocs();
List<SearchHotWordVO> searchHotWords();
List<UserActivityVO> userActivity();
```

- [ ] **Step 2: 在 StatisticsServiceImpl.java 实现扩展的 all() 方法**

修改 `all()` 方法，新增字段：

```java
@Override
public StatsVO all() {
    StatsVO vo = new StatsVO();
    vo.setDocNum(countDocument());
    vo.setUserNum(userRepository.count());  // 新增
    vo.setCategoryNum(countCategory());
    vo.setTagNum(countTag());
    vo.setCommentNum(commentRepository.count());
    // 以下暂无数据源，返回 0
    vo.setDownloadNum(0L);
    vo.setSearchNum(0L);
    vo.setViewNum(0L);
    return vo;
}
```

- [ ] **Step 3: 实现 docTypeDist()**

```java
@Override
public List<DocTypeDistVO> docTypeDist() {
    List<Map<String, Object>> rawList = documentRepository.countByDocType();
    List<DocTypeDistVO> result = new ArrayList<>();
    for (Map<String, Object> map : rawList) {
        DocTypeDistVO vo = new DocTypeDistVO();
        vo.setType((String) map.get("type"));
        vo.setCount(((Number) map.get("count")).longValue());
        result.add(vo);
    }
    return result;
}
```

- [ ] **Step 4: 实现 categoryDist()**

```java
@Override
public List<CategoryDistVO> categoryDist() {
    List<Map<String, Object>> rawList = documentRepository.countByCategory();
    List<CategoryDistVO> result = new ArrayList<>();
    for (Map<String, Object> map : rawList) {
        CategoryDistVO vo = new CategoryDistVO();
        vo.setCategory((String) map.get("category"));
        Object countObj = map.get("count");
        vo.setCount(countObj != null ? ((Number) countObj).longValue() : 0L);
        result.add(vo);
    }
    return result;
}
```

- [ ] **Step 5: 实现 hotDocs()**

```java
@Override
public List<HotDocVO> hotDocs() {
    List<String> docIdList = redisService.getHotList(null, RedisServiceImpl.DOC_KEY);
    List<HotDocVO> result = new ArrayList<>();
    if (docIdList == null || docIdList.isEmpty()) {
        return result;
    }
    int limit = Math.min(docIdList.size(), 10);
    for (int i = 0; i < limit; i++) {
        String docId = docIdList.get(i);
        FileDocument doc = documentRepository.queryById(docId);
        if (doc == null) {
            continue;
        }
        HotDocVO vo = new HotDocVO();
        vo.setId(docId);
        vo.setTitle(doc.getName());
        vo.setViewCount((long) redisService.score(RedisServiceImpl.DOC_KEY, docId));
        result.add(vo);
    }
    return result;
}
```

- [ ] **Step 6: 实现 searchHotWords()**

```java
@Override
public List<SearchHotWordVO> searchHotWords() {
    List<String> hotList = redisService.getHotList(null, RedisServiceImpl.SEARCH_KEY);
    List<SearchHotWordVO> result = new ArrayList<>();
    if (hotList == null || hotList.isEmpty()) {
        return result;
    }
    int limit = Math.min(hotList.size(), 10);
    for (int i = 0; i < limit; i++) {
        String keyword = hotList.get(i);
        SearchHotWordVO vo = new SearchHotWordVO();
        vo.setKeyword(keyword);
        vo.setCount((long) redisService.score(RedisServiceImpl.SEARCH_KEY, keyword));
        result.add(vo);
    }
    return result;
}
```

- [ ] **Step 7: 实现 userActivity()**

```java
@Override
public List<UserActivityVO> userActivity() {
    // 暂无数据源，返回空列表
    return new ArrayList<>();
}
```

---

## Chunk 4: API 层 - Controller 端点

**Files:**
- Modify: `all-docs-api/src/main/java/com/jiaruiblog/api/controller/StatisticsController.java`

- [ ] **Step 1: 新增 docTypeDist 端点**

```java
@Operation(summary = "文档类型分布", description = "查询各文档类型的数量分布")
@GetMapping("/docTypeDist")
public ApiResult<List<DocTypeDistVO>> docTypeDist() {
    return ApiResult.success(statisticsService.docTypeDist());
}
```

- [ ] **Step 2: 新增 categoryDist 端点**

```java
@Operation(summary = "分类文档分布", description = "查询各分类下的文档数量")
@GetMapping("/categoryDist")
public ApiResult<List<CategoryDistVO>> categoryDist() {
    return ApiResult.success(statisticsService.categoryDist());
}
```

- [ ] **Step 3: 新增 hotDocs 端点**

```java
@Operation(summary = "热门文档 TOP 10", description = "查询热门文档排行")
@GetMapping("/hotDocs")
public ApiResult<List<HotDocVO>> hotDocs() {
    return ApiResult.success(statisticsService.hotDocs());
}
```

- [ ] **Step 4: 新增 searchHotWords 端点**

```java
@Operation(summary = "搜索热词排行", description = "查询搜索热词排行")
@GetMapping("/searchHotWords")
public ApiResult<List<SearchHotWordVO>> searchHotWords() {
    return ApiResult.success(statisticsService.searchHotWords());
}
```

- [ ] **Step 5: 新增 userActivity 端点**

```java
@Operation(summary = "用户活跃度趋势", description = "查询用户活跃度趋势")
@GetMapping("/userActivity")
public ApiResult<List<UserActivityVO>> userActivity() {
    return ApiResult.success(statisticsService.userActivity());
}
```

---

## Chunk 5: 创建 TODO 文档

**Files:**
- Create: `C:\Project\java\all-docs\TODO.md`

- [ ] **Step 1: 创建 TODO.md**

```markdown
# 待补充功能

以下功能在 Statistics API 扩展中暂未实现，后续补充：

## 1. 浏览量统计 (viewNum)

**目标：** 在 `/statistics/all` 接口中返回真实的文档浏览次数

**方案：** 在文档浏览接口中增加 Redis ZSet 计数，key 为 `doc:hot`

**待办：**
- [ ] 在文档浏览接口中调用 `redisService.incrementDocScore(docId, 1)`
- [ ] 验证 `doc:hot` ZSet 是否正常更新

## 2. 下载次数统计 (downloadNum)

**目标：** 在 `/statistics/all` 接口中返回真实的文档下载次数

**方案：** 在文档下载接口中增加 Redis 计数

**待办：**
- [ ] 确定下载接口位置
- [ ] 增加下载计数逻辑
- [ ] 在 `all()` 方法中聚合下载次数

## 3. 搜索次数统计 (searchNum)

**目标：** 在 `/statistics/all` 接口中返回真实的搜索次数

**方案：** 使用 Redis ZSet `search:hot` 的总分数作为搜索次数

**待办：**
- [ ] 在 `all()` 方法中聚合 `search:hot` ZSet 的总分

## 4. 用户活跃度统计 (userActivity)

**目标：** 实现 `/statistics/userActivity` 接口

**方案：** 在用户登录/关键操作时记录活跃状态，按月聚合

**待办：**
- [ ] 设计用户活跃度记录机制（Redis 或数据库）
- [ ] 实现按月统计活跃用户数
- [ ] 实现 `userActivity()` 方法
```
