# MinIO 存储结构重构实现计划

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 重构MinIO存储结构，实现用户上传文件到`documents/`、预览图到`thumbs/`、头像到`avatars/[username]/`，所有文件元数据存储在MySQL，通过MySQL中的唯一key从MinIO进行下载/预览。

**Architecture:**
- 存储层(`MinioStorageStrategy`): 支持带前缀路径的上传/下载/删除
- 服务层: `DocumentService`处理文档、`ThumbnailService`处理缩略图、`UserService`处理头像
- 控制器层: `FileController`处理文件预览下载、`UserController`处理头像
- 关键原则: MinIO存储key = MySQL中存储的gridfsId/thumbId等，路径格式统一

**Tech Stack:** Java 17, Spring Boot 3.2.0, MinIO 8.5.7, MyBatis, MySQL 8.0

---

## Chunk 1: 存储策略层重构

### 1.1 更新 StorageStrategy 接口

**Files:**
- Modify: `all-docs-infrastructure/src/main/java/com/jiaruiblog/infrastructure/storage/StorageStrategy.java`

```java
package com.jiaruiblog.infrastructure.storage;

/**
 * 存储策略接口 - 支持带前缀路径的对象存储
 */
public interface StorageStrategy {
    /**
     * 上传文件
     * @param inputStream 文件输入流
     * @param objectKey 对象key (包含路径前缀，如 "documents/xxx" 或 "thumbs/xxx")
     * @param contentType 内容类型
     * @return 上传后的objectKey，失败返回null
     */
    String upload(java.io.InputStream inputStream, String objectKey, String contentType);

    /**
     * 下载文件
     * @param objectKey 对象key (包含路径前缀)
     * @return InputStream，调用者负责关闭
     */
    java.io.InputStream download(String objectKey);

    /**
     * 删除文件
     * @param objectKey 对象key (包含路径前缀)
     * @return 是否删除成功
     */
    boolean delete(String objectKey);

    /**
     * 获取文件预签名URL
     * @param objectKey 对象key (包含路径前缀)
     * @param expiry 过期时间(秒)
     * @return 预签名URL
     */
    String getPresignedUrl(String objectKey, int expiry);

    /**
     * 获取文件预签名URL (默认1小时)
     */
    default String getPresignedUrl(String objectKey) {
        return getPresignedUrl(objectKey, 3600);
    }
}
```

- [ ] **Step 1: Read current StorageStrategy interface**

```bash
# 确认文件路径和内容
cat all-docs-infrastructure/src/main/java/com/jiaruiblog/infrastructure/storage/StorageStrategy.java
```

- [ ] **Step 2: Update StorageStrategy interface with new method signatures**

### 1.2 更新 MinioStorageStrategy 实现

**Files:**
- Modify: `all-docs-infrastructure/src/main/java/com/jiaruiblog/infrastructure/storage/MinioStorageStrategy.java`

```java
package com.jiaruiblog.infrastructure.storage;

import io.minio.*;
import io.minio.http.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * MinIO存储策略实现
 * 支持带前缀路径的存储: documents/, thumbs/, avatars/[username]/
 */
@Component
public class MinioStorageStrategy implements StorageStrategy {

    private static final Logger log = LoggerFactory.getLogger(MinioStorageStrategy.class);

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket-name:alldocs}")
    private String bucketName;

    /**
     * 确保bucket存在
     */
    private void ensureBucketExists() throws Exception {
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            log.info("Created MinIO bucket: {}", bucketName);
        }
    }

    @Override
    public String upload(InputStream inputStream, String objectKey, String contentType) {
        if (objectKey == null || objectKey.isEmpty()) {
            log.error("Upload failed: objectKey is empty");
            return null;
        }
        try (InputStream is = inputStream) {
            ensureBucketExists();

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectKey)
                    .stream(is, -1, 10485760) // 10MB buffer
                    .contentType(contentType)
                    .build());

            log.info("Uploaded to MinIO: bucket={}, objectKey={}", bucketName, objectKey);
            return objectKey;
        } catch (Exception e) {
            log.error("MinIO upload failed: objectKey={}", objectKey, e);
            return null;
        }
    }

    @Override
    public InputStream download(String objectKey) {
        if (objectKey == null || objectKey.isEmpty()) {
            log.error("Download failed: objectKey is empty");
            return null;
        }
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectKey)
                    .build());
        } catch (Exception e) {
            log.error("MinIO download failed: objectKey={}", objectKey, e);
            return null;
        }
    }

    @Override
    public boolean delete(String objectKey) {
        if (objectKey == null || objectKey.isEmpty()) {
            log.error("Delete failed: objectKey is empty");
            return false;
        }
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectKey)
                    .build());
            log.info("Deleted from MinIO: bucket={}, objectKey={}", bucketName, objectKey);
            return true;
        } catch (Exception e) {
            log.error("MinIO delete failed: objectKey={}", objectKey, e);
            return false;
        }
    }

    @Override
    public String getPresignedUrl(String objectKey, int expiry) {
        if (objectKey == null || objectKey.isEmpty()) {
            log.error("getPresignedUrl failed: objectKey is empty");
            return null;
        }
        try {
            Map<String, String> extraQueryParams = new HashMap<>();
            extraQueryParams.put("expires", String.valueOf(expiry));
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucketName)
                    .object(objectKey)
                    .expiry(expiry)
                    .extraQueryParams(extraQueryParams)
                    .build());
        } catch (Exception e) {
            log.error("MinIO getPresignedUrl failed: objectKey={}", objectKey, e);
            return null;
        }
    }
}
```

