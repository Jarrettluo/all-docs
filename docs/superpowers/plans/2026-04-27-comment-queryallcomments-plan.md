# queryAllComments 重构实现计划

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 重构 `queryAllComments` 方法，Service 层返回 `PageVO<Comment>`，Controller 层负责组装 `CommentWithUserVO`

**Architecture:** Service 层不再构建 VO，Controller 层负责调用 DocumentRepository 批量查询文档名称并使用 CommentConverter 组装 VO

**Tech Stack:** Spring, CommentRepository, DocumentRepository

---

## Chunk 1: Service 层接口修改

**Files:**
- Modify: `all-docs-application/src/main/java/com/jiaruiblog/application/service/ICommentService.java`
- Modify: `all-docs-application/src/main/java/com/jiaruiblog/application/service/impl/CommentServiceImpl.java`

- [ ] **Step 1: 修改 ICommentService 接口返回类型**

修改 `ICommentService.java` 第 128 行：
```java
// 修改前
PageVO<CommentWithUserVO> queryAllComments(BasePageDTO page, String userId, Boolean isAdmin);

// 修改后
PageVO<Comment> queryAllComments(BasePageDTO page, String userId, Boolean isAdmin);
```

- [ ] **Step 2: 修改 CommentServiceImpl 实现**

修改 `CommentServiceImpl.java` 第 128-162 行：
```java
@Override
public PageVO<Comment> queryAllComments(BasePageDTO page, String userId, Boolean isAdmin) {
    log.info("查询的参数是：{}, {}", page, userId);
    List<Comment> comments;
    if (Boolean.TRUE.equals(isAdmin)) {
        comments = commentRepository.findAll();
    } else {
        comments = commentRepository.findByUserId(userId);
    }

    long count = comments.size();

    int pageNum = page.getPage();
    int pageSize = page.getRows();
    int skip = (pageNum - 1) * pageSize;
    comments = comments.stream()
            .skip(skip)
            .limit(pageSize)
            .toList();

    return PageVO.<Comment>builder()
            .total((int) count)
            .list(comments)
            .pageNum(pageNum)
            .pageSize(pageSize)
            .build();
}
```

- [ ] **Step 3: 提交**

```bash
git add all-docs-application/src/main/java/com/jiaruiblog/application/service/ICommentService.java
git add all-docs-application/src/main/java/com/jiaruiblog/application/service/impl/CommentServiceImpl.java
git commit -m "refactor: queryAllComments返回PageVO<Comment>"
```

---

## Chunk 2: 新增 CommentConverter

**Files:**
- Create: `all-docs-application/src/main/java/com/jiaruiblog/application/service/converter/CommentConverter.java`

- [ ] **Step 1: 创建 CommentConverter**

创建文件 `all-docs-application/src/main/java/com/jiaruiblog/application/service/converter/CommentConverter.java`：

```java
package com.jiaruiblog.application.service.converter;

import com.jiaruiblog.domain.entity.po.Comment;
import com.jiaruiblog.domain.entity.vo.CommentWithUserVO;
import org.springframework.stereotype.Component;

@Component
public class CommentConverter {

    public CommentWithUserVO toVO(Comment comment, String docName) {
        CommentWithUserVO vo = new CommentWithUserVO();
        vo.setId(comment.getId());
        vo.setUserId(comment.getUserId());
        vo.setUserName(comment.getUserName());
        vo.setContent(comment.getContent());
        vo.setDocId(comment.getDocId());
        vo.setDocName(docName);
        vo.setCreateDate(comment.getCreateDate());
        vo.setUpdateDate(comment.getUpdateDate());
        return vo;
    }
}
```

- [ ] **Step 2: 提交**

```bash
git add all-docs-application/src/main/java/com/jiaruiblog/application/service/converter/CommentConverter.java
git commit -m "feat: add CommentConverter手写字段映射"
```

---

## Chunk 3: Controller 层 VO 组装

