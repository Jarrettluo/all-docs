package com.jiaruiblog.common.constants;

/**
 * MinIO存储路径常量
 * 统一管理存储路径前缀
 *
 * 存储结构：
 * - documents/{uniqueKey} - 用户上传的文档原文
 * - thumbs/{uniqueKey} - 文档缩略图
 * - previews/{uniqueKey} - 文档预览图
 * - texts/{uniqueKey} - 文档提取的文本内容
 * - avatars/{username}/{filename} - 用户头像
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
     * @param uniqueKey 唯一标识符(UUID)
     * @return documents/{uniqueKey}
     */
    public static String documentPath(String uniqueKey) {
        return DOCUMENTS + uniqueKey;
    }

    /**
     * 生成缩略图存储路径
     * @param uniqueKey 唯一标识符(UUID)
     * @return thumbs/{uniqueKey}
     */
    public static String thumbPath(String uniqueKey) {
        return THUMBS + uniqueKey;
    }

    /**
     * 生成头像存储路径
     * @param username 用户名
     * @param filename 文件名
     * @return avatars/{username}/{filename}
     */
    public static String avatarPath(String username, String filename) {
        return AVATARS + username + "/" + filename;
    }

    /**
     * 生成文本文件存储路径
     * @param uniqueKey 唯一标识符(UUID)
     * @return texts/{uniqueKey}
     */
    public static String textPath(String uniqueKey) {
        return TEXTS + uniqueKey;
    }

    /**
     * 生成预览文件存储路径
     * @param uniqueKey 唯一标识符(UUID)
     * @return previews/{uniqueKey}
     */
    public static String previewPath(String uniqueKey) {
        return PREVIEWS + uniqueKey;
    }
}
