package com.jiaruiblog.application.task.executor;

import com.jiaruiblog.application.service.DocumentService;
import com.jiaruiblog.application.service.ElasticService;
import com.jiaruiblog.application.service.FileOperationService;
import com.jiaruiblog.application.service.ThumbnailService;
import com.jiaruiblog.application.task.data.TaskData;
import com.jiaruiblog.application.task.exception.TaskRunException;
import com.jiaruiblog.common.constants.StorageConstants;
import com.jiaruiblog.common.enums.FileFormatEnum;
import com.jiaruiblog.common.util.SpringApplicationContext;
import com.jiaruiblog.domain.entity.po.FileDocument;
import com.jiaruiblog.domain.entity.po.SearchDocument;
import com.jiaruiblog.infrastructure.storage.StorageFactory;
import com.jiaruiblog.infrastructure.storage.StorageStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * PDF 文档解析执行器，使用 Apache Tika 提取文字
 */
@Slf4j
public class PdfWordTaskExecutor extends TaskExecutor {

    @Override
    public void execute(TaskData taskData) throws TaskRunException {
        // 第一步下载文件，转换为byte数组
        FileDocument fileDocument = taskData.getFileDocument();
        byte[] dfsBytes = downFileBytes(fileDocument.getGridfsId());
        InputStream docInputStream = new ByteArrayInputStream(dfsBytes);

        // 第二步 将文本索引到es中
        try {
            uploadFileToEs(docInputStream, fileDocument, taskData);
        } catch (Exception e) {
            throw new TaskRunException("建立索引的时候出错!", e);
        }

        // 第三步 生成PDF缩略图（第一页渲染），存储到thumbnails文件夹
        docInputStream = new ByteArrayInputStream(dfsBytes);
        try {
            ThumbnailService thumbnailService = SpringApplicationContext.getBean(ThumbnailService.class);
            String thumbId = fileDocument.getMd5();
            String result = thumbnailService.makeThumbForPdf(docInputStream, fileDocument.getName(), thumbId);
            if (result != null && !result.isEmpty()) {
                fileDocument.setThumbId(result);
                log.info("PDF缩略图生成成功：docId={}, thumbId={}", fileDocument.getId(), result);
            }
        } catch (Exception e) {
            log.error("PDF缩略图生成失败：docId={}", fileDocument.getId(), e);
        }

        // 第四步 PDF不需要制作预览文件（预览文件指如PPT转PDF这类需要格式转换的文件）
        // makePreviewFile()在此不做任何处理
    }

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
        // PDF缩略图生成已移至execute()方法中通过ThumbnailService.makeThumbForPdf()实现
        // 此方法不再需要实现，保留接口签名以满足抽象类要求
        log.debug("PDF缩略图通过execute()中的ThumbnailService.makeThumbForPdf()生成");
    }

    @Override
    protected void makePreviewFile(InputStream is, TaskData taskData) {
        // PDF不需要格式转换，预览文件指如PPT转PDF这类需要格式转换的文件
        // PDF的缩略图已在execute()中通过makeThumbForPdf()生成并设置thumbId
        log.debug("PDF不需要生成预览文件");
    }
}