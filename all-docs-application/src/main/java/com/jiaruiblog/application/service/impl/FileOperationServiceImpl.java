package com.jiaruiblog.application.service.impl;

import com.jiaruiblog.application.service.FileOperationService;
import com.jiaruiblog.infrastructure.storage.MinioStorageStrategy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author luojiarui
 **/
@Slf4j
@Service
public class FileOperationServiceImpl implements FileOperationService {

    private final Tika tika = new Tika();

    @Resource
    private MinioStorageStrategy minioStorageStrategy;

    @Override
    public String parseToStr(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            log.warn("解析文件失败：文件为空");
            return "";
        }
        try {
            String contentType = file.getContentType();
            if (contentType != null && contentType.startsWith("text/")) {
                return new String(file.getBytes());
            }
            // 使用Tika解析文件内容
            return tika.parseToString(file.getInputStream());
        } catch (Exception e) {
            log.error("解析MultipartFile失败", e);
            return "";
        }
    }

    @Override
    public String parseToStr(String path) {
        if (path == null || path.isEmpty()) {
            log.warn("解析文件失败：路径为空");
            return "";
        }
        File file = new File(path);
        if (!file.exists() || !file.isFile()) {
            log.warn("解析文件失败：文件不存在或不是文件：{}", path);
            return "";
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            return tika.parseToString(fis);
        } catch (Exception e) {
            log.error("解析文件失败：{}", path, e);
            return "";
        }
    }

    @Override
    public String uploadFile(String name, InputStream inputStream) {
        if (name == null || name.isEmpty() || inputStream == null) {
            log.warn("上传文件失败：参数为空");
            return "";
        }
        try {
            String fileName = generateFileName(name);
            String contentType = tika.detect(inputStream);
            inputStream.reset();
            String result = minioStorageStrategy.upload(inputStream, fileName, contentType);
            log.info("上传文件成功：originalName={}, fileName={}", name, fileName);
            return result != null ? fileName : "";
        } catch (Exception e) {
            log.error("上传文件失败：name={}", name, e);
            return "";
        }
    }

    @Override
    public String uploadFile(String name, File file) {
        if (name == null || name.isEmpty() || file == null || !file.exists()) {
            log.warn("上传文件失败：参数无效");
            return "";
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            return uploadFile(name, fis);
        } catch (IOException e) {
            log.error("上传文件失败：name={}", name, e);
            return "";
        }
    }

    @Override
    public String uploadFile(String name, InputStream inputStream, long size) {
        return uploadFile(name, inputStream);
    }

    @Override
    public String uploadFile(String name, File file, long size) {
        return uploadFile(name, file);
    }

    @Override
    public InputStream getFile(String name) {
        if (name == null || name.isEmpty()) {
            log.warn("获取文件失败：文件名为空");
            return null;
        }
        try {
            return minioStorageStrategy.download(name);
        } catch (Exception e) {
            log.error("获取文件失败：name={}", name, e);
            return null;
        }
    }

    @Override
    public InputStream getFile(String name, Integer expires) {
        // MinIO的presigned URL支持expires参数，但download方法不直接支持
        // 这里简化处理，返回普通文件流
        return getFile(name);
    }

    @Override
    public void deleteFile(String name) {
        if (name == null || name.isEmpty()) {
            log.warn("删除文件失败：文件名为空");
            return;
        }
        try {
            minioStorageStrategy.delete(name);
            log.info("删除文件成功：name={}", name);
        } catch (Exception e) {
            log.error("删除文件失败：name={}", name, e);
        }
    }

    @Override
    public String getFileUrl(String name) {
        if (name == null || name.isEmpty()) {
            log.warn("获取文件URL失败：文件名为空");
            return "";
        }
        try {
            return minioStorageStrategy.getUrl(name);
        } catch (Exception e) {
            log.error("获取文件URL失败：name={}", name, e);
            return "";
        }
    }

    @Override
    public String getFileUrl(String name, Integer expires) {
        // 同样简化处理
        return getFileUrl(name);
    }

    @Override
    public List<String> listNames() {
        // MinIO不提供列出所有对象的API，这里返回空列表
        log.warn("listNames方法暂不支持MinIO存储");
        return new ArrayList<>();
    }

    @Override
    public boolean isExist(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        // 尝试获取文件，如果成功则存在
        try {
            InputStream is = minioStorageStrategy.download(name);
            if (is != null) {
                is.close();
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 生成唯一的文件名
     */
    private String generateFileName(String originalName) {
        String prefix = UUID.randomUUID().toString();
        if (originalName != null && !originalName.isEmpty()) {
            int dotIndex = originalName.lastIndexOf('.');
            if (dotIndex > 0) {
                String extension = originalName.substring(dotIndex);
                return prefix + extension;
            }
        }
        return prefix;
    }
}