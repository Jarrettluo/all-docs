# 文档搜索接口重构设计方案

## 一、背景

根据 `接口文档.md` 的规范，改造现有的 `/api/v1/document/search` 接口，支持更丰富的搜索功能。

## 二、接口规范

### 2.1 接口信息

- **接口地址**: `/api/v1/document/search`
- **请求方式**: `POST`
- **Content-Type**: `application/json`
- **认证**: 需要登录（token）

### 2.2 请求参数

```json
{
  "keyword": "项目",
  "fullText": true,
  "segment": true,
  "searchType": "all",
  "tags": ["pdf", "docx"],
  "category": "技术文档",
  "sortField": "createTime",
  "sortOrder": "desc",
  "page": 1,
  "pageSize": 20
}
```

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|-------|------|------|--------|------|
| keyword | string | 否 | "" | 搜索关键词 |
| fullText | boolean | 否 | false | 是否全文检索（true: 匹配名称+描述+分类；false: 只匹配名称） |
| segment | boolean | 否 | false | 是否分词（true: 开启中文分词搜索） |
| searchType | string | 否 | "all" | 搜索类型：`all`(全部)、`name`(仅名称)、`description`(仅描述) |
| tags | string[] | 否 | [] | 标签筛选数组，传入 tag 的 name |
| category | string | 否 | "" | 分类名称筛选，传空字符串表示不限分类 |
| sortField | string | 否 | "createTime" | 排序字段：`name`、`size`、`type`、`category`、`createTime` |
| sortOrder | string | 否 | "desc" | 排序方向：`asc`(升序)、`desc`(降序) |
| page | int | 否 | 1 | 页码，从 1 开始 |
| pageSize | int | 否 | 20 | 每页条数 |

### 2.3 响应参数

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "id": "doc_001",
        "name": "项目需求文档.pdf",
        "type": "pdf",
        "size": 2048576,
        "sizeDisplay": "2 MB",
        "description": "2024年Q1项目需求文档，包含功能清单和里程碑",
        "category": "需求文档",
        "tags": [
          { "name": "重要", "color": "red" },
          { "name": "2024", "color": "blue" }
        ],
        "liked": false,
        "collected": true,
        "createTime": "2024-01-15 10:30:00",
        "updateTime": "2024-01-20 14:22:00"
      }
    ],
    "total": 100,
    "page": 1,
    "pageSize": 20
  }
}
```

| 字段名 | 类型 | 说明 |
|-------|------|------|
| list | array | 文档列表 |
| list[].id | string | 文档ID |
| list[].name | string | 文档名称（带后缀） |
| list[].type | string | 文档类型（pdf/docx/xlsx/pptx/image/zip） |
| list[].size | long | 文档大小（字节） |
| list[].sizeDisplay | string | 文档大小（格式化，如 "2 MB"） |
| list[].description | string | 文档描述 |
| list[].category | string | 所属分类名称 |
| list[].tags | array | 标签列表 |
| list[].tags[].name | string | 标签名称 |
| list[].tags[].color | string | 标签颜色 |
| list[].liked | boolean | 当前用户是否已点赞 |
| list[].collected | boolean | 当前用户是否已收藏 |
| list[].createTime | string | 创建时间（yyyy-MM-dd HH:mm:ss） |
| list[].updateTime | string | 更新时间（yyyy-MM-dd HH:mm:ss） |
| total | int | 总记录数 |
| page | int | 当前页码 |
| pageSize | int | 每页条数 |

## 三、数据模型改动

### 3.1 Tag 实体新增 color 字段

**文件**: `all-docs-domain/src/main/java/com/jiaruiblog/domain/entity/po/Tag.java`

```java
@Data
public class Tag {
    protected String id;
    protected String name;
    protected String color;  // 新增
    protected Date createDate;
    protected Date updateDate;
}
```

**说明**:
- `color` 字段用于存储标签颜色
- 新增标签时传入颜色值，不传则使用默认颜色
- 查询时返回 `name` 和 `color`

### 3.2 SearchDocument 新增 ES 字段

**文件**: `all-docs-domain/src/main/java/com/jiaruiblog/domain/entity/po/SearchDocument.java`

```java
@Slf4j
@Data
@Document(indexName = "all_docs_document_index")
public class SearchDocument {

