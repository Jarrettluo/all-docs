package com.jiaruiblog.infrastructure.storage;

import java.io.InputStream;

/**
 * 存储策略接口
 *
 * @author luojiarui
 * @version 1.0
 */
public interface StorageStrategy {

    /**
     * 上传文件
     *
     * @param inputStream 文件输入流
     * @param filename    文件名
     * @param contentType  内容类型
     * @return 文件ID
     */
    String upload(InputStream inputStream, String filename, String contentType);

    /**
     * 下载文件
     *
     * @param fileId 文件ID
     * @return 文件输入流
     */
    InputStream download(String fileId);

    /**
     * 删除文件
     *
     * @param fileId 文件ID
     * @return 是否删除成功
     */
    boolean delete(String fileId);

    /**
     * 获取文件预签名URL
     *
     * @param objectKey 对象key (包含路径前缀)
     * @param expiry 过期时间(秒)
     * @return 预签名URL
     */
    String getPresignedUrl(String objectKey, int expiry);

    /**
     * 获取文件预签名URL (默认1小时过期)
     *
     * @param objectKey 对象key (包含路径前缀)
     * @return 预签名URL
     */
    default String getPresignedUrl(String objectKey) {
        return getPresignedUrl(objectKey, 3600);
    }
}