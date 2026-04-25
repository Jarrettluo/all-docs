# ES 文档审核 + 解析 + 检索 集成实施计划

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现审核通过后才触发解析写入ES的完整流程，并将ES检索串联到DocumentController作为核心搜索功能

**Architecture:** 审核通过 → `reviewing=false, docState=WAIT` → `TaskExecuteService.execute()` 提交解析任务 → 异步线程池执行 `readText()` + `uploadFileToEs()` → `docState=SUCCESS/FAIL`。搜索入口在 `DocumentController.search()`，先查ES得ID列表，再查MySQL过滤 `reviewing=false AND docState=SUCCESS`。

**Tech Stack:** Spring Boot, Elasticsearch (Spring Data), Apache Tika, Apache POI, MinIO, MyBatis

---

## Chunk 1: DocReviewServiceImpl 审核→解析串联 + 状态机修复

### 目标
- `approve()` 设置 `reviewing=false`、`docState=WAIT`，触发 `TaskExecuteService.execute()`
- `refuse()` 设置 `reviewing=false`、`docState=FAIL`
- `approveBatch()` 复用 `approve()`

### Files
- Modify: `all-docs-application/src/main/java/com/jiaruiblog/application/service/impl/DocReviewServiceImpl.java`
- Modify: `all-docs-application/src/main/java/com/jiaruiblog/application/service/impl/DocReviewServiceImpl.java` (interface injection)

- [ ] **Step 1: 在 DocReviewServiceImpl 中注入 TaskExecuteService**

在类顶部添加:
```java
import com.jiaruiblog.application.service.TaskExecuteService;

@Resource
private TaskExecuteService taskExecuteService;
```

- [ ] **Step 2: 修改 approve(FileDocument doc) 方法**

原方法:
```java
private void approve(FileDocument doc) {
    if (doc == null || doc.getId() == null) {
        return;
    }
    doc.setDocState(DocStateEnum.SUCCESS);
    documentRepository.update(doc);
    log.info("文档审核通过：docId={}", doc.getId());
}
```

修改为:
```java
private void approve(FileDocument doc) {
    if (doc == null || doc.getId() == null) {
        return;
    }
    // 设置审核完毕，状态改为 WAIT（等待解析）
    doc.setReviewing(false);
    doc.setDocState(DocStateEnum.WAIT);
    documentRepository.update(doc);
    // 触发异步解析任务
    taskExecuteService.execute(doc);
    log.info("文档审核通过，已触发解析任务：docId={}", doc.getId());
}
```

- [ ] **Step 3: 修改 refuse(FileDocument, String) 方法**

原方法:
```java
public void refuse(FileDocument fileDocument, String reason) {
    if (fileDocument == null || fileDocument.getId() == null) {
        return;
    }
    fileDocument.setDocState(DocStateEnum.FAIL);
    fileDocument.setErrorMsg(reason);
    documentRepository.update(fileDocument);
    log.info("文档审核拒绝：docId={}, reason={}", fileDocument.getId(), reason);
}
```

修改为:
```java
@Override
public void refuse(FileDocument fileDocument, String reason) {
    if (fileDocument == null || fileDocument.getId() == null) {
        return;
    }
    fileDocument.setReviewing(false);
    fileDocument.setDocState(DocStateEnum.FAIL);
    fileDocument.setErrorMsg(reason);
    documentRepository.update(fileDocument);
    log.info("文档审核拒绝：docId={}, reason={}", fileDocument.getId(), reason);
}
```

- [ ] **Step 4: 确认 approveBatch 逻辑**

approveBatch 内部调用 `approve(doc)`，会自动获得上面的改动，无需单独修改。

- [ ] **Step 5: 提交代码**

```bash
git add all-docs-application/src/main/java/com/jiaruiblog/application/service/impl/DocReviewServiceImpl.java
git commit -m "feat: 审核通过触发解析任务，修复状态机逻辑

- approve() 设置 reviewing=false, docState=WAIT 并触发 TaskExecuteService.execute()
- refuse() 设置 reviewing=false, docState=FAIL
- 遵循：先审核后解析流程"

Co-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>
```

---

## Chunk 2: ElasticServiceImpl update 方法实现

