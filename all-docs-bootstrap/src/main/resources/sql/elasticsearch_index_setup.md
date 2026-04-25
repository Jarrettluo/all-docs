# Elasticsearch 索引创建指南

## 全文档系统 (All-Docs) ES 索引评审与配置

---

## 一、代码评审

### 1.1 索引配置

**索引名称**: `all_docs_all_docs_document_index`

**实体**: `SearchDocument` (对应 `com.jiaruiblog.domain.entity.po.SearchDocument`)

**字段映射分析**:

| 字段 | ES类型 | 分词器 | 说明 |
|------|--------|--------|------|
| `id` | Keyword | - | 精确匹配，使用文件 MD5 |
| `name` | Text | ik_max_word | 中文分词，用于文件名检索 |
| `type` | Keyword | - | 精确匹配，文件类型筛选 |
| `content` | Text | ik_smart | 中文分词，内容检索（存储原始文本） |

### 1.2 发现并修复的问题

#### 问题1: content字段存储base64编码 ✅ 已修复

**原因**: `TaskExecutor.uploadFileToEs()` 中 `fileObj.readFile(textFilePath)` 对已提取的纯文本文本做了 base64 编码。

**修复方案**: 直接使用 `Files.readString()` 读取文本文件内容，以 UTF-8 字符串直接存入 `content` 字段，不再 base64 编码。

#### 问题2: getWordStat() 未实现 ✅ 已修复

**原因**: 原实现只返回了索引基本信息，未返回真正的词云统计数据。

**修复方案**: 使用 ES 的 `CriteriaQuery` 遍历所有文档内容，进行词频统计，返回 top 100 高频词。

#### 问题3: 命名不当 ✅ 已修复

- `FileObj` → `SearchDocument`：与 MySQL 的 `FileDocument` 区分
- `docwrite` → `all_docs_document_index`：语义更清晰

#### 问题4: 缺少 @Document 注解 ✅ 已修复

`SearchDocument` 类已添加 `@Document(indexName = "all_docs_document_index")` 注解，实体与索引名绑定。

---

## 二、索引创建命令

### 2.1 前置条件

确保已安装 Elasticsearch 和 IK 分词器插件:

```bash
# 检查ES状态
curl -X GET "localhost:9200/_cluster/health?pretty"

# 安装 IK 分词器 (如果尚未安装)
# 下载对应版本的 IK 分词器: https://github.com/medcl/elasticsearch-analysis-ik
./bin/elasticsearch-plugin install https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v7.9.3/elasticsearch-analysis-ik-7.9.3.zip
```

### 2.2 删除旧索引 (如需要)

```bash
# 删除已存在的旧索引 (如果有)
curl -X DELETE "localhost:9200/all_docs_document_index?pretty"
# 如果之前使用的是旧索引名 docwrite
curl -X DELETE "localhost:9200/docwrite?pretty"
```

### 2.3 创建 all_docs_document_index 索引

```bash
curl -X PUT "localhost:9200/all_docs_document_index?pretty" -H "Content-Type: application/json" -d @all_docs_document_index_mapping.json
```

### 2.4 验证索引

```bash
# 查看索引信息
curl -X GET "localhost:9200/all_docs_document_index?pretty"

# 查看索引字段映射
curl -X GET "localhost:9200/all_docs_document_index/_mapping?pretty"

# 检查分词器效果
curl -X POST "localhost:9200/all_docs_document_index/_analyze?pretty" -H "Content-Type: application/json" -d "{\"analyzer\":\"ik_max_word\",\"text\":\"这是一个中文文档测试\"}"
```

### 2.5 测试文档操作

```bash
# 索引一篇测试文档
curl -X POST "localhost:9200/all_docs_document_index/_doc/abc123?pretty" -H "Content-Type: application/json" -d '
{
  "id": "abc123",
  "name": "测试文档",
  "type": "txt",
  "content": "这是一个测试文档的内容，主要用于验证中文分词是否正常工作"
}'

# 搜索测试
curl -X POST "localhost:9200/all_docs_document_index/_search?pretty" -H "Content-Type: application/json" -d '
{
  "query": {
    "multi_match": {
      "query": "测试",
      "fields": ["content", "name"]
    }
  }
}'

# 词云统计测试 (调用接口)
# GET /statistics

# 删除测试文档
curl -X DELETE "localhost:9200/all_docs_document_index/_doc/abc123?pretty"
```

---

## 三、索引映射 JSON 文件

创建文件 `all_docs_document_index_mapping.json` (放在 `sql/` 目录下):

```json
{
  "settings": {
    "number_of_shards": 3,
    "number_of_replicas": 1,
    "analysis": {
      "analyzer": {
        "ik_max_word_analyzer": {
          "type": "custom",
          "tokenizer": "ik_max_word",
          "filter": ["lowercase"]
        },
        "ik_smart_analyzer": {
          "type": "custom",
          "tokenizer": "ik_smart",
          "filter": ["lowercase"]
        }
      }
    }
  },
  "mappings": {
    "properties": {
      "id": {
        "type": "keyword"
      },
      "name": {
        "type": "text",
        "analyzer": "ik_max_word",
        "search_analyzer": "ik_smart",
        "fields": {
          "keyword": {
            "type": "keyword",
            "ignore_above": 256
          }
        }
      },
      "type": {
        "type": "keyword"
      },
      "content": {
        "type": "text",
        "analyzer": "ik_smart",
        "search_analyzer": "ik_smart"
      }
    }
  }
}
```

---

## 四、ES 实体类说明

`SearchDocument` 类位置: `all-docs-domain/src/main/java/com/jiaruiblog/domain/entity/po/SearchDocument.java`

```java
@Document(indexName = "all_docs_document_index")
public class SearchDocument {
    @Id
    @Field(type = FieldType.Keyword)
    private String id;        // MD5

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String name;     // 文件名

    @Field(type = FieldType.Keyword)
    private String type;     // 文件类型

    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String content;  // 原始文本内容 (不再 base64 编码)
}
```

---

## 五、常见问题排查

```bash
# 1. 检查IK分词器是否安装
./bin/elasticsearch-plugin list

# 2. 查看ES日志定位问题
tail -f /path/to/elasticsearch/logs/elasticsearch.log

# 3. 检查索引健康状态
curl -X GET "localhost:9200/_cluster/health/all_docs_document_index?pretty"

# 4. 重新设置分片数 (索引创建后不可更改主分片数)
# 如需修改，需要 reindex

# 5. 确认文档内容未 base64 编码
curl -X GET "localhost:9200/all_docs_document_index/_doc/<doc_id>?pretty"
# content 字段应显示原始中文文本，而非 base64 字符串
```

---

## 六、环境变量参考

| 变量名 | 默认值 (dev) | 说明 |
|--------|--------------|------|
| ES_HOST | 192.168.1.29 | Elasticsearch 主机 |
| ES_PORT | 1200 | Elasticsearch 端口 |

---

**文档版本**: v2.0
**最后更新**: 2026-04-25
**适用版本**: All-Docs 系统
