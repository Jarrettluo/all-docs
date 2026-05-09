# 文档统计页面后端接口扩展设计

## 1. 概述

为前端统计页面扩展后端 API，补全统计卡片数据，新增文档类型分布、分类文档分布、热门文档、搜索热词等接口。

---

## 2. 现有接口扩展

### 2.1 GET /statistics/all

**扩展 `StatsVO` 字段：**

| 字段 | 类型 | 说明 | 数据来源 |
|------|------|------|----------|
| docNum | Long | 文档总数 | `DocumentRepository.count()` |
| userNum | Long | 用户总数 | `UserRepository.count()` |
| downloadNum | Long | 下载次数 | **暂无，返回 0** |
| searchNum | Long | 搜索次数 | **暂无，返回 0** |
| commentNum | Long | 评论总数 | `CommentRepository.count()` |
| tagNum | Long | 标签总数 | `TagRepository.count()` |
| categoryNum | Long | 分类总数 | `CategoryRepository.count()` |
| viewNum | Long | 浏览次数 | **暂无，返回 0** |

---

## 3. 新增接口

### 3.1 GET /statistics/docTypeDist

**说明：** 统计各文档类型的数量分布

**响应：**
```json
{
  "code": 200,
  "data": [
    { "type": "pdf", "count": 439 },
    { "type": "docx", "count": 352 }
  ]
}
```

**新增 VO：** `DocTypeDistVO(type, count)`

**数据来源：** `file_document.suffix` GROUP BY

---

### 3.2 GET /statistics/categoryDist

**说明：** 统计各分类下的文档数量

**响应：**
```json
{
  "code": 200,
  "data": [
    { "category": "产品文档", "count": 286 },
    { "category": "技术文档", "count": 342 }
  ]
}
```

**新增 VO：** `CategoryDistVO(category, count)`

**数据来源：** `cate_doc_relationship` + `category` JOIN，COUNT GROUP BY category

---

### 3.3 GET /statistics/hotDocs

**说明：** 返回热门文档 TOP 10

**响应：**
```json
{
  "code": 200,
  "data": [
    { "id": 1, "title": "2024年产品路线图.pdf", "viewCount": 2456 },
    { "id": 2, "title": "系统架构设计文档.docx", "viewCount": 2134 }
  ]
}
```

**新增 VO：** `HotDocVO(id, title, viewCount)`

**数据来源：** Redis `doc:hot` ZSet，取 TOP 10，再根据 docId 查 `file_document` 表获取 title

---

### 3.4 GET /statistics/searchHotWords

**说明：** 返回搜索热词排行

**响应：**
```json
{
  "code": 200,
  "data": [
    { "keyword": "架构设计", "count": 3421 },
    { "keyword": "产品路线图", "count": 2876 }
  ]
}
```

**新增 VO：** `SearchHotWordVO(keyword, count)`

**数据来源：** Redis `search:hot` ZSet，取 TOP 10

---

### 3.5 GET /statistics/userActivity

**说明：** 用户活跃度趋势

**响应：**
```json
{
  "code": 200,
  "data": [
    { "month": "2024-01", "activeUsers": 156, "totalUsers": 180 }
  ]
}
```

**新增 VO：** `UserActivityVO(month, activeUsers, totalUsers)`

**数据来源：** **暂无，返回空列表**

---

## 4. 新增 VO 类清单

| 类名 | 包路径 | 字段 |
|------|--------|------|
| DocTypeDistVO | `domain/entity/vo/` | type(String), count(Long) |
| CategoryDistVO | `domain/entity/vo/` | category(String), count(Long) |
| HotDocVO | `domain/entity/vo/` | id(String), title(String), viewCount(Long) |
| SearchHotWordVO | `domain/entity/vo/` | keyword(String), count(Long) |
| UserActivityVO | `domain/entity/vo/` | month(String), activeUsers(Long), totalUsers(Long) |

---

## 5. 修改文件清单

### 5.1 Domain 层
- `StatsVO.java` - 新增 userNum, downloadNum, searchNum, viewNum 字段

### 5.2 Application 层
- `StatisticsService.java` - 新增 5 个方法声明
- `StatisticsServiceImpl.java` - 实现新增方法

### 5.3 API 层
- `StatisticsController.java` - 新增 5 个端点

### 5.4 Infrastructure 层
- `DocumentMapper.xml` - 新增 SQL 查询（docTypeDist, categoryDist）
- `DocumentMapper.java` - 新增 mapper 方法
- `DocumentMybatisRepository.java` - 新增 repository 方法

---

## 6. 待补充功能（记录到 TODO.md）

以下功能本次不做实现，后续补充：

1. **浏览量统计（viewNum）** - 需要在文档浏览接口中增加 Redis/DB 计数
2. **下载次数统计（downloadNum）** - 需要在文档下载接口中增加 Redis/DB 计数
3. **搜索次数统计（searchNum）** - 需要在搜索接口中增加计数
4. **用户活跃度统计（userActivity）** - 需要新增用户活跃度记录机制

---

## 7. 风险与约束

- 浏览量、下载次数、搜索次数、用户活跃度目前无数据源，返回 0 或空列表
- 热门文档依赖 Redis `doc:hot` ZSet，需确认文档浏览时是否更新该 ZSet
- 搜索热词依赖 Redis `search:hot` ZSet，该机制已存在
