package com.jiaruiblog.infrastructure.storage;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
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
