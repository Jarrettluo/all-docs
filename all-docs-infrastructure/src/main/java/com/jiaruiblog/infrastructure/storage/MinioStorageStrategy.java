package com.jiaruiblog.infrastructure.storage;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * MinIO存储策略实现
 *
 * @author luojiarui
 * @version 1.0
 */
@Component
public class MinioStorageStrategy implements StorageStrategy {

    @Autowired
    private MinioClient minioClient;

    private static final String BUCKET_NAME = "alldocs";

    @Override
    public String upload(InputStream inputStream, String filename, String contentType) {
        try (InputStream is = inputStream) {
            // 确保bucket存在
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(BUCKET_NAME).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(BUCKET_NAME).build());
            }

            // 上传文件
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(BUCKET_NAME)
                    .object(filename)
                    .stream(is, -1, 10485760)
                    .contentType(contentType)
                    .build());

            return filename;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public InputStream download(String fileId) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(BUCKET_NAME)
                    .object(fileId)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean delete(String fileId) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(BUCKET_NAME)
                    .object(fileId)
                    .build());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String getUrl(String fileId) {
        try {
            Map<String, String> extraQueryParams = new HashMap<>();
            extraQueryParams.put("expires", "3600");
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(BUCKET_NAME)
                    .object(fileId)
                    .expiry(3600)
                    .extraQueryParams(extraQueryParams)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}