### 目标
- 实现 `updateFileObj()` 方法（当前为空），用于解析重试时更新ES内容

### Files
- Modify: `all-docs-application/src/main/java/com/jiaruiblog/application/service/impl/ElasticServiceImpl.java`

- [ ] **Step 1: 实现 updateFileObj 方法**

将空的 `updateFileObj` 方法替换为:
```java
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
```

- [ ] **Step 2: 提交代码**

```bash
git add all-docs-application/src/main/java/com/jiaruiblog/application/service/impl/ElasticServiceImpl.java
git commit -m "feat: 实现 ElasticServiceImpl.updateFileObj() 方法

用于解析重试时更新 ES 中的文档内容"

Co-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>
```

---

## Chunk 3: DocumentController 搜索入口 + DocumentServiceImpl 搜索方法

### 目标
- 在 `DocumentController` 新增 `GET /api/v1/document/search?keyword=&page=&size=` 接口
- 在 `DocumentService` 新增 `search(keyword, page, size)` 方法
- 搜索结果过滤 `reviewing=false AND docState=SUCCESS`

### Files
- Modify: `all-docs-application/src/main/java/com/jiaruiblog/application/service/DocumentService.java` (interface)
- Modify: `all-docs-application/src/main/java/com/jiaruiblog/application/service/impl/DocumentServiceImpl.java`
- Modify: `all-docs-api/src/main/java/com/jiaruiblog/api/controller/DocumentController.java`

- [ ] **Step 1: 在 DocumentService 接口添加搜索方法声明**

在 `all-docs-application/src/main/java/com/jiaruiblog/application/service/DocumentService.java` 中找到 `list(DocumentDTO)` 方法声明，在其上方添加:
```java
/**
 * 根据关键字搜索文档（仅返回审核通过且解析成功的文档）
 * @param keyword 搜索关键字
 * @param pageNum 页码
 * @param pageSize 每页大小
 * @return 分页文档列表
 */
PageVO<DocumentVO> search(String keyword, int pageNum, int pageSize);
```

- [ ] **Step 2: 在 DocumentServiceImpl 实现搜索方法**

在 `DocumentServiceImpl` 类底部（`list(DocumentDTO)` 方法附近）添加:
```java
@Override
public PageVO<DocumentVO> search(String keyword, int pageNum, int pageSize) {
    if (keyword == null || keyword.isEmpty()) {
        return PageVO.<DocumentVO>builder()
                .pageNum(pageNum)
                .pageSize(pageSize)
                .total(0)
                .list(new java.util.ArrayList<>())
                .build();
    }
    // 第一步：从 ES 获取匹配的文档 ID 列表
    java.util.List<String> matchedIds = elasticService.searchIds(keyword);
    if (matchedIds == null || matchedIds.isEmpty()) {
        return PageVO.<DocumentVO>builder()
                .pageNum(pageNum)
                .pageSize(pageSize)
                .total(0)
                .list(new java.util.ArrayList<>())
                .build();
    }
    // 第二步：从 MySQL 查询这些 ID 对应的文档，过滤 reviewing=false AND docState=SUCCESS
    java.util.List<FileDocument> allMatchedDocs = documentRepository.findByIdList(matchedIds);
    java.util.List<FileDocument> filteredDocs = allMatchedDocs.stream()
            .filter(doc -> !doc.isReviewing() && doc.getDocState() == DocStateEnum.SUCCESS)
            .collect(java.util.stream.Collectors.toList());
    // 分页
    int total = filteredDocs.size();
    int start = (pageNum - 1) * pageSize;
    int end = Math.min(start + pageSize, total);
    java.util.List<FileDocument> pagedDocs = (start >= total)
            ? new java.util.ArrayList<>()
            : filteredDocs.subList(start, end);
    // 转换为 VO
    java.util.List<DocumentVO> voList = pagedDocs.stream()
            .map(this::convertToVO)
            .collect(java.util.stream.Collectors.toList());
    return PageVO.<DocumentVO>builder()
            .pageNum(pageNum)
            .pageSize(pageSize)
            .total(total)
            .list(voList)
            .build();
}

private DocumentVO convertToVO(FileDocument doc) {
    DocumentVO vo = new DocumentVO();
    vo.setId(doc.getId());
    vo.setName(doc.getName());
    vo.setSize(doc.getSize());
    vo.setUploadDate(doc.getUploadDate());
    vo.setContentType(doc.getContentType());
    vo.setSuffix(doc.getSuffix());
    vo.setDescription(doc.getDescription());
    vo.setThumbId(doc.getThumbId());
    vo.setUserId(doc.getUserId());
    vo.setUserName(doc.getUserName());
    return vo;
}
```

