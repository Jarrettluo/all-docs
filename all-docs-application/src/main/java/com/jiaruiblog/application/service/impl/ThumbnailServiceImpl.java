package com.jiaruiblog.application.service.impl;

import com.jiaruiblog.application.service.ThumbnailService;
import com.jiaruiblog.common.constants.StorageConstants;
import com.jiaruiblog.infrastructure.repository.ThumbnailRepository;
import com.jiaruiblog.infrastructure.storage.MinioStorageStrategy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
            // 先将 InputStream 转换为 BufferedImage，再生成缩略图
            BufferedImage thumbImage = Thumbnails.of(ImageIO.read(inputStream))
                    .size(width, height)
                    .outputFormat("jpg")
                    .asBufferedImage();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(thumbImage, "jpg", baos);
            byte[] thumbBytes = baos.toByteArray();

            String thumbId = UUID.randomUUID().toString();
            String objectKey = StorageConstants.thumbPath(thumbId);

            String result = minioStorageStrategy.upload(new ByteArrayInputStream(thumbBytes), objectKey, "image/jpeg");

            if (result != null) {
                log.info("缩略图生成成功：fileName={}, thumbId={}, objectKey={}, size={}x{}", fileName, thumbId, objectKey, width, height);
                return thumbId; // 返回thumbId，调用方保存到MySQL
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
            BufferedImage previewImage = Thumbnails.of(ImageIO.read(inputStream))
                    .size(800, 800)
                    .outputFormat("jpg")
                    .asBufferedImage();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(previewImage, "jpg", baos);
            byte[] previewBytes = baos.toByteArray();

            String previewId = UUID.randomUUID().toString();
            String objectKey = StorageConstants.previewPath(previewId);

            String result = minioStorageStrategy.upload(new ByteArrayInputStream(previewBytes), objectKey, "image/jpeg");

            if (result != null) {
                log.info("预览图生成成功：fileName={}, previewId={}, objectKey={}", fileName, previewId, objectKey);
                return previewId; // 返回previewId，调用方保存到MySQL
            }

            log.error("预览图上传失败：fileName={}", fileName);
            return "";
        } catch (Exception e) {
            log.error("生成预览图异常：fileName={}", fileName, e);
            return "";
        }
    }
}
