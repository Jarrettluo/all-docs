package com.jiaruiblog.infrastructure.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 存储工厂类，默认使用 MinIO 存储策略
 *
 * @author luojiarui
 * @version 1.0
 */
@Component
public class StorageFactory {

    @Autowired
    private MinioStorageStrategy minioStorageStrategy;

    /**
     * 获取默认的存储策略（固定返回 MinIO）
     *
     * @return 存储策略
     */
    public StorageStrategy getStorageStrategy() {
        return minioStorageStrategy;
    }

    /**
     * 根据类型获取存储策略（仅支持 minio）
     *
     * @param type 存储类型：minio
     * @return 存储策略
     */
    public StorageStrategy getStorageStrategy(String type) {
        if ("minio".equalsIgnoreCase(type)) {
            return minioStorageStrategy;
        }
        // 默认返回 MinIO
        return minioStorageStrategy;
    }
}