- [ ] **Step 3: 在 DocumentController 添加搜索接口**

在 `DocumentController` 类底部（`hot()` 方法之后，`addKey()` 方法之前）添加:
```java
@Operation(summary = "2.4 文档全文搜索", description = "根据关键字搜索已审核且解析成功的文档")
@GetMapping(value = "/search")
public ApiResult<Object> search(
        @RequestParam(value = "keyword")
        @Schema(description = "搜索关键字") String keyword,
        @RequestParam(value = "page", defaultValue = "1")
        @Schema(description = "页码") int page,
        @RequestParam(value = "size", defaultValue = "10")
        @Schema(description = "每页大小") int size) {
    if (keyword == null || keyword.trim().isEmpty()) {
        return ApiResult.success(PageVO.<DocumentVO>builder()
                .pageNum(page)
                .pageSize(size)
                .total(0)
                .list(new java.util.ArrayList<>())
                .build());
    }
    // 记录搜索词
    if (StringUtils.hasText(keyword)) {
        SensitiveFilter filter = SensitiveFilter.getInstance();
        int n = filter.checkSensitiveWord(keyword, 0, 1);
        if (n <= 0) {
            redisService.incrementScoreByUserId(keyword, RedisServiceImpl.SEARCH_KEY);
        }
    }
    return ApiResult.success(documentService.search(keyword.trim(), page, size));
}
```

同时在文件顶部添加缺少的 import:
```java
import com.jiaruiblog.domain.entity.vo.DocumentVO;
import java.util.ArrayList;
```

- [ ] **Step 4: 提交代码**

```bash
git add all-docs-application/src/main/java/com/jiaruiblog/application/service/DocumentService.java
git add all-docs-application/src/main/java/com/jiaruiblog/application/service/impl/DocumentServiceImpl.java
git add all-docs-api/src/main/java/com/jiaruiblog/api/controller/DocumentController.java
git commit -m "feat: DocumentController 新增 /search 接口实现全文搜索

- GET /api/v1/document/search?keyword=&page=&size=
- 先查 ES 获取匹配 ID 列表，再查 MySQL 过滤已审核且解析成功的文档
- 复用 DocumentVO 结构，与 list 接口一致"

Co-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>
```

---

## Chunk 4: Executor 文字提取实现（使用 Apache Tika）

### 目标
使用 `FileOperationService.parseToStr()` (Apache Tika) 实现各 Executor 的 `readText()` 方法

### Files
- Modify: `all-docs-application/src/main/java/com/jiaruiblog/application/task/executor/PdfWordTaskExecutor.java`
- Modify: `all-docs-application/src/main/java/com/jiaruiblog/application/task/executor/DocxExecutor.java`
- Modify: `all-docs-application/src/main/java/com/jiaruiblog/application/task/executor/slider/PptExecutor.java`
- Modify: `all-docs-application/src/main/java/com/jiaruiblog/application/task/executor/slider/PptxExecutor.java`

**注意:** `TxtExecutor` 已实现，无需修改。`PicExecutor` 跳过 ES 上传（图片无法提取文字），保持现状。

- [ ] **Step 1: 实现 PdfWordTaskExecutor.readText()**

