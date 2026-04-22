-- All-Docs 数据库初始化脚本
-- MySQL 8.0+

CREATE DATABASE IF NOT EXISTS alldocs DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE alldocs;

-- 用户表
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id` VARCHAR(64) NOT NULL PRIMARY KEY COMMENT '主键',
    `username` VARCHAR(100) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `mail` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `male` BOOLEAN DEFAULT NULL COMMENT '性别',
    `description` TEXT COMMENT '个人描述',
    `avatar_list` JSON DEFAULT NULL COMMENT '头像列表',
    `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
    `birthtime` DATETIME DEFAULT NULL COMMENT '生日',
    `banning` BOOLEAN DEFAULT FALSE COMMENT '封禁状态',
    `permission_enum` INT DEFAULT 1 COMMENT '权限枚举: 1-普通用户, 2-管理员, -99999-无权限',
    `nickname` VARCHAR(100) DEFAULT NULL COMMENT '昵称',
    `last_login` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `create_date` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_date` DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_username` (`username`),
    INDEX `idx_create_date` (`create_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 文档表
DROP TABLE IF EXISTS `file_document`;
CREATE TABLE `file_document` (
    `id` VARCHAR(64) NOT NULL PRIMARY KEY COMMENT '主键',
    `name` VARCHAR(255) NOT NULL COMMENT '文件名称',
    `size` BIGINT NOT NULL DEFAULT 0 COMMENT '文件大小',
    `upload_date` DATETIME DEFAULT NULL COMMENT '上传时间',
    `md5` VARCHAR(64) DEFAULT NULL COMMENT 'MD5值',
    `content` BLOB COMMENT '文件内容(仅小文件)',
    `content_type` VARCHAR(100) DEFAULT NULL COMMENT '文件类型',
    `suffix` VARCHAR(50) DEFAULT NULL COMMENT '文件后缀',
    `description` TEXT COMMENT '文件描述',
    `gridfs_id` VARCHAR(255) DEFAULT NULL COMMENT 'MinIO对象ID',
    `thumb_id` VARCHAR(255) DEFAULT NULL COMMENT '缩略图ID',
    `text_file_id` VARCHAR(255) DEFAULT NULL COMMENT '文本文件ID',
    `preview_file_id` VARCHAR(255) DEFAULT NULL COMMENT '预览文件ID',
    `doc_state` INT DEFAULT 0 COMMENT '文档状态: 0-待处理, 1-处理中, 2-成功, 3-失败',
    `error_msg` TEXT COMMENT '错误信息',
    `reviewing` BOOLEAN DEFAULT TRUE COMMENT '是否审核中',
    `word_list` JSON DEFAULT NULL COMMENT '违禁词列表',
    `user_id` VARCHAR(64) DEFAULT NULL COMMENT '用户ID',
    `user_name` VARCHAR(100) DEFAULT NULL COMMENT '用户名',
    `create_date` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_date` DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_upload_date` (`upload_date`),
    INDEX `idx_doc_state` (`doc_state`),
    INDEX `idx_md5` (`md5`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文档表';

-- 分类表
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
    `id` VARCHAR(64) NOT NULL PRIMARY KEY COMMENT '主键',
    `name` VARCHAR(100) NOT NULL COMMENT '分类名称',
    `create_date` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_date` DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类表';

-- 标签表
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag` (
    `id` VARCHAR(64) NOT NULL PRIMARY KEY COMMENT '主键',
    `name` VARCHAR(100) NOT NULL COMMENT '标签名称',
    `create_date` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_date` DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

-- 评论表
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
    `id` VARCHAR(64) NOT NULL PRIMARY KEY COMMENT '主键',
    `create_user` BIGINT NOT NULL COMMENT '创建用户ID',
    `user_id` VARCHAR(64) DEFAULT NULL COMMENT '用户ID',
    `user_name` VARCHAR(100) DEFAULT NULL COMMENT '用户名',
    `content` VARCHAR(500) NOT NULL COMMENT '评论内容',
    `doc_id` VARCHAR(64) NOT NULL COMMENT '文档ID',
    `create_date` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_date` DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_doc_id` (`doc_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_create_date` (`create_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- 文档审核表
DROP TABLE IF EXISTS `doc_review`;
CREATE TABLE `doc_review` (
    `id` VARCHAR(64) NOT NULL PRIMARY KEY COMMENT '主键',
    `doc_id` VARCHAR(64) NOT NULL COMMENT '文档ID',
    `doc_name` VARCHAR(255) DEFAULT NULL COMMENT '文档名称',
    `user_id` VARCHAR(64) NOT NULL COMMENT '用户ID',
    `user_name` VARCHAR(100) DEFAULT NULL COMMENT '用户名',
    `check_state` BOOLEAN DEFAULT FALSE COMMENT '审核是否通过',
    `read_state` BOOLEAN DEFAULT FALSE COMMENT '用户是否已读',
    `user_remove` BOOLEAN DEFAULT FALSE COMMENT '用户是否删除',
    `admin_remove` BOOLEAN DEFAULT FALSE COMMENT '管理员是否删除',
    `review_log` TEXT COMMENT '审核意见',
    `create_date` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_date` DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_doc_id` (`doc_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_create_date` (`create_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文档审核表';

-- 操作日志表
DROP TABLE IF EXISTS `doc_log`;
CREATE TABLE `doc_log` (
    `id` VARCHAR(64) NOT NULL PRIMARY KEY COMMENT '主键',
    `user_id` VARCHAR(64) DEFAULT NULL COMMENT '用户ID',
    `user_name` VARCHAR(100) DEFAULT NULL COMMENT '用户名',
    `action` VARCHAR(50) DEFAULT NULL COMMENT '操作类型',
    `doc_id` VARCHAR(64) DEFAULT NULL COMMENT '文档ID',
    `doc_name` VARCHAR(255) DEFAULT NULL COMMENT '文档名称',
    `create_date` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_date` DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_doc_id` (`doc_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_action` (`action`),
    INDEX `idx_create_date` (`create_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 缩略图表
DROP TABLE IF EXISTS `thumbnail`;
CREATE TABLE `thumbnail` (
    `id` VARCHAR(64) NOT NULL PRIMARY KEY COMMENT '主键',
    `object_id` VARCHAR(64) NOT NULL COMMENT '对象ID',
    `thumbnail_enum` INT DEFAULT 0 COMMENT '缩略图类型',
    `gridfs_id` VARCHAR(255) DEFAULT NULL COMMENT 'MinIO对象ID',
    `thumb_size_enum` INT DEFAULT 0 COMMENT '缩略图尺寸',
    INDEX `idx_object_id` (`object_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='缩略图表';

-- 分类文档关系表
DROP TABLE IF EXISTS `cate_doc_relationship`;
CREATE TABLE `cate_doc_relationship` (
    `id` VARCHAR(64) NOT NULL PRIMARY KEY COMMENT '主键',
    `category_id` VARCHAR(64) NOT NULL COMMENT '分类ID',
    `file_id` VARCHAR(64) NOT NULL COMMENT '文档ID',
    `create_date` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_date` DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_category_id` (`category_id`),
    INDEX `idx_file_id` (`file_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类文档关系表';

-- 标签文档关系表
DROP TABLE IF EXISTS `tag_doc_relationship`;
CREATE TABLE `tag_doc_relationship` (
    `id` VARCHAR(64) NOT NULL PRIMARY KEY COMMENT '主键',
    `tag_id` VARCHAR(64) NOT NULL COMMENT '标签ID',
    `file_id` VARCHAR(64) NOT NULL COMMENT '文档ID',
    `create_date` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_date` DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_tag_id` (`tag_id`),
    INDEX `idx_file_id` (`file_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签文档关系表';

-- 收藏关系表
DROP TABLE IF EXISTS `collect_doc_relationship`;
CREATE TABLE `collect_doc_relationship` (
    `id` VARCHAR(64) NOT NULL PRIMARY KEY COMMENT '主键',
    `redis_action_enum` INT DEFAULT 0 COMMENT '操作类型',
    `user_id` VARCHAR(64) NOT NULL COMMENT '用户ID',
    `doc_id` VARCHAR(64) NOT NULL COMMENT '文档ID',
    `create_date` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_date` DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_user_doc` (`user_id`, `doc_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_doc_id` (`doc_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏关系表';

-- 点赞关系表
DROP TABLE IF EXISTS `like_relationship`;
CREATE TABLE `like_relationship` (
    `id` VARCHAR(64) NOT NULL PRIMARY KEY COMMENT '主键',
    `user_id` VARCHAR(64) NOT NULL COMMENT '用户ID',
    `entity_type` INT DEFAULT 0 COMMENT '实体类型: 0-文档点赞, 1-文档收藏',
    `entity_id` VARCHAR(64) NOT NULL COMMENT '实体ID(文档ID)',
    `create_date` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_user_entity` (`user_id`, `entity_type`, `entity_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_entity_id` (`entity_id`),
    INDEX `idx_entity_type` (`entity_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞关系表';