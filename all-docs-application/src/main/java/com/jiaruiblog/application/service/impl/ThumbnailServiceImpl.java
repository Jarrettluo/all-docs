package com.jiaruiblog.application.service.impl;

import com.jiaruiblog.application.service.ThumbnailService;
import com.jiaruiblog.domain.entity.Thumbnail;
import com.jiaruiblog.infrastructure.repository.ThumbnailRepository;
import com.jiaruiblog.infrastructure.storage.MinioStorageStrategy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

/**
 * 缩略图服务实现类
 *
 * @author luojiarui
 * @version 1.0
 */
@Slf4j
@Service
public class ThumbnailServiceImpl implements ThumbnailService {

    private static final int DEFAULT_WIDTH = 200;
    private static final int DEFAULT_HEIGHT = 200;

    @Resource
    private ThumbnailRepository thumbnailRepository;

    @Resource
    private MinioStorageStrategy minioStorageStrategy;

    @Override
    public String makeThumb(InputStream inputStream, String fileName) {
        return makeThumb(inputStream, fileName, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    @Override
    public String makeThumb(InputStream inputStream, String fileName, int width, int height) {
        if (inputStream == null || fileName == null || fileName.isEmpty()) {
            log.warn("生成缩略图失败：输入参数为空");
            return "";
        }

        try {
            // 使用 Thumbnailator 生成缩略图
            byte[] thumbBytes = Thumbnails.of(inputStream)
                    .size(width, height)
                    .outputFormat("jpg")
                    .asBytes(ByteArrayInputStream.class);

            String thumbId = UUID.randomUUID().toString();
            String contentType = getContentType(fileName);

            String result = minioStorageStrategy.upload(new ByteArrayInputStream(thumbBytes), thumbId, contentType);

            if (result != null) {
                log.info("缩略图生成成功：fileName={}, thumbId={}, size={}x{}", fileName, thumbId, width, height);
                return thumbId;
            }

            log.error("缩略图上传失败：fileName={}", fileName);
            return "";
        } catch (Exception e) {
            log.error("生成缩略图异常：fileName={}", fileName, e);
            return "";
        }
    }

    @Override
    public String makePreview(InputStream inputStream, String fileName) {
        if (inputStream == null || fileName == null || fileName.isEmpty()) {
            log.warn("生成预览图失败：输入参数为空");
            return "";
        }

        try {
            // 预览图使用较大的尺寸
            byte[] previewBytes = Thumbnails.of(inputStream)
                    .size(800, 800)
                    .outputFormat("jpg")
                    .asBytes(ByteArrayInputStream.class);

            String previewId = UUID.randomUUID().toString();
            String contentType = getContentType(fileName);

            String result = minioStorageStrategy.upload(new ByteArrayInputStream(previewBytes), previewId, contentType);

            if (result != null) {
                log.info("预览图生成成功：fileName={}, previewId={}", fileName, previewId);
                return previewId;
            }

            log.error("预览图上传失败：fileName={}", fileName);
            return "";
        } catch (Exception e) {
            log.error("生成预览图异常：fileName={}", fileName, e);
            return "";
        }
    }

    /**
     * 根据文件扩展名获取内容类型
     */
    private String getContentType(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "image/jpeg";
        }
        String lowerFileName = fileName.toLowerCase();
        if (lowerFileName.endsWith(".png")) {
            return "image/png";
        } else if (lowerFileName.endsWith(".gif")) {
            return "image/gif";
        } else if (lowerFileName.endsWith(".bmp")) {
            return "image/bmp";
        } else {
            return "image/jpeg";
        }
    }
}