将 `PdfWordTaskExecutor.java` 整体替换为:
```java
package com.jiaruiblog.application.task.executor;

import com.jiaruiblog.application.service.FileOperationService;
import com.jiaruiblog.application.task.data.TaskData;
import com.jiaruiblog.common.util.SpringApplicationContext;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * PDF 文档解析执行器，使用 Apache Tika 提取文字
 * @author Jarrett Luo
 * @Date 2022/10/24 11:32
 * @Version 1.0
 */
@Slf4j
public class PdfWordTaskExecutor extends TaskExecutor {

    @Override
    protected void readText(InputStream is, String textFilePath) throws IOException {
        if (is == null) {
            throw new IOException("输入流为空");
        }
        FileOperationService fileOperationService = SpringApplicationContext.getBean(FileOperationService.class);
        // 使用 Tika 解析 PDF 内容
        com.jiaruiblog.application.service.TextExtractResult result =
                fileOperationService.parseToStr(is);
        String content = result.isSuccess() ? result.getContent() : "";
        // 写入文本文件
        File file = new File(textFilePath);
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), java.nio.charset.StandardCharsets.UTF_8))) {
            writer.write(content != null ? content : "");
        }
        log.info("PDF 文字提取完成，输出路径: {}", textFilePath);
    }

    @Override
    protected void makeThumb(InputStream is, String picPath) throws IOException {
        // PDF 缩略图可使用 Tika 提取首页，或使用 PDF Renderer
        // 暂时跳过，留作后续实现
        log.debug("PDF 缩略图生成暂未实现");
    }

    @Override
    protected void makePreviewFile(InputStream is, TaskData taskData) {
        // 预览文件暂未实现
    }
}
```

- [ ] **Step 2: 实现 DocxExecutor.readText()**

将 `DocxExecutor.java` 整体替换为:
```java
package com.jiaruiblog.application.task.executor;

import com.jiaruiblog.application.service.FileOperationService;
import com.jiaruiblog.application.task.data.TaskData;
import com.jiaruiblog.common.util.SpringApplicationContext;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * DOCX/DOC/XLS/XLSX 文档解析执行器，使用 Apache Tika 提取文字
 * @author Jarrett Luo
 * @Date 2022/10/24 11:42
 * @Version 1.0
 */
@Slf4j
public class DocxExecutor extends TaskExecutor {

    @Override
    protected void readText(InputStream is, String textFilePath) throws IOException {
        if (is == null) {
            throw new IOException("输入流为空");
        }
        FileOperationService fileOperationService = SpringApplicationContext.getBean(FileOperationService.class);
        com.jiaruiblog.application.service.TextExtractResult result =
                fileOperationService.parseToStr(is);
        String content = result.isSuccess() ? result.getContent() : "";
        File file = new File(textFilePath);
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), java.nio.charset.StandardCharsets.UTF_8))) {
            writer.write(content != null ? content : "");
        }
        log.info("Office 文档文字提取完成，输出路径: {}", textFilePath);
    }

    @Override
    protected void makeThumb(InputStream is, String picPath) throws IOException {
        // 暂时跳过，留作后续实现
    }

    @Override
    protected void makePreviewFile(InputStream is, TaskData taskData) {
        // 预览文件暂未实现
    }
}
```

- [ ] **Step 3: 实现 PptExecutor.readText()**

将 `PptExecutor.java` 整体替换为:
```java
package com.jiaruiblog.application.task.executor.slider;

import com.jiaruiblog.application.service.FileOperationService;
import com.jiaruiblog.application.task.data.TaskData;
import com.jiaruiblog.application.task.executor.TaskExecutor;
import com.jiaruiblog.common.util.SpringApplicationContext;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * PPT 文档解析执行器，使用 Apache Tika 提取文字
 * @author Jarrett Luo
 * @Date 2022/10/24 11:32
 * @Version 1.0
 */
@Slf4j
public class PptExecutor extends TaskExecutor {

    @Override
    protected void readText(InputStream is, String textFilePath) throws IOException {
        if (is == null) {
            throw new IOException("输入流为空");
        }
        FileOperationService fileOperationService = SpringApplicationContext.getBean(FileOperationService.class);
        com.jiaruiblog.application.service.TextExtractResult result =
                fileOperationService.parseToStr(is);
        String content = result.isSuccess() ? result.getContent() : "";
        File file = new File(textFilePath);
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), java.nio.charset.StandardCharsets.UTF_8))) {
            writer.write(content != null ? content : "");
        }
        log.info("PPT 文字提取完成，输出路径: {}", textFilePath);
    }

    @Override
    protected void makeThumb(InputStream is, String picPath) throws IOException {
        // PPT 缩略图暂未实现
        log.debug("PPT 缩略图生成暂未实现");
    }

    @Override
    protected void makePreviewFile(InputStream inStream, TaskData taskData) {
        // 预览文件暂未实现
    }
}
```

