package com.jiaruiblog.application.service;

import java.io.InputStream;

/**
 * @author Jarrett Luo
 * @Date 2022/6/7 11:38
 * @Version 1.0
 */
public interface ThumbnailService {

    /**
     * @author luojiarui
     * @Description 生成缩略图
     * @Date 17:03 2022/11/5
     * @Param [inputStream, fileName]
     * @return java.lang.String 缩略图ID
     */
    String makeThumb(InputStream inputStream, String fileName);

    /**
     * @author luojiarui
     * @Description 生成缩略图
     * @Date 17:03 2022/11/5
     * @Param [inputStream, fileName, width, height]
     * @return java.lang.String 缩略图ID
     */
    String makeThumb(InputStream inputStream, String fileName, int width, int height);

    /**
     * @author luojiarui
     * @Description 生成预览图
     * @Date 17:03 2022/11/5
     * @Param [inputStream, fileName]
     * @return java.lang.String 预览图ID
     */
    String makePreview(InputStream inputStream, String fileName);

    /**
     * @Description 生成预览图
     * @Param [inputStream, fileName, previewId] - previewId 为null时自动生成UUID
     * @return java.lang.String 预览图ID
     */
    String makePreview(InputStream inputStream, String fileName, String previewId);

    /**
     * @Description 生成PDF预览图
     * @Param [inputStream, fileName, previewId] - previewId 为null时自动生成UUID
     * @return java.lang.String 预览图ID
     */
    String makePreviewForPdf(InputStream inputStream, String fileName, String previewId);

    /**
     * @Description 生成PPT/PPTX预览图
     * @Param [inputStream, fileName, previewId] - previewId 为null时自动生成UUID
     * @return java.lang.String 预览图ID
     */
    String makePreviewForPpt(InputStream inputStream, String fileName, String previewId);
}