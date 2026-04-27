package com.jiaruiblog.application.task.executor.slider;

import com.jiaruiblog.application.service.FileOperationService;
import com.jiaruiblog.application.service.ThumbnailService;
import com.jiaruiblog.application.task.data.TaskData;
import com.jiaruiblog.application.task.executor.TaskExecutor;
import com.jiaruiblog.application.task.exception.TaskRunException;
import com.jiaruiblog.common.util.SpringApplicationContext;
import com.jiaruiblog.domain.entity.po.FileDocument;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * PPT 文档解析执行器，使用 Apache Tika 提取文字
 */
@Slf4j
public class PptExecutor extends TaskExecutor {

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

        // 第三步 生成PPT缩略图（第一页渲染），存储到thumbnails文件夹
        docInputStream = new ByteArrayInputStream(dfsBytes);
        try {
            ThumbnailService thumbnailService = SpringApplicationContext.getBean(ThumbnailService.class);
            String thumbId = fileDocument.getMd5() + "_" + fileDocument.getName();
            String result = thumbnailService.makeThumbForPpt(docInputStream, fileDocument.getName(), thumbId);
            if (result != null && !result.isEmpty()) {
                fileDocument.setThumbId(result);
                log.info("PPT缩略图生成成功：docId={}, thumbId={}", fileDocument.getId(), result);
            }
        } catch (Exception e) {
            log.error("PPT缩略图生成失败：docId={}", fileDocument.getId(), e);
        }

        // 第四步 生成预览文件（PPT转PDF），存储到previews文件夹
        // PPT预览文件生成需要完整的PPT转PDF转换，目前暂未实现
        docInputStream = new ByteArrayInputStream(dfsBytes);
        makePreviewFile(docInputStream, taskData);
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
        log.info("PPT 文字提取完成，输出路径: {}", textFilePath);
    }

    @Override
    protected void makeThumb(InputStream is, String picPath) throws IOException {
        // PPT缩略图生成已移至execute()方法中通过ThumbnailService.makeThumbForPpt()实现
        // 此方法不再需要实现，保留接口签名以满足抽象类要求
        log.debug("PPT缩略图通过execute()中的ThumbnailService.makeThumbForPpt()生成");
    }

    @Override
    protected void makePreviewFile(InputStream inStream, TaskData taskData) {
        // PPT预览文件（PPT转PDF）暂未实现
        // 预览文件转换需要完整的格式转换支持
        if (inStream == null) {
            log.warn("PPT预览文件生成失败：输入流为空");
            return;
        }
        try {
            ThumbnailService thumbnailService = SpringApplicationContext.getBean(ThumbnailService.class);
            FileDocument fileDocument = taskData.getFileDocument();
            // 使用 md5 + filename 作为预览文件ID
            String previewId = fileDocument.getMd5() + "_" + fileDocument.getName();
            String result = thumbnailService.makePreviewForPpt(inStream, fileDocument.getName(), previewId);
            if (result != null && !result.isEmpty()) {
                fileDocument.setPreviewFileId(previewId);
                log.info("PPT预览文件生成成功：docId={}, previewId={}", fileDocument.getId(), previewId);
            }
        } catch (Exception e) {
            log.error("PPT预览文件生成异常：docId={}", taskData.getFileDocument().getId(), e);
        }
    }
}