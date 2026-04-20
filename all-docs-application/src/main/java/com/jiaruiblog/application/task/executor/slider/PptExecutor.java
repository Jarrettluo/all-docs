package com.jiaruiblog.application.task.executor.slider;

import com.jiaruiblog.application.task.data.TaskData;
import com.jiaruiblog.application.task.exception.TaskRunException;
import com.jiaruiblog.application.task.executor.TaskExecutor;
import com.jiaruiblog.domain.entity.FileDocument;

import java.io.IOException;
import java.io.InputStream;

/**
 * <p></p>
 * edit at 2024/11/11 6:47
 *
 * @author Jarrett Luo
 * @version 1.0
 */
public class PptExecutor extends TaskExecutor {

    @Override
    protected void readText(InputStream is, String textFilePath) throws IOException {

    }

    @Override
    protected void makeThumb(InputStream is, String picPath) throws IOException {
        // TODO: Implement with PPTUtil
    }

    @Override
    protected void makePreviewFile(InputStream inStream, TaskData taskData) {
        // TODO: Implement with PptToPDFConverter
    }

    @Override
    public void uploadFileToEs(InputStream is, FileDocument fileDocument, TaskData taskData) {
        // TODO: Implement
    }
}