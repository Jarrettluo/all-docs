package com.jiaruiblog.application.task.executor;

import com.jiaruiblog.application.service.FileOperationService;
import com.jiaruiblog.application.service.ThumbnailService;
import com.jiaruiblog.application.task.data.TaskData;
import com.jiaruiblog.common.util.SpringApplicationContext;
import com.jiaruiblog.domain.entity.po.FileDocument;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * PDF 文档解析执行器，使用 Apache Tika 提取文字
 */
@Slf4j
public class PdfWordTaskExecutor extends TaskExecutor {

    @Override
    protected void readText(InputStream is, String textFilePath) throws IOException {
        if (is == null) {
            throw new IOException("输入流为空");
        }
        FileOperationService fileOperationService = SpringApplicationContext.getBean(FileOperationService.class);
        com.jiaruiblog.application.service.TextExtractResult result =
                fileOperationService.parseToStr(is);
        String content = result.isSuccess() ? result.getContent() : "";
        File file = new File(textFilePath);
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.write(content != null ? content : "");
        }
        log.info("PDF 文字提取完成，输出路径: {}", textFilePath);
    }

    @Override
    protected void makeThumb(InputStream is, String picPath) throws IOException {
        log.debug("PDF 缩略图生成暂未实现");
    }

    @Override
    protected void makePreviewFile(InputStream is, TaskData taskData) {
        if (is == null) {
            log.warn("PDF预览图生成失败：输入流为空");
            return;
        }
        try {
            ThumbnailService thumbnailService = SpringApplicationContext.getBean(ThumbnailService.class);
            FileDocument fileDocument = taskData.getFileDocument();
            // 使用 md5 + filename 作为预览图ID
            String previewId = fileDocument.getMd5() + "_" + fileDocument.getName();
            String result = thumbnailService.makePreviewForPdf(is, fileDocument.getName(), previewId);
            if (result != null && !result.isEmpty()) {
                fileDocument.setPreviewFileId(previewId);
                log.info("PDF预览图生成成功：docId={}, previewId={}", fileDocument.getId(), previewId);
            }
        } catch (Exception e) {
            log.error("PDF预览图生成异常：docId={}", taskData.getFileDocument().getId(), e);
        }
    }
}