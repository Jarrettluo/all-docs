# 文档统计页面 - 后端接口需求

## 概述

文档统计页面需要对后端API进行扩展和完善，以下是各模块需要的数据结构说明。

---

## 1. 统计卡片数据

**接口:** `GET /statistics/all`

**响应:**
```json
{
  "code": 200,
  "data": {
    "docNum": 1256,
    "userNum": 342,
    "downloadNum": 8965,
    "searchNum": 23589,
    "commentNum": 1856,
    "tagNum": 89,
    "categoryNum": 12,
    "viewNum": 45892
  }
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| docNum | number | 文档总数 |
| userNum | number | 用户总数 |
| downloadNum | number | 下载次数 |
| searchNum | number | 搜索次数 |
| commentNum | number | 评论总数 |
| tagNum | number | 标签总数 |
| categoryNum | number | 分类总数 |
| viewNum | number | 浏览次数 |

---

## 2. 月度文档上传趋势

**接口:** `GET /statistics/monthStat`

**响应:**
```json
{
  "code": 200,
  "data": [
    { "date": "2024-01", "count": 156 },
    { "date": "2024-02", "count": 198 },
    { "date": "2024-03", "count": 245 }
  ]
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| date | string | 月份，格式: YYYY-MM |
| count | number | 该月上传文档数量 |

---

## 3. 文档类型分布

**接口:** `GET /statistics/docTypeDist`

**响应:**
```json
{
  "code": 200,
  "data": [
    { "type": "pdf", "count": 439 },
    { "type": "docx", "count": 352 },
    { "type": "xlsx", "count": 226 },
    { "type": "pptx", "count": 151 },
    { "type": "other", "count": 88 }
  ]
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| type | string | 文档类型: pdf/docx/xlsx/pptx/other |
| count | number | 该类型文档数量 |

---

## 4. 分类文档分布

**接口:** `GET /statistics/categoryDist`

**响应:**
```json
{
  "code": 200,
  "data": [
    { "category": "产品文档", "count": 286 },
    { "category": "技术文档", "count": 342 },
    { "category": "需求文档", "count": 198 },
    { "category": "运维文档", "count": 156 },
    { "category": "测试文档", "count": 124 },
    { "category": "用户手册", "count": 150 }
  ]
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| category | string | 分类名称 |
| count | number | 该分类文档数量 |

---

## 5. 热门文档 TOP 10

**接口:** `GET /statistics/hotDocs`

**响应:**
```json
{
  "code": 200,
  "data": [
    { "id": 1, "title": "2024年产品路线图.pdf", "viewCount": 2456 },
    { "id": 2, "title": "系统架构设计文档.docx", "viewCount": 2134 }
  ]
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| id | number | 文档ID |
| title | string | 文档标题 |
| viewCount | number | 浏览次数 |

---

## 6. 搜索热词排行

**接口:** `GET /statistics/searchHotWords`

**响应:**
```json
{
  "code": 200,
  "data": [
    { "keyword": "架构设计", "count": 3421 },
    { "keyword": "产品路线图", "count": 2876 },
    { "keyword": "API文档", "count": 2543 },
    { "keyword": "财务报告", "count": 2234 },
    { "keyword": "用户手册", "count": 1987 },
    { "keyword": "测试用例", "count": 1765 },
    { "keyword": "部署手册", "count": 1543 },
    { "keyword": "需求调研", "count": 1432 }
  ]
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| keyword | string | 搜索关键词 |
| count | number | 搜索次数 |

---

## 7. 用户活跃度趋势

**接口:** `GET /statistics/userActivity`

**响应:**
```json
{
  "code": 200,
  "data": [
    { "month": "2024-01", "activeUsers": 156, "totalUsers": 180 },
    { "month": "2024-02", "activeUsers": 178, "totalUsers": 195 }
  ]
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| month | string | 月份，格式: YYYY-MM |
| activeUsers | number | 活跃用户数 |
| totalUsers | number | 总用户数 |

---

## 8. 最新文档列表

**接口:** `GET /statistics/recentDocs`

**响应:**
```json
{
  "code": 200,
  "data": [
    { "id": 1, "name": "2024年产品路线图.pdf", "type": "pdf", "date": "2024-04-28" },
    { "id": 2, "name": "系统架构设计文档.docx", "type": "docx", "date": "2024-04-27" },
    { "id": 3, "name": "Q1财务数据统计.xlsx", "type": "xlsx", "date": "2024-04-26" }
  ]
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| id | number | 文档ID |
| name | string | 文档名称 |
| type | string | 文档类型: pdf/docx/xlsx/pptx/image/zip |
| date | string | 上传日期，格式: YYYY-MM-DD |

---

## 9. 新增后端API清单

| 接口 | 方法 | 说明 |
|------|------|------|
| /statistics/docTypeDist | GET | 文档类型分布 |
| /statistics/categoryDist | GET | 分类文档分布 |
| /statistics/hotDocs | GET | 热门文档 TOP 10 |
| /statistics/searchHotWords | GET | 搜索热词排行 |
| /statistics/userActivity | GET | 用户活跃度趋势 |
