# All-Docs 项目架构设计文档

## 1. 项目概述

### 1.1 项目简介

All-Docs 是一个文档管理系统，支持文档上传、下载、在线预览、分类管理、标签管理、评论、收藏、点赞等核心功能。

### 1.2 技术选型

| 类别 | 技术 |
|------|------|
| 核心框架 | Spring Boot 3.2.0 |
| Java 版本 | Java 17 |
| 数据库 | MySQL 8.0+ |
| ORM 框架 | MyBatis 3.0 + MyBatis-Plus |
| 缓存 | Redis |
| 搜索引擎 | Elasticsearch |
| 对象存储 | MinIO (S3 兼容) |
| 任务调度 | Quartz |
| 认证 | JWT |
| 文档处理 | Apache Tika, Tess4j (OCR), iText |

---

## 2. 项目结构

### 2.1 模块架构

```
all-docs/
├── pom.xml                          # 父POM，统一版本管理
├── all-docs-common/                 # 通用层：工具类、通用类型、枚举
├── all-docs-domain/                 # 领域层：实体、DTO、VO、仓储接口
├── all-docs-infrastructure/         # 基础设施层：MyBatis Mapper、仓储实现、数据源配置
├── all-docs-application/            # 应用层：服务实现、任务调度
├── all-docs-api/                    # API层：控制器、拦截器、过滤器
├── all-docs-bootstrap/              # 启动模块
├── docs/                            # 文档
├── deploy/                          # 部署脚本
└── docker/                          # Docker配置
```

### 2.2 分层依赖关系

```
API Layer (all-docs-api)
         ↓
Application Layer (all-docs-application)
         ↓
Domain Layer (all-docs-domain)
         ↓
Infrastructure Layer (all-docs-infrastructure)
```

| 模块 | 职责 | 关键依赖 |
|------|------|---------|
| all-docs-common | 通用基础类型、无业务依赖 | Hutool, Caffeine, Validation API |
| all-docs-domain | 领域层：实体、DTO、VO、仓储接口 | MySQL(provided), ES(provided) |
| all-docs-infrastructure | 基础设施层：MyBatis Mapper、Redis/ES/MinIO 客户端 | MySQL, MyBatis, Redis, ES, MinIO |
| all-docs-application | 应用层：服务实现、任务调度 | Spring Boot Web, Quartz, AOP |
| all-docs-api | API层：REST控制器、Swagger、拦截器 | Spring Boot Web, JWT, SpringDoc |
| all-docs-bootstrap | 启动模块：Spring Boot主类 | 汇总所有依赖 |

---

## 3. 技术栈详表

### 3.1 核心依赖版本

| 组件 | 版本 |
|------|------|
| Spring Boot | 3.2.0 |
| Hutool | 5.8.25 |
| Lombok | 1.18.30 |
| FastJSON | 2.0.0 |
| java-jwt | 4.4.0 |
| MyBatis | 3.0.3 |
| Tika | 2.9.1 |
| Tess4j (OCR) | 5.8.0 |
| ANSJ (分词) | 5.1.6 |
| iText PDF | 5.5.13.3 |
| SpringDoc OpenAPI | 2.5.0 |
| MinIO | 8.5.7 |

### 3.2 数据存储

| 存储类型 | 说明 |
|----------|------|
| MySQL 8.0+ | 主数据库，存储业务数据 |
| Redis | 缓存、搜索历史、Session |
| Elasticsearch | 文档搜索 |
| MinIO | 文件存储 (S3 兼容) |

---

## 4. 数据模型

### 4.1 核心实体

| 实体 | 说明 | 表名 |
|------|------|------|
| User | 用户实体 | user |
| FileDocument | 文档实体 | file_document |
| Category | 分类实体 | category |
| Tag | 标签实体 | tag |
| Comment | 评论实体 | comment |
| DocReview | 文档审核实体 | doc_review |
| DocLog | 文档操作日志 | doc_log |
| Thumbnail | 缩略图 | thumbnail |
| CateDocRelationship | 分类-文档关系 | cate_doc_relationship |
| TagDocRelationship | 标签-文档关系 | tag_doc_relationship |
| CollectDocRelationship | 收藏-文档关系 | collect_doc_relationship |
| LikeDocRelationship | 点赞-文档关系 | like_relationship |

### 4.2 枚举类

| 枚举 | 说明 |
|------|------|
| PermissionEnum | 权限：USER(1), ADMIN(2), NO(-99999) |
| DocStateEnum | 文档状态：WAITE(0), ON_PROCESS(1), SUCCESS(2), FAIL(3) |
| DocType | 文档类型 |
| FilterTypeEnum | 过滤类型：CATEGORY, TAG, FILTER |
| FileFormatEnum | 文件格式 |
| RedisActionEnum | Redis操作类型 |

---

## 5. API 设计

### 5.1 控制器列表