**Files:**
- Modify: `all-docs-api/src/main/java/com/jiaruiblog/api/controller/CommentController.java`

- [ ] **Step 1: 修改 CommentController 注入依赖并组装 VO**

修改 `CommentController.java`：

1. 新增注入：
```java
@Resource
CommentConverter commentConverter;

@Resource
DocumentRepository documentRepository;
```

2. 修改 `queryMyComments` 方法：
```java
@Operation(summary = "查询用户评论", description = "查询当前用户的评论列表")
@PostMapping(value = "/auth/myComments")
public ApiResult<PageVO<CommentWithUserVO>> queryMyComments(@RequestBody BasePageDTO pageDTO, HttpServletRequest request) {
    String userId = (String) request.getAttribute("id");
    PageVO<Comment> commentPage = commentService.queryAllComments(pageDTO, userId, false);
    PageVO<CommentWithUserVO> result = buildCommentWithUserVO(commentPage);
    return ApiResult.success(result);
}
```

3. 修改 `queryAllComments` 方法：
```java
@Operation(summary = "查询所有评论", description = "管理员查询所有用户的评论列表")
@Permission(PermissionEnum.ADMIN)
@PostMapping(value = "/auth/allComments")
public ApiResult<PageVO<CommentWithUserVO>> queryAllComments(@RequestBody BasePageDTO pageDTO) {
    PageVO<Comment> commentPage = commentService.queryAllComments(pageDTO, null, true);
    PageVO<CommentWithUserVO> result = buildCommentWithUserVO(commentPage);
    return ApiResult.success(result);
}
```

4. 新增私有方法：
```java
private PageVO<CommentWithUserVO> buildCommentWithUserVO(PageVO<Comment> commentPage) {
    if (commentPage == null || commentPage.getList() == null || commentPage.getList().isEmpty()) {
        return PageVO.<CommentWithUserVO>builder()
                .total(0)
                .list(new java.util.ArrayList<>())
                .pageNum(commentPage != null ? commentPage.getPageNum() : 1)
                .pageSize(commentPage != null ? commentPage.getPageSize() : 10)
                .build();
    }

    // 收集所有docId
    List<String> docIdList = commentPage.getList().stream()
            .map(Comment::getDocId)
            .collect(Collectors.toList());

    // 批量查询文档
    List<FileDocument> documents = documentRepository.findByIdList(docIdList);
    Map<String, String> docNameMap = documents.stream()
            .collect(Collectors.toMap(FileDocument::getId, FileDocument::getName));

    // 组装VO
    List<CommentWithUserVO> voList = commentPage.getList().stream()
            .map(comment -> commentConverter.toVO(comment, docNameMap.get(comment.getDocId())))
            .collect(Collectors.toList());

    return PageVO.<CommentWithUserVO>builder()
            .total(commentPage.getTotal())
            .list(voList)
            .pageNum(commentPage.getPageNum())
            .pageSize(commentPage.getPageSize())
            .build();
}
```

5. 添加必要的 import：
```java
import com.jiaruiblog.application.service.converter.CommentConverter;
import com.jiaruiblog.domain.entity.po.Comment;
import com.jiaruiblog.domain.entity.po.FileDocument;
import com.jiaruiblog.infrastructure.repository.DocumentRepository;
import java.util.Map;
import java.util.stream.Collectors;
```

- [ ] **Step 2: 提交**

```bash
git add all-docs-api/src/main/java/com/jiaruiblog/api/controller/CommentController.java
git commit -m "refactor: Controller层组装CommentWithUserVO"
```

---

## 验收标准

1. `ICommentService.queryAllComments` 返回 `PageVO<Comment>`
2. `CommentConverter.toVO` 手写字段映射，无 BeanUtils
3. Controller 层调用 `DocumentRepository.findByIdList` 批量查询文档名称
4. `CommentWithUserVO.docName` 有值