    @Id
    @Field(type = FieldType.Keyword)
    private String id;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String name;

    @Field(type = FieldType.Keyword)
    private String type;

    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String content;

    // 新增字段
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private List<String> tagNames;  // 标签名字列表

    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String categoryName;  // 分类名字
}
```

## 四、业务逻辑流程

```
1. 用户登录 → 获取 userId
2. ES 检索：
   - fullText=true → 组合条件：name + content + tagNames + categoryName
   - fullText=false → 只搜索 name
   - segment=true → 使用 ik_max_word 分词器
   - searchType=all → 搜索所有字段
   - searchType=name → 只搜索 name
   - searchType=description → 搜索 content
3. MySQL/关系表过滤：
   - tags 筛选 → 通过 TagDocRelationship + Tag 查询匹配文档
   - category 筛选 → 通过 CateDocRelationship + Category 查询匹配文档
4. 排序：sortField + sortOrder
5. 分页：page + pageSize
6. 组装结果：
   - liked → 查询 like 表（当前用户是否点赞该文档）
   - collected → 查询 collect 表（当前用户是否收藏该文档）
   - tags → 查询 TagDocRelationship + Tag，返回 TagVO 列表 (name + color)
```

## 五、文件改动清单

| 序号 | 文件 | 改动内容 |
|------|------|---------|
| 1 | `Tag.java` | 新增 `color` 字段 |
| 2 | `SearchDocument.java` | 新增 `tagNames` 和 `categoryName` 字段 |
| 3 | `DocumentDTO.java` | 新增搜索参数字段 |
| 4 | `DocumentController.java` | GET→POST，添加认证，调用新逻辑 |
| 5 | `DocumentService.java` | 接口定义扩展 |
| 6 | `DocumentServiceImpl.java` | 实现新搜索逻辑 |
| 7 | `ElasticService.java` | 接口定义扩展 |
| 8 | `ElasticServiceImpl.java` | 实现新 ES 检索逻辑 |
| 9 | `LikeController.java` | 复用已有点赞接口 |
| 10 | `CollectController.java` | 复用已有收藏接口 |

## 六、依赖关系

```
DocumentController
    ↓
DocumentService.search(SearchQuery)
    ↓
┌─────────────────────────────────────┐
│  ElasticService.searchIds()        │ ← ES 检索文档ID
│  TagRepository.findFileIdsByTags()  │ ← 标签过滤
│  CategoryService.getDocIds()       │ ← 分类过滤
│  LikeService.isLiked()             │ ← 点赞状态
│  CollectService.isCollected()       │ ← 收藏状态
│  TagService.getTagsByDocId()        │ ← 标签列表
└─────────────────────────────────────┘
```

## 七、实现任务分解

### 任务 1: 数据模型改动
- Tag.java 新增 color 字段
- SearchDocument.java 新增 tagNames 和 categoryName 字段

### 任务 2: DTO/VO 改动
- 新建 SearchQuery.java 请求参数类
- 新建 DocSearchVO.java 响应VO类
- DocumentDTO.java 新增字段

### 任务 3: ES 检索逻辑
- ElasticService 新增 searchDocuments 方法
- ElasticServiceImpl 实现多条件检索

### 任务 4: Service 层改动
- DocumentService.search 扩展支持多条件
- DocumentServiceImpl 实现完整搜索流程

### 任务 5: Controller 层改动
- DocumentController.search GET→POST
- 添加认证注解
- 参数解析和响应组装

### 任务 6: ES 索引同步
- 文档上传/更新时同步更新 ES 的 tagNames 和 categoryName
- 文档删除时同步删除 ES 索引
