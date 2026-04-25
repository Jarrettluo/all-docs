package com.jiaruiblog.application.task.executor;

import com.jiaruiblog.application.service.FileOperationService;
import com.jiaruiblog.application.task.data.TaskData;
import com.jiaruiblog.common.util.SpringApplicationContext;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * DOCX/DOC/XLS/XLSX 文档解析执行器，使用 Apache Tika 提取文字
 */
@Slf4j
public class DocxExecutor extends TaskExecutor {

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
        log.info("Office 文档文字提取完成，输出路径: {}", textFilePath);
    }

    @Override
    protected void makeThumb(InputStream is, String picPath) throws IOException {
        log.debug("Office 文档缩略图生成暂未实现");
    }

    @Override
    protected void makePreviewFile(InputStream is, TaskData taskData) {
        // 预览文件暂未实现
    }
}