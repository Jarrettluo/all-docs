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
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
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
            String objectKey = StorageConstants.thumbPath(thumbId, "jpg");

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
            String objectKey = StorageConstants.previewPath(previewId, "jpg");

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
            String objectKey = StorageConstants.previewPath(previewId, "jpg");

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

    @Override
    public String makeThumbForPdf(InputStream inputStream, String fileName, String thumbId) {
        if (inputStream == null || fileName == null || fileName.isEmpty()) {
            log.warn("生成PDF缩略图失败：输入参数为空");
            return "";
        }

        PDDocument document = null;
        try {
            document = PDDocument.load(inputStream);
            PDFRenderer pdfRenderer = new PDFRenderer(document);

            // 渲染第一页作为缩略图
            BufferedImage pdfImage = pdfRenderer.renderImageWithDPI(0, 150, ImageType.RGB);
            // 转换为 jpg
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(pdfImage, "jpg", baos);
            byte[] thumbBytes = baos.toByteArray();

            if (thumbId == null || thumbId.isEmpty()) {
                thumbId = UUID.randomUUID().toString();
            }
            // 存储到thumbnails文件夹，带.jpg后缀
            String objectKey = StorageConstants.thumbPath(thumbId, "jpg");

            String result = minioStorageStrategy.upload(new ByteArrayInputStream(thumbBytes), objectKey, "image/jpeg");

            if (result != null) {
                log.info("PDF缩略图生成成功：fileName={}, thumbId={}, objectKey={}, pages={}",
                        fileName, thumbId, objectKey, document.getNumberOfPages());
                return thumbId;
            }

            log.error("PDF缩略图上传失败：fileName={}", fileName);
            return "";
        } catch (Exception e) {
            log.error("生成PDF缩略图异常：fileName={}", fileName, e);
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
    public String makeThumbForPpt(InputStream inputStream, String fileName, String thumbId) {
        if (inputStream == null || fileName == null || fileName.isEmpty()) {
            log.warn("生成PPT缩略图失败：输入参数为空");
            return "";
        }

        XMLSlideShow slideShow = null;
        try {
            String lowerName = fileName.toLowerCase();
            if (!lowerName.endsWith(".pptx")) {
                log.warn("PPT缩略图仅支持PPTX格式：fileName={}", fileName);
                return "";
            }

            slideShow = new XMLSlideShow(inputStream);
            List<XSLFSlide> slides = slideShow.getSlides();
            if (slides == null || slides.isEmpty()) {
                log.warn("PPT文件中没有幻灯片：fileName={}", fileName);
                return "";
            }

            // 获取第一张幻灯片
            XSLFSlide firstSlide = slides.get(0);

            // 获取幻灯片尺寸
            Dimension slideSize = slideShow.getPageSize();
            double width = slideSize.width;
            double height = slideSize.height;

            // 创建缓冲区图像
            BufferedImage image = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = image.createGraphics();

            // 设置背景和渲染选项
            graphics.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING, java.awt.RenderingHints.VALUE_RENDER_QUALITY);

            // 设置白色背景
            graphics.setPaint(java.awt.Color.WHITE);
            graphics.fill(new Rectangle2D.Float(0, 0, (float) width, (float) height));

            // 渲染第一张幻灯片
            firstSlide.draw(graphics);

            // 缩放为缩略图 (200x200)
            BufferedImage thumbImage = Thumbnails.of(image)
                    .size(200, 200)
                    .asBufferedImage();

            // 转换为jpg
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(thumbImage, "jpg", baos);
            byte[] thumbBytes = baos.toByteArray();

            if (thumbId == null || thumbId.isEmpty()) {
                thumbId = UUID.randomUUID().toString();
            }
            // 存储到thumbnails文件夹，带.jpg后缀
            String objectKey = StorageConstants.thumbPath(thumbId, "jpg");

            String result = minioStorageStrategy.upload(new ByteArrayInputStream(thumbBytes), objectKey, "image/jpeg");

            if (result != null) {
                log.info("PPT缩略图生成成功：fileName={}, thumbId={}, objectKey={}, totalSlides={}",
                        fileName, thumbId, objectKey, slides.size());
                return thumbId;
            }

            log.error("PPT缩略图上传失败：fileName={}", fileName);
            return "";
        } catch (Exception e) {
            log.error("生成PPT缩略图异常：fileName={}", fileName, e);
            return "";
        } finally {
            if (slideShow != null) {
                try {
                    slideShow.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    @Override
    public String makeThumbForDocx(InputStream inputStream, String fileName, String thumbId) {
        if (inputStream == null || fileName == null || fileName.isEmpty()) {
            log.warn("生成DOCX缩略图失败：输入参数为空");
            return "";
        }

        try {
            String lowerName = fileName.toLowerCase();
            if (!lowerName.endsWith(".docx")) {
                log.warn("DOCX缩略图仅支持DOCX格式：fileName={}", fileName);
                return "";
            }

            // DOCX缩略图生成需要XWPF和Java Graphics2D配合
            // 由于实现复杂度较高，暂使用占位实现
            log.info("DOCX缩略图生成暂未完全支持：fileName={}，需要进一步实现", fileName);
            return "";
        } catch (Exception e) {
            log.error("生成DOCX缩略图异常：fileName={}", fileName, e);
            return "";
        }
    }
}