| 控制器 | 路由前缀 | 主要功能 |
|--------|----------|----------|
| UserController | /api/v1/user | 用户管理：登录、注册、CRUD、权限管理 |
| DocumentController | /api/v1/document | 文档查询、列表、详情 |
| FileController | /api/v1/file | 文件上传、下载、预览 |
| CategoryController | /api/v1/category | 分类管理 |
| TagController | /api/v1/tag | 标签管理 |
| CommentController | /api/v1/comment | 评论管理 |
| CollectController | /api/v1/collect | 收藏管理 |
| LikeController | /api/v1/like | 点赞管理 |
| DocReviewController | /api/v1/doc-review | 文档审核 |
| DocLogController | /api/v1/doc-log | 操作日志 |
| StatisticsController | /api/v1/statistics | 统计分析 |
| SystemConfigController | /api/v1/system-config | 系统配置 |

### 5.2 API 版本控制

所有 API 采用 `/api/v1` 前缀进行版本管理，便于未来升级和兼容。

### 5.3 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

错误响应：
```json
{
  "code": 400,
  "message": "参数错误",
  "data": null
}
```

### 5.4 权限控制

使用 `@Permission(PermissionEnum.ADMIN)` 注解进行方法级权限控制。

### 5.5 JWT 白名单

以下路径无需 JWT 认证即可访问：
- `/api/v1/user/login`
- `/api/v1/user/register`
- `/api/v1/file/view`
- `/api/v1/file/image`
- `/api/v1/document/list`
- `/api/v1/category/all`
- Swagger/OpenAPI 路径

---

## 6. 核心服务组件

| 服务类 | 职责 |
|--------|------|
| DocumentService | 文档管理：上传、下载、删除、搜索 |
| UserService | 用户管理：注册、登录、权限、头像 |
| CategoryService | 分类管理 |
| TagService | 标签管理 |
| CommentService | 评论管理 |
| CollectService | 收藏管理 |
| LikeService | 点赞管理 |
| DocReviewService | 文档审核 |
| StatisticsService | 统计分析 |
| ElasticService | ES搜索 |
| RedisService | 缓存和搜索历史 |
| FileOperationService | 文件操作（缩略图、文本提取） |
| TaskExecuteService | 异步任务执行 |
| ThumbnailService | 缩略图生成 |

---

## 7. 数据库配置

### 7.1 MySQL 配置

