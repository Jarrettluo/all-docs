package com.jiaruiblog.application.task.executor;

import com.jiaruiblog.application.task.data.TaskData;

import java.io.InputStream;

/**
 * @author Jarrett Luo
 * @Date 2022/10/24 11:42
 * @Version 1.0
 */
public class DocxExecutor extends TaskExecutor{

    @Override
    protected void readText(InputStream is, String textFilePath) {
        // TODO: Implement with MsExcelParse
    }

    @Override
    protected void makeThumb(InputStream is, String picPath) {
        // no action
    }

    @Override
    protected void makePreviewFile(InputStream is, TaskData taskData) {
        // no action
    }
}