- [ ] **Step 1: Read current MinioStorageStrategy implementation**
- [ ] **Step 2: Update MinioStorageStrategy with new implementation**
- [ ] **Step 3: Run build to verify compilation**

```bash
cd C:/Project/java/all-docs && mvn compile -pl all-docs-infrastructure -am -q
```

---

## Chunk 2: 存储路径常量定义

### 2.1 创建存储路径常量类

**Files:**
- Create: `all-docs-common/src/main/java/com/jiaruiblog/common/constants/StorageConstants.java`

```java
package com.jiaruiblog.common.constants;

/**
 * MinIO存储路径常量
 * 统一管理存储路径前缀
 */
public final class StorageConstants {

    private StorageConstants() {}

    /**
     * 文档存储路径前缀
     * 完整路径格式: documents/{uniqueKey}
     */
    public static final String DOCUMENTS = "documents/";

    /**
     * 缩略图存储路径前缀
     * 完整路径格式: thumbs/{uniqueKey}
     */
    public static final String THUMBS = "thumbs/";

    /**
     * 头像存储路径前缀
     * 完整路径格式: avatars/{username}/{filename}
     */
    public static final String AVATARS = "avatars/";

    /**
     * 文本文件存储路径前缀
     * 完整路径格式: texts/{uniqueKey}
     */
    public static final String TEXTS = "texts/";

    /**
     * 预览文件存储路径前缀
     * 完整路径格式: previews/{uniqueKey}
     */
    public static final String PREVIEWS = "previews/";

    /**
     * 生成文档存储路径
     */
    public static String documentPath(String uniqueKey) {
        return DOCUMENTS + uniqueKey;
    }

    /**
     * 生成缩略图存储路径
     */
    public static String thumbPath(String uniqueKey) {
        return THUMBS + uniqueKey;
    }

    /**
     * 生成头像存储路径
     */
    public static String avatarPath(String username, String filename) {
        return AVATARS + username + "/" + filename;
    }

    /**
     * 生成文本文件存储路径
     */
    public static String textPath(String uniqueKey) {
        return TEXTS + uniqueKey;
    }

    /**
     * 生成预览文件存储路径
     */
    public static String previewPath(String uniqueKey) {
        return PREVIEWS + uniqueKey;
    }
}
```

- [ ] **Step 1: Create StorageConstants.java**
- [ ] **Step 2: Verify compilation**

---

## Chunk 3: DocumentService 文档存储重构

### 3.1 更新 DocumentServiceImpl

**Files:**
- Modify: `all-docs-application/src/main/java/com/jiaruiblog/application/service/impl/DocumentServiceImpl.java`

关键变更:
1. `uploadFileToGridFs` 方法 - 使用 `documents/` 前缀 + UUID作为objectKey
2. `saveFile` 方法 - 完善实现，上传文件到MinIO并保存元数据
3. `documentUpload` 方法 - 实现完整的文档上传逻辑
4. `getFileBytes` 方法 - 从MinIO下载文件而非从content字段
5. `getFileThumb` 方法 - 使用 `thumbs/` 前缀
6. `removeFile` 方法 - 同时删除MinIO中的文件

