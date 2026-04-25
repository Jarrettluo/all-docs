# ES 文档审核 + 解析 + 检索 集成设计方案

## 1. 背景与目标

当前系统 ES 功能断裂：
- ES 写入入口（TaskExecutor.uploadFileToEs）存在，但文字提取大多未实现
- ES 检索（ElasticService.search）独立存在，未与 API 层串联
- 文档审核流程（DocReview）与解析流程（TaskExecuteService）未串联

目标：
1. 实现审核通过后才触发解析的完整流程
2. 将 ES 检索接入 DocumentController，作为核心搜索功能
3. 完善各 Executor 的文字提取实现（TxtExecutor 已实现，可复刻）

---

## 2. 文档状态机

| 阶段 | `reviewing` | `docState` | 含义 |
|------|-------------|------------|------|
| 上传后 | `true` | `WAIT` | 待审核 |
| 审核通过 | `false` | `WAIT` | 审核通过，待解析 |
| 解析中 | `false` | `ON_PROCESS` | 正在解析/写入ES |
| 解析成功 | `false` | `SUCCESS` | 已解析且已写入ES |
| 解析失败 | `false` | `FAIL` | 解析失败 |

---

## 3. 核心流程

### 3.1 文档上传
```
FileController.documentUpload()
  → FileDocument 创建 (docState=WAIT, reviewing=true)
  → DocReview 记录创建 (checkState=待审核)
  → 不触发解析任务
```

### 3.2 管理员审核通过
```
DocReviewController.approve() / approveBatch()
  → DocReviewService.approve(docId)
    → FileDocument.reviewing = false
    → FileDocument.docState = WAIT
    → TaskExecuteService.execute(fileDocument)  ← 直接触发解析
```

### 3.3 异步解析线程池
```
TaskExecuteService.execute(fileDocument)
  → MainTask 提交到 TaskThreadPool
  → MainTask.run():
      1. docState = ON_PROCESS
      2. TaskExecutor.readText()        ← 提取文字（各子类实现）
      3. uploadFileToEs()               ← 写入 ES（基类实现）
      4. docState = SUCCESS 或 FAIL
```

### 3.4 ES 检索串联
```
DocumentController.search(keyword, page, size)
  → ElasticService.search(keyword, page, size)
  → 获取匹配的 docId 列表
  → 二次查询 MySQL：WHERE id IN (...) AND reviewing=false AND docState=SUCCESS
  → 返回标准文档列表结构（复用 list 渲染逻辑）
```

---

## 4. 需修改的文件清单

### 4.1 审核 → 解析串联
- `DocReviewServiceImpl.approve()` — 添加 `reviewing=false`、`docState=WAIT`、触发 `TaskExecuteService.execute()`
- `DocReviewServiceImpl.approveBatch()` — 同上，批量处理

### 4.2 状态机修复
- `DocReviewServiceImpl.approve()` — 确认 `reviewing=false` 正确设置
- `DocReviewServiceImpl.refuse()` — 确认 `reviewing=false`、`docState=FAIL`

### 4.3 ES Service 增强
- `ElasticService` — 增加 `update(SearchDocument)` 方法
- `ElasticServiceImpl` — 实现 update（先 delete 再 upload，或直接覆盖）
- `ElasticServiceImpl.upload()` — 处理 index 不存在时的创建

### 4.4 DocumentController 搜索入口
- `DocumentController` — 新增 `GET /api/v1/document/search?keyword=&page=&size=`
- 返回结构复用 `DocumentController.list` 的 VO 结构

### 4.5 Executor 文字提取实现
- `PdfWordTaskExecutor.readText()` — 使用 Apache Tika 解析 PDF
- `DocxExecutor.readText()` — 使用 Apache POI/Tika 解析 DOCX/XLSX
- `PptExecutor` / `PptxExecutor` — 使用 Apache POI 解析 PPT/PPTX
- `PptxExecutor.makeThumb()` — 使用 Apache POI 生成缩略图

### 4.6 重构触发解析调用链
- `TaskExecuteServiceImpl.execute(FileDocument)` — 确认可接受已存在的 FileDocument
- `TaskExecutorFactory` — 确认各类型路由正确

---

## 5. ES 索引设计

```json
{
  "all_docs_document_index": {
    "mappings": {
      "properties": {
        "id":    { "type": "keyword" },
        "name":  { "type": "text", "analyzer": "ik_max_word" },
        "type":  { "type": "keyword" },
        "content": { "type": "text", "analyzer": "ik_smart" }
      }
    }
  }
}
```

---

## 6. API 设计

### 6.1 搜索接口
```
GET /api/v1/document/search?keyword=关键词&page=1&size=10

Response:
{
  "code": 200,
  "data": {
    "total": 100,
    "list": [
      {
        "id": "docId",
        "name": "文档名称",
        "type": "PDF",
        "createDate": "2026-04-25",
        ...
      }
    ]
  }
}
```

### 6.2 搜索过滤条件
- 只返回 `reviewing = false` 且 `docState = SUCCESS` 的文档
- 确保未审核或解析失败的文档不出现在搜索结果中

---

## 7. 错误处理

- ES 写入失败 → `docState = FAIL`，不重试（可由管理员手动触发重新解析）
- 文字提取失败 → `docState = FAIL`，异常信息记录到日志
- ES 服务不可用 → 解析任务整体失败，不阻塞文档其他流程
- 重解析机制：`DocumentController.rebuildIndex()` 已存在，可复用

---

## 8. 测试要点

1. 上传文档 → 确认 `reviewing=true`，`docState=WAIT`，无 ES 写入
2. 审核拒绝 → 确认无解析任务，状态正确
3. 审核通过 → 确认触发解析任务，`docState=ON_PROCESS` → `SUCCESS/FAIL`
4. 搜索 → 确认只返回 `reviewing=false AND docState=SUCCESS` 的文档
5. 各文件类型解析 → PDF、DOCX、XLSX、PPT、PPTX 均正确提取文字并写入 ES
