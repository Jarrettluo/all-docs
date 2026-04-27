# queryAllComments 重构设计

## 问题

1. `CommentWithUserVO` 在 Service 层构建，但 Controller 层也需要使用
2. `CommentWithUserVO.docName` 为空，Comment 和 FileDocument 需要关联查询
3. 使用了 `BeanUtils.copyProperties` 不够清晰

## 解决方案

### 1. Service 层 - 返回 `PageVO<Comment>`

修改 `ICommentService.queryAllComments` 返回类型为 `PageVO<Comment>`，Service 层不再构建 VO

### 2. Controller 层 - 负责 VO 组装

`CommentController` 注入 `DocumentRepository`，根据返回的 `docId` 批量查询文档名称，组装 `CommentWithUserVO`

### 3. 新增 `CommentConverter` - 手写转换替代 BeanUtils

```java
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

## 改动文件

| 文件 | 改动 |
|------|------|
| `ICommentService.java` | `queryAllComments` 返回 `PageVO<Comment>` |
| `CommentServiceImpl.java` | 移除 VO 构建逻辑，返回 `PageVO<Comment>` |
| `CommentConverter.java` | 新增，手写字段映射 |
| `CommentController.java` | 注入 `DocumentRepository`，调用 `CommentConverter` 组装 VO |

## 数据流

```
Controller -> Service (PageVO<Comment>) -> Controller
Controller -> DocumentRepository (batch query docName)
Controller -> CommentConverter (to CommentWithUserVO)
```
