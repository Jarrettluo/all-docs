package com.jiaruiblog.application.task.executor.slider;

import com.jiaruiblog.application.service.ThumbnailService;
import com.jiaruiblog.application.task.data.TaskData;
import com.jiaruiblog.application.task.executor.DocxExecutor;
import com.jiaruiblog.common.util.SpringApplicationContext;
import com.jiaruiblog.domain.entity.po.FileDocument;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

/**
 * @ClassName PptxExecutor
 * @Description Pptx转换执行器
 * @author luojiarui
 * @Date 2023/2/25 17:24
 * @Version 1.0
 **/
@Slf4j
public class PptxExecutor extends DocxExecutor {

    @Override
    protected void makePreviewFile(InputStream inStream, TaskData taskData) {
        if (inStream == null) {
            log.warn("PPTX预览图生成失败：输入流为空");
            return;
        }
        try {
            ThumbnailService thumbnailService = SpringApplicationContext.getBean(ThumbnailService.class);
            FileDocument fileDocument = taskData.getFileDocument();
            // 使用 md5 + filename 作为预览图ID
            String previewId = fileDocument.getMd5() + "_" + fileDocument.getName();
            String result = thumbnailService.makePreviewForPpt(inStream, fileDocument.getName(), previewId);
            if (result != null && !result.isEmpty()) {
                fileDocument.setPreviewFileId(previewId);
                log.info("PPTX预览图生成成功：docId={}, previewId={}", fileDocument.getId(), previewId);
            }
        } catch (Exception e) {
            log.error("PPTX预览图生成异常：docId={}", taskData.getFileDocument().getId(), e);
        }
    }
}