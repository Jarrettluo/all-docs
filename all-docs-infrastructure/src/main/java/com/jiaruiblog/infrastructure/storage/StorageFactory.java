package com.jiaruiblog.infrastructure.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 存储工厂类，根据配置选择不同的存储策略
 *
 * @author luojiarui
 * @version 1.0
 */
@Component
public class StorageFactory {

    @Autowired
    private GridFSStorageStrategy gridFSStorageStrategy;

    @Autowired
    private MinioStorageStrategy minioStorageStrategy;

    /**
     * 获取默认的存储策略
     * 可以根据配置动态选择，目前默认返回GridFS
     *
     * @return 存储策略
     */
    public StorageStrategy getStorageStrategy() {
        return gridFSStorageStrategy;
    }

    /**
     * 根据类型获取存储策略
     *
     * @param type 存储类型：gridfs 或 minio
     * @return 存储策略
     */
    public StorageStrategy getStorageStrategy(String type) {
        if ("minio".equalsIgnoreCase(type)) {
            return minioStorageStrategy;
        }
        return gridFSStorageStrategy;
    }
}