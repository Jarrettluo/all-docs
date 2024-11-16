package com.jiaruiblog.task.executor.slider;

import com.jiaruiblog.entity.FileDocument;
import com.jiaruiblog.entity.FileObj;
import com.jiaruiblog.enums.FileFormatEnum;
import com.jiaruiblog.task.data.TaskData;
import com.jiaruiblog.task.exception.TaskRunException;
import com.jiaruiblog.task.executor.TaskExecutor;
import com.jiaruiblog.util.poi.Converter;
import com.jiaruiblog.util.poi.PPTUtil;
import com.jiaruiblog.util.poi.PptxToPDFConverter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import static com.jiaruiblog.task.executor.slider.PptxExecutor.getOutFileStream;

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
        PPTUtil.extractFirstPPT(is, picPath);
    }

    @Override
    protected void makePreviewFile(InputStream inStream, TaskData taskData) {
        taskData.setPreviewFilePath(UUID.randomUUID() + ".pdf");
        try {
            OutputStream outStream = getOutFileStream(taskData.getPreviewFilePath());
            Converter converter = new PptxToPDFConverter(inStream, outStream, true,
                    true);
            converter.convert();
        } catch (Exception e) {
            throw new TaskRunException("转换预览文件报错", e);
        }
        // 解析出来的预览文件存储到文件系统中
        String objId = saveFileToDFS(taskData.getPreviewFilePath(), FileFormatEnum.PDF, "preview-");
        FileDocument fileDocument = taskData.getFileDocument();
        fileDocument.setPreviewFileId(objId);
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
