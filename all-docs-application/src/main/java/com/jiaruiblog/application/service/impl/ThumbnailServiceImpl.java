package com.jiaruiblog.application.service.impl;

import com.jiaruiblog.application.service.ThumbnailService;
import com.jiaruiblog.common.constants.StorageConstants;
import com.jiaruiblog.infrastructure.repository.ThumbnailRepository;
import com.jiaruiblog.infrastructure.storage.MinioStorageStrategy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.ImageType;
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
        return makePreview(inputStream, fileName, null);
    }

    @Override
    public String makePreview(InputStream inputStream, String fileName, String previewId) {
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

            if (previewId == null || previewId.isEmpty()) {
                previewId = UUID.randomUUID().toString();
            }
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

    @Override
    public String makePreviewForPdf(InputStream inputStream, String fileName, String previewId) {
        if (inputStream == null || fileName == null || fileName.isEmpty()) {
            log.warn("生成PDF预览图失败：输入参数为空");
            return "";
        }

        PDDocument document = null;
        try {
            document = PDDocument.load(inputStream);
            PDFRenderer pdfRenderer = new PDFRenderer(document);

            // 渲染第一页
            BufferedImage pdfImage = pdfRenderer.renderImageWithDPI(0, 150, ImageType.RGB);
            // 转换为 jpg
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(pdfImage, "jpg", baos);
            byte[] previewBytes = baos.toByteArray();

            if (previewId == null || previewId.isEmpty()) {
                previewId = UUID.randomUUID().toString();
            }
            String objectKey = StorageConstants.previewPath(previewId);

            String result = minioStorageStrategy.upload(new ByteArrayInputStream(previewBytes), objectKey, "image/jpeg");

            if (result != null) {
                log.info("PDF预览图生成成功：fileName={}, previewId={}, objectKey={}, pages={}",
                        fileName, previewId, objectKey, document.getNumberOfPages());
                return previewId;
            }

            log.error("PDF预览图上传失败：fileName={}", fileName);
            return "";
        } catch (Exception e) {
            log.error("生成PDF预览图异常：fileName={}", fileName, e);
            return "";
        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    @Override
    public String makePreviewForPpt(InputStream inputStream, String fileName, String previewId) {
        if (inputStream == null || fileName == null || fileName.isEmpty()) {
            log.warn("生成PPT预览图失败：输入参数为空");
            return "";
        }

        try {
            String lowerName = fileName.toLowerCase();
            if (!lowerName.endsWith(".pptx")) {
                log.warn("PPT预览图仅支持PPTX格式：fileName={}", fileName);
                return "";
            }

            // PPTX 预览图生成需要 POI 5.x 与 Java Graphics2D 配合
            // 由于 POI 5.x API 变化，暂时标记为不支持
            log.info("PPT预览图生成暂未完全支持：fileName={}，需要进一步适配 POI 5.x API", fileName);
            return "";
        } catch (Exception e) {
            log.error("生成PPT预览图异常：fileName={}", fileName, e);
            return "";
        }
    }
}