```java
@Override
public String uploadFileToGridFs(String fileName, InputStream inputStream, String contentType, String md5) {
    if (inputStream == null) {
        throw new IllegalArgumentException("InputStream cannot be null");
    }
    // 使用UUID作为唯一key，路径前缀为 documents/
    String uniqueKey = IdUtil.simpleUUID();
    String objectKey = StorageConstants.documentPath(uniqueKey);
    
    StorageStrategy storageStrategy = storageFactory.getStorageStrategy();
    String result = storageStrategy.upload(inputStream, objectKey, contentType);
    
    if (result != null) {
        log.info("Uploaded document to MinIO: objectKey={}, filename={}", objectKey, fileName);
        return uniqueKey; // 返回唯一key，用于存储到MySQL的gridfsId字段
    }
    throw new RuntimeException("Failed to upload file to MinIO");
}
```

- [ ] **Step 1: Read current DocumentServiceImpl**
- [ ] **Step 2: Update DocumentServiceImpl with new storage paths**
- [ ] **Step 3: Update imports to include StorageConstants**
- [ ] **Step 4: Verify build**

```bash
mvn compile -pl all-docs-application -am -q
```

### 3.2 关键方法变更说明

| 方法 | 旧实现 | 新实现 |
|------|--------|--------|
| `uploadFileToGridFs` | 上传到根路径，返回UUID | 上传到`documents/`，返回UUID作为key |
| `saveFile(FileDocument, InputStream)` | 设置`gridfsId`为UUID | 设置`gridfsId`为UUID，路径前缀`documents/` |
| `documentUpload` | 空实现 | 完整实现：上传→生成缩略图→保存元数据 |
| `getFileBytes` | 从`content`字段读取 | 从MinIO下载`documents/{gridfsId}` |
| `getFileThumb` | 下载任意key | 下载`thumbs/{thumbId}` |
| `removeFile` | 只删MySQL记录 | 同时删除MinIO文件 |

---

## Chunk 4: ThumbnailService 缩略图存储重构

### 4.1 更新 ThumbnailServiceImpl

**Files:**
- Modify: `all-docs-application/src/main/java/com/jiaruiblog/application/service/impl/ThumbnailServiceImpl.java`

关键变更:
1. `makeThumb` 方法 - 上传到 `thumbs/` 前缀
2. `makePreview` 方法 - 上传到 `previews/` 前缀
3. 返回值保持UUID，调用方需保存到MySQL

```java
@Override
public String makeThumb(InputStream inputStream, String fileName, int width, int height) {
    if (inputStream == null || fileName == null || fileName.isEmpty()) {
        log.warn("生成缩略图失败：输入参数为空");
        return "";
    }

    try {
        BufferedImage thumbImage = Thumbnails.of(ImageIO.read(inputStream))
                .size(width, height)
                .outputFormat("jpg")
                .asBufferedImage();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(thumbImage, "jpg", baos);
        byte[] thumbBytes = baos.toByteArray();

        String thumbId = UUID.randomUUID().toString();
        String objectKey = StorageConstants.thumbPath(thumbId);
        
        // 直接上传缩略图到thumbs/路径
        String result = minioStorageStrategy.upload(new ByteArrayInputStream(thumbBytes), objectKey, "image/jpeg");

        if (result != null) {
            log.info("缩略图生成成功：fileName={}, thumbId={}, objectKey={}", fileName, thumbId, objectKey);
            return thumbId; // 返回thumbId，调用方保存到MySQL
        }
        return "";
    } catch (Exception e) {
        log.error("生成缩略图异常：fileName={}", fileName, e);
        return "";
    }
}

@Override
public String makePreview(InputStream inputStream, String fileName) {
    if (inputStream == null || fileName == null || fileName.isEmpty()) {
        log.warn("生成预览图失败：输入参数为空");
        return "";
    }

    try {
        BufferedImage previewImage = Thumbnails.of(ImageIO.read(inputStream))
                .size(800, 800)
                .outputFormat("jpg")
                .asBufferedImage();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(previewImage, "jpg", baos);
        byte[] previewBytes = baos.toByteArray();

        String previewId = UUID.randomUUID().toString();
        String objectKey = StorageConstants.previewPath(previewId);
        
        String result = minioStorageStrategy.upload(new ByteArrayInputStream(previewBytes), objectKey, "image/jpeg");

        if (result != null) {
            log.info("预览图生成成功：fileName={}, previewId={}, objectKey={}", fileName, previewId, objectKey);
            return previewId;
        }
        return "";
    } catch (Exception e) {
        log.error("生成预览图异常：fileName={}", fileName, e);
        return "";
    }
}
```

