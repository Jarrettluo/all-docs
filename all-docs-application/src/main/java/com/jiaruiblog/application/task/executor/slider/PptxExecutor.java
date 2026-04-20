package com.jiaruiblog.application.task.executor.slider;

import com.jiaruiblog.application.task.data.TaskData;
import com.jiaruiblog.application.task.executor.DocxExecutor;
import com.jiaruiblog.application.task.exception.TaskRunException;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

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
    protected void makeThumb(InputStream is, String picPath) {
        // TODO: Implement with PPTUtil
    }

    @Override
    protected void makePreviewFile(InputStream inStream, TaskData taskData) {
        // TODO: Implement with PptxToPDFConverter
    }

    public void uploadFileToEs(InputStream is, FileDocument fileDocument, TaskData taskData) {
        // TODO: Implement
    }

    private static final class FileDocument extends com.jiaruiblog.domain.entity.FileDocument {
    }

    private static FileDocument getFileDocument(TaskData taskData) {
        return (FileDocument) taskData.getFileDocument();
    }
}