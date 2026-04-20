package com.jiaruiblog.application.task.executor;

import com.jiaruiblog.application.task.data.TaskData;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Jarrett Luo
 * @Date 2022/10/24 11:32
 * @Version 1.0
 */
public class PdfWordTaskExecutor extends TaskExecutor {


    @Override
    protected void readText(InputStream is, String textFilePath) throws IOException {
        // TODO: Implement with PdfUtil
    }

    @Override
    protected void makeThumb(InputStream is, String picPath) {
        // TODO: Implement with PdfUtil
    }

    @Override
    protected void makePreviewFile(InputStream is, TaskData taskData) {
        // no action
    }
}