- [ ] **Step 1: Read current ThumbnailServiceImpl**
- [ ] **Step 2: Update with new storage paths**
- [ ] **Step 3: Verify build**

---

## Chunk 5: UserService 头像存储重构

### 5.1 更新 UserServiceImpl

**Files:**
- Modify: `all-docs-application/src/main/java/com/jiaruiblog/application/service/impl/UserServiceImpl.java`

关键变更:
1. `uploadUserAvatar` - 上传到 `avatars/{username}/` 前缀
2. `removeUserAvatar` - 从 `avatars/{username}/` 路径删除

```java
@Override
public void uploadUserAvatar(String userId, MultipartFile file) {
    if (!StringUtils.hasText(userId) || file == null || file.isEmpty()) {
        throw new BusinessException(ErrorCode.INVALID_PARAM, "用户ID或文件不能为空");
    }

    User user = userRepository.findById(userId);
    if (user == null) {
        throw new BusinessException(ErrorCode.USER_NOT_FOUND);
    }

    try {
        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            originalFilename = "avatar.jpg";
        }
        
        // 生成唯一文件名
        String uuid = UUID.randomUUID().toString();
        String extension = getFileExtension(originalFilename);
        String newFilename = uuid + extension;
        
        // 构建存储路径: avatars/{username}/{filename}
        String objectKey = StorageConstants.avatarPath(user.getUsername(), newFilename);
        String contentType = file.getContentType() != null ? file.getContentType() : "image/jpeg";

        String result = minioStorageStrategy.upload(file.getInputStream(), objectKey, contentType);
        if (result == null) {
            throw new BusinessException(ErrorCode.OPERATE_FAILED, "头像上传失败");
        }
        
        // 保存avatar objectKey到用户记录（用于后续获取URL）
        user.setAvatar(objectKey);
        user.setUpdateDate(new Date());
        userRepository.update(user);

        log.info("用户头像上传成功：userId={}, username={}, objectKey={}", userId, user.getUsername(), objectKey);
    } catch (BusinessException e) {
        throw e;
    } catch (Exception e) {
        log.error("上传用户头像失败：userId={}", userId, e);
        throw new BusinessException(ErrorCode.OPERATE_FAILED, "头像上传失败");
    }
}

@Override
public void removeUserAvatar(String userId) {
    if (!StringUtils.hasText(userId)) {
        return;
    }
    User user = userRepository.findById(userId);
    if (user == null) {
        return;
    }
    // avatar字段现在存储的是完整objectKey
    if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
        try {
            minioStorageStrategy.delete(user.getAvatar());
        } catch (Exception e) {
            log.error("删除用户头像文件失败：objectKey={}", user.getAvatar(), e);
        }
        user.setAvatar(null);
        user.setUpdateDate(new Date());
        userRepository.update(user);
    }
    log.info("用户头像删除成功：userId={}", userId);
}

/**
 * 获取文件扩展名
 */
private String getFileExtension(String filename) {
    if (filename == null || filename.isEmpty()) {
        return ".jpg";
    }
    int dotIndex = filename.lastIndexOf('.');
    if (dotIndex > 0) {
        return filename.substring(dotIndex);
    }
    return ".jpg";
}
```

- [ ] **Step 1: Read current UserServiceImpl**
- [ ] **Step 2: Update uploadUserAvatar and removeUserAvatar methods**
- [ ] **Step 3: Add getFileExtension helper method**
- [ ] **Step 4: Verify build**

---

## Chunk 6: FileController 文件预览下载重构

### 6.1 分析当前实现

当前 `FileController` 的问题:
1. `serveFileOnline` (line 99-120) - 从 `file.getContent()` 读取，应该从MinIO下载
2. `downloadFile` (line 170-194) - 同上
3. `previewFileOnline` (line 202-216) - 同上
4. `previewThumb` (line 425-443) - 调用 `fileService.getFileThumb(thumbId)` 但Service层未使用thumbs/前缀
5. `previewThumb1` (line 461-481) - 同上

### 6.2 需要的变更

**Files:**
- Modify: `all-docs-api/src/main/java/com/jiaruiblog/api/controller/FileController.java`