```yaml
spring:
  datasource:
    url: jdbc:mysql://${MYSQL_HOST:localhost}:${MYSQL_PORT:3306}/${MYSQL_DATABASE:demo}?useSSL=false&serverTimezone=UTC&characterEncoding=utf8mb4
    username: ${MYSQL_USERNAME:root}
    password: ${MYSQL_PASSWORD}
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

### 7.2 MyBatis 配置

```java
@MapperScan("com.jiaruiblog.infrastructure.repository.mysql")
```

Mapper XML 文件位于：`src/main/resources/mapper/*.xml`

### 7.3 Redis

```yaml
spring:
  redis:
    database: 0
    host: ${REDIS_HOST:127.0.0.1}
    password: ${REDIS_PWD}
    port: ${REDIS_PORT:6379}
    timeout: 3000
    jedis:
      pool:
        max-idle: 500
        min-idle: 50
        max-active: 2000
```

### 7.4 Elasticsearch

```yaml
cloud:
  elasticsearch:
    host: ${ES_HOST:localhost}
    port: ${ES_PORT:9200}
```

---

## 8. 对象存储配置

### 8.1 MinIO 配置

```yaml
minio:
  endpoint: ${MINIO_ENDPOINT:http://localhost:9000}
  access-key: ${MINIO_ACCESS_KEY}
  secret-key: ${MINIO_SECRET_KEY}
  bucket: ${MINIO_BUCKET:alldocs}
  url-expiration: 3600

storage:
  type: minio  # 固定使用 MinIO
```

### 8.2 存储操作

通过 `StorageFactory` 获取 `MinioStorageStrategy` 进行文件操作：
- `upload()` - 上传文件
- `download()` - 下载文件
- `delete()` - 删除文件
- `getUrl()` - 获取预签名 URL

---

## 9. 安全认证

### 9.1 JWT 认证

- **JwtUtil**：密钥从环境变量 `JWT_SECRET` 获取，过期时间 2 天
- **JwtFilter**：拦截请求验证 JWT token

### 9.2 权限校验

**AuthenticationInterceptor** 拦截器配合 `@Permission` 注解进行方法级权限控制。

### 9.3 HMAC 下载保护

使用 `HmacUtil` 生成下载链接，HMAC 密钥存储在 Redis 中，10 分钟有效期。

---

## 10. 异常处理

### 10.1 异常类

| 异常类 | 说明 |
|--------|------|
| BusinessException | 业务异常 |
| BusinessExceptionBuilder | 异常构建器 |
| TaskRunException | 任务执行异常 |

### 10.2 全局异常处理

**GlobalExceptionHandler** (`@ControllerAdvice`) 处理以下异常：

- BusinessException
- MethodArgumentNotValidException
- ConstraintViolationException
- MaxUploadSizeExceededException
- AuthenticationException

---

## 11. 配置管理

### 11.1 主配置

位置：`all-docs-bootstrap/src/main/resources/application.yml`

```yaml
server:
  port: 8082

storage:
  type: minio  # 固定 MinIO

minio:
  endpoint: ${MINIO_ENDPOINT:http://localhost:9000}
  access-key: ${MINIO_ACCESS_KEY}
  secret-key: ${MINIO_SECRET_KEY}
  bucket: ${MINIO_BUCKET:alldocs}
```

### 11.2 环境配置

- `application-dev.yml` - 开发环境
- `application-prod.yml` - 生产环境

---

## 12. 部署架构

### 12.1 环境变量

| 变量名 | 说明 |
|--------|------|
| JWT_SECRET | JWT 密钥 |
| MYSQL_HOST | MySQL 地址 |
| MYSQL_PORT | MySQL 端口 |
| MYSQL_DATABASE | MySQL 数据库名 |
| MYSQL_USERNAME | MySQL 用户名 |
| MYSQL_PASSWORD | MySQL 密码 |
| REDIS_HOST | Redis 地址 |
| REDIS_PORT | Redis 端口 |
| REDIS_PWD | Redis 密码 |
| ES_HOST | Elasticsearch 地址 |
| ES_PORT | Elasticsearch 端口 |
| MINIO_ENDPOINT | MinIO 地址 |
| MINIO_ACCESS_KEY | MinIO Access Key |
| MINIO_SECRET_KEY | MinIO Secret Key |
| MINIO_BUCKET | MinIO Bucket |
| AD_INITIAL_USERNAME | 初始管理员用户名 |
| AD_INITIAL_PASSWORD | 初始管理员密码 |

### 12.2 文件限制

- 单文件最大：300MB
- 请求最大：500MB

---

## 13. 项目特性

1. **单一数据库架构**：使用 MySQL 作为主数据库，简化部署和维护
2. **对象存储**：使用 MinIO (S3 兼容) 存储文件
3. **全文检索**：基于 Elasticsearch 实现文档搜索
4. **敏感词过滤**：内置敏感词过滤器
5. **文档预览**：支持多种文档格式的在线预览
6. **异步任务**：基于 Quartz 和异步机制处理耗时操作
7. **完善的权限体系**：基于 JWT 和注解的权限控制

---

## 14. 数据库表结构

### 14.1 用户表 (user)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | VARCHAR(64) | 主键 |
| username | VARCHAR(100) | 用户名 |
| password | VARCHAR(255) | 密码 |
| phone | VARCHAR(20) | 手机号 |
| mail | VARCHAR(100) | 邮箱 |
| male | BOOLEAN | 性别 |
| description | TEXT | 个人描述 |
| avatar | VARCHAR(500) | 头像 URL |
| birthtime | DATETIME | 生日 |
| banning | BOOLEAN | 封禁状态 |
| permission_enum | INT | 权限枚举 |
| nickname | VARCHAR(100) | 昵称 |
| last_login | DATETIME | 最后登录时间 |
| create_date | DATETIME | 创建时间 |
| update_date | DATETIME | 更新时间 |

### 14.2 文档表 (file_document)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | VARCHAR(64) | 主键 |
| name | VARCHAR(255) | 文件名称 |
| size | BIGINT | 文件大小 |
| upload_date | DATETIME | 上传时间 |
| md5 | VARCHAR(64) | MD5 值 |
| content | BLOB | 文件内容 |
| content_type | VARCHAR(100) | 文件类型 |
| suffix | VARCHAR(50) | 文件后缀 |
| description | TEXT | 文件描述 |
| gridfs_id | VARCHAR(255) | MinIO 对象 ID |
| thumb_id | VARCHAR(255) | 缩略图 ID |
| text_file_id | VARCHAR(255) | 文本文件 ID |
| preview_file_id | VARCHAR(255) | 预览文件 ID |
| doc_state | INT | 文档状态 |
| error_msg | TEXT | 错误信息 |
| reviewing | BOOLEAN | 审核状态 |
| user_id | VARCHAR(64) | 用户 ID |
| user_name | VARCHAR(100) | 用户名 |

### 14.3 其他表

- `category` - 分类表
- `tag` - 标签表
- `comment` - 评论表
- `doc_review` - 审核表
- `doc_log` - 操作日志表
- `thumbnail` - 缩略图表
- `like_relationship` - 点赞关系表
- `collect_doc_relationship` - 收藏关系表
- `cate_doc_relationship` - 分类文档关系表
- `tag_doc_relationship` - 标签文档关系表