- [ ] **Step 4: 实现 PptxExecutor.readText() 和清理 uploadFileToEs override**

`PptxExecutor` 继承自 `DocxExecutor`，`DocxExecutor.readText()` 已使用 Tika 实现，所以 `PptxExecutor` 无需重写 `readText()`。

需要修复 `PptxExecutor.uploadFileToEs()` — 当前 override 为空（TODO），会导致 PPTX 文件无法写入 ES。由于 `PptxExecutor` 继承 `DocxExecutor`，基类的 `uploadFileToEs()` 已完整实现，所以删除这个 override 即可。

修改 `PptxExecutor.java`:
```java
@Override
protected void makeThumb(InputStream is, String picPath) {
    // PPTX 缩略图暂未实现
    log.debug("PPTX 缩略图生成暂未实现");
}

@Override
protected void makePreviewFile(InputStream inStream, TaskData taskData) {
    // 预览文件暂未实现
}

// 删除 uploadFileToEs override，因为基类 TaskExecutor 的实现已经完整
// PptxExecutor 继承 DocxExecutor，DocxExecutor 继承 TaskExecutor
// 基类的 uploadFileToEs() 调用 readText() + upload() 即可处理 PPTX
```

- [ ] **Step 5: 提交代码**

```bash
git add all-docs-application/src/main/java/com/jiaruiblog/application/task/executor/PdfWordTaskExecutor.java
git add all-docs-application/src/main/java/com/jiaruiblog/application/task/executor/DocxExecutor.java
git add all-docs-application/src/main/java/com/jiaruiblog/application/task/executor/slider/PptExecutor.java
git add all-docs-application/src/main/java/com/jiaruiblog/application/task/executor/slider/PptxExecutor.java
git commit -m "feat: 实现各 Executor 的文字提取（使用 Apache Tika）

- PdfWordTaskExecutor: Tika 解析 PDF
- DocxExecutor: Tika 解析 DOCX/DOC/XLS/XLSX
- PptExecutor: Tika 解析 PPT
- PptxExecutor: 继承 DocxExecutor，删除无效的 uploadFileToEs override"

Co-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>
```

---

## Chunk 5: 验证与测试

### 目标
验证各步骤实现的正确性

- [ ] **Step 1: 编译验证**

在项目根目录执行:
```bash
cd C:\Project\java\all-docs
mvn compile -q 2>&1 | head -50
```

预期: 无编译错误（忽略外部依赖下载）

- [ ] **Step 2: 验证 DocReviewServiceImpl 的 approve 方法**

确认 approve 方法包含:
- `doc.setReviewing(false)`
- `doc.setDocState(DocStateEnum.WAIT)`
- `taskExecuteService.execute(doc)`

确认 refuse 方法包含:
- `fileDocument.setReviewing(false)`
- `fileDocument.setDocState(DocStateEnum.FAIL)`

- [ ] **Step 3: 验证 DocumentController.search 接口**

确认 `/api/v1/document/search` 接口存在，调用链:
`DocumentController.search()` → `DocumentService.search()` → `ElasticService.searchIds()` → `MySQL filter(reviewing=false AND docState=SUCCESS)` → `PageVO<DocumentVO>`

- [ ] **Step 4: 验证 Executor readText 实现**

确认 PdfWordTaskExecutor、DocxExecutor、PptExecutor 的 `readText()` 方法使用 `FileOperationService.parseToStr()` (Apache Tika)。

- [ ] **Step 5: 提交全部修改**

如果以上验证通过，执行最终提交:
```bash
git add -A
git commit -m "feat: 完成 ES 文档审核-解析-检索全流程串联

核心变更:
1. DocReviewServiceImpl.approve() 触发异步解析任务
2. DocumentController 新增 /search 全文搜索接口
3. 各 Executor 使用 Apache Tika 实现文字提取"

Co-Authored-By: Claude Opus 4.6 <noreply@anthropic.com>
```