| 方法 | 变更 |
|------|------|
| `serveFileOnline` | 从 `documents/{fileDocument.getGridfsId()}` 下载 |
| `downloadFile` | 从 `documents/{fileDocument.getGridfsId()}` 下载 |
| `previewFileOnline` | 从 `previews/{fileDocument.getPreviewFileId()}` 下载 |
| `previewThumb` | 从 `thumbs/{thumbId}` 下载 |

```java
@GetMapping("/view/{id}")
public ResponseEntity<Object> serveFileOnline(@PathVariable String id,
                                               HttpServletResponse response) throws UnsupportedEncodingException {
    Optional<FileDocument> fileOpt = fileService.getById(id);
    if (fileOpt.isEmpty()) {
        throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
    }

    FileDocument fileDocument = fileOpt.get();
    
    // 记录日志
    User user = new User(); // 需要从request获取
    docLogService.addLog(user, fileDocument, DocLogServiceImpl.Action.PREVIEW);

    // 从MinIO下载文件内容
    String objectKey = StorageConstants.documentPath(fileDocument.getGridfsId());
    InputStream inputStream = fileService.getFileStream(objectKey);
    if (inputStream == null) {
        throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
    }

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "fileName=" + URLEncoder.encode(fileDocument.getName(), "utf-8"))
            .header(HttpHeaders.CONTENT_TYPE, fileDocument.getContentType())
            .header(HttpHeaders.CONTENT_LENGTH, fileDocument.getSize() + "")
            .body(inputStream); // 直接返回InputStream让Spring处理
}
```

- [ ] **Step 1: Read FileController completely**
- [ ] **Step 2: Identify all methods that need updating**
- [ ] **Step 3: Add getFileStream method to DocumentService if needed**
- [ ] **Step 4: Update FileController methods**

### 6.3 DocumentService 需要新增的方法

```java
/**
 * 获取文件流 - 用于Controller直接返回
 * @param objectKey MinIO中的对象key (包含路径前缀)
 */
InputStream getFileStream(String objectKey);
```

- [ ] **Add getFileStream method to DocumentService interface**
- [ ] **Implement in DocumentServiceImpl**
- [ ] **Verify build**

---

## Chunk 7: FileOperationService 工具服务重构

### 7.1 分析当前实现

当前 `FileOperationServiceImpl` 问题:
1. `uploadFile` - 上传到根路径，应该根据类型上传到 `documents/` 或其他前缀
2. `getFileUrl` - 返回presigned URL

### 7.2 需要的变更

**Files:**
- Modify: `all-docs-application/src/main/java/com/jiaruiblog/application/service/impl/FileOperationServiceImpl.java`

建议: 让调用方指定存储路径前缀，或者根据文件类型自动选择

```java
@Override
public String uploadFile(String name, InputStream inputStream, String folderPrefix) {
    if (name == null || name.isEmpty() || inputStream == null) {
        log.warn("上传文件失败：参数为空");
        return "";
    }
    try {
        String uniqueId = IdUtil.simpleUUID();
        String objectKey = folderPrefix + uniqueId;
        String contentType = tika.detect(inputStream);
        inputStream.reset();
        String result = minioStorageStrategy.upload(inputStream, objectKey, contentType);
        log.info("上传文件成功：originalName={}, objectKey={}", name, objectKey);
        return result != null ? uniqueId : ""; // 返回uniqueId，objectKey通过folderPrefix+uniqueId计算
    } catch (Exception e) {
        log.error("上传文件失败：name={}", name, e);
        return "";
    }
}
```

- [ ] **Step 1: Read FileOperationServiceImpl**
- [ ] **Step 2: Update uploadFile to support folder prefix**
- [ ] **Step 3: Verify build**

---

## Chunk 8: 数据库实体确认

### 8.1 FileDocument 实体

**Files:**
- Review: `all-docs-domain/src/main/java/com/jiaruiblog/domain/entity/po/FileDocument.java`

现有字段已足够:
- `id` - MySQL主键
- `gridfsId` - MinIO存储key (UUID，不含路径前缀)
- `thumbId` - 缩略图MinIO key
- `textFileId` - 文本文件MinIO key
- `previewFileId` - 预览图MinIO key

**使用方式:**
- `gridfsId` = UUID, MinIO路径 = `documents/{gridfsId}`
- `thumbId` = UUID, MinIO路径 = `thumbs/{thumbId}`
- 依此类推

### 8.2 User 实体

**Files:**
- Review: `all-docs-domain/src/main/java/com/jiaruiblog/domain/entity/po/User.java`

现有字段:
- `avatar` - 需要改为存储完整的objectKey (如 `avatars/admin/avatar.jpg`)

- [ ] **确认avatar字段用途 - 是存储objectKey还是URL**
- [ ] **如果存储URL，保持不变；如果存储objectKey，UserService已更新**

---

## Chunk 9: 头像获取API

### 9.1 头像获取端点

**Files:**
- Modify: `all-docs-api/src/main/java/com/jiaruiblog/api/controller/UserController.java`

需要添加获取头像的endpoint:

```java
/**
 * 获取用户头像
 */
@GetMapping(value = "/avatar/{username}", produces = MediaType.IMAGE_PNG_VALUE)
@ResponseBody
public byte[] getUserAvatar(@PathVariable String username) {
    User user = userService.queryByUsername(username);
    if (user == null || user.getAvatar() == null || user.getAvatar().isEmpty()) {
        // 返回默认头像或空
        return new byte[0];
    }
    // avatar字段存储的是完整objectKey
    return userService.getAvatarBytes(user.getAvatar());
}
```

- [ ] **Step 1: Add getAvatarBytes method to IUserService**
- [ ] **Step 2: Implement in UserServiceImpl using MinioStorageStrategy**
- [ ] **Step 3: Add endpoint to UserController**
- [ ] **Step 4: Verify build**

---

## Chunk 10: 集成测试与验证

### 10.1 测试用例

- [ ] **Test 1: 上传文档**
  - 上传文件 → 检查MinIO中`documents/{uuid}`是否存在
  - 检查MySQL `file_document`表中`gridfsId`字段

- [ ] **Test 2: 获取文档预览**
  - 调用`GET /api/v1/file/view/{id}`
  - 验证从`documents/{gridfsId}`下载

- [ ] **Test 3: 上传头像**
  - 上传头像 → 检查MinIO中`avatars/{username}/{filename}`是否存在
  - 检查User表中avatar字段

- [ ] **Test 4: 获取头像**
  - 调用`GET /api/v1/user/avatar/{username}`
  - 验证从`avatars/{username}/...`下载

- [ ] **Test 5: 删除文档**
  - 删除文档 → 验证MySQL记录删除 + MinIO文件删除

### 10.2 手动验证步骤

```bash
# 1. 启动应用
cd C:/Project/java/all-docs && mvn spring-boot:run

# 2. 上传文档
curl -X POST http://localhost:8080/api/v1/file/auth/upload \
  -H "Authorization: Bearer {token}" \
  -F "file=@test.pdf"

# 3. 检查MinIO
# 登录MinIO Console，查看 all-docs-bucket 桶中的 documents/ 文件夹

# 4. 获取文档
curl http://localhost:8080/api/v1/file/view/{docId} \
  -o downloaded.pdf

# 5. 上传头像
curl -X POST http://localhost:8080/api/v1/user/auth/uploadUserAvatar \
  -H "Authorization: Bearer {token}" \
  -F "img=@avatar.jpg"

# 6. 获取头像
curl http://localhost:8080/api/v1/user/avatar/{username} -o avatar.jpg
```

---

## 任务清单汇总

| Chunk | 任务 | 状态 |
|-------|------|------|
| 1 | 存储策略层重构 | [ ] |
| 2 | 存储路径常量定义 | [ ] |
| 3 | DocumentService文档存储重构 | [ ] |
| 4 | ThumbnailService缩略图存储重构 | [ ] |
| 5 | UserService头像存储重构 | [ ] |
| 6 | FileController文件预览下载重构 | [ ] |
| 7 | FileOperationService工具服务重构 | [ ] |
| 8 | 数据库实体确认 | [ ] |
| 9 | 头像获取API | [ ] |
| 10 | 集成测试与验证 | [ ] |

---

## 风险与注意事项

1. **向后兼容性**: 如果已有数据，旧的UUID无法自动迁移到新路径
2. **头像字段变更**: `User.avatar` 从存储UUID变为存储完整objectKey
3. **删除逻辑**: 删除文档时需要同时删除关联的thumbs/previews/texts
4. **异常处理**: MinIO连接失败时需要适当降级

---

## 开始执行

计划完成，保存于 `docs/superpowers/plans/2026-04-25-minio-storage-refactor.md`

准备好后，使用 superpowers:subagent-driven-development 或 superpowers:executing-plans 开始执行。
