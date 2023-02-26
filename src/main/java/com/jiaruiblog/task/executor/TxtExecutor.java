package com.jiaruiblog.task.executor;

import com.jiaruiblog.entity.FileDocument;
import com.jiaruiblog.entity.FileObj;
import com.jiaruiblog.task.data.TaskData;
import com.jiaruiblog.task.exception.TaskRunException;

import java.io.IOException;
import java.io.InputStream;

/**
 * @ClassName TxtExecutor
 * @Description TODO
 * @Author luojiarui
 * @Date 2023/2/26 11:22
 * @Version 1.0
 **/
public class TxtExecutor extends TaskExecutor{

    @Override
    protected void readText(InputStream is, String textFilePath) throws IOException {

    }

    @Override
    protected void makeThumb(InputStream is, String picPath) throws IOException {

    }

    @Override
    protected void makePreviewFile(InputStream is, TaskData taskData) {

    }

    @Override
    public void uploadFileToEs(InputStream is, FileDocument fileDocument, TaskData taskData) {
        try {
            FileObj fileObj = new FileObj();
            fileObj.setId(fileDocument.getMd5());
            fileObj.setName(fileDocument.getName());
            fileObj.setType(fileDocument.getContentType());
            fileObj.readFile(is);
            this.upload(fileObj);
        } catch (IOException | TaskRunException e) {
            throw new TaskRunException("存入es的过程中报错了", e);
        }
    }
}
