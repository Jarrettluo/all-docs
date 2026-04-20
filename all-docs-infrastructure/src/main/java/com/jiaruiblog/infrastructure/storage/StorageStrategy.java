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
     * 获取文件URL
     *
     * @param fileId 文件ID
     * @return 文件访问URL
     */
    String getUrl(String fileId);
}