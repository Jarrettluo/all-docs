package com.jiaruiblog.task.executor;

import com.jiaruiblog.entity.FileDocument;
import com.jiaruiblog.enums.FileFormatEnum;
import com.jiaruiblog.task.data.TaskData;
import com.jiaruiblog.task.exception.TaskRunException;
import com.jiaruiblog.util.poi.Converter;
import com.jiaruiblog.util.poi.PptxToPDFConverter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.UUID;

/**
 * @ClassName PptxExecutor
 * @Description Pptx转换执行器
 * @Author luojiarui
 * @Date 2023/2/25 17:24
 * @Version 1.0
 **/
@Slf4j
public class PptxExecutor extends DocxExecutor{

    @Override
    protected void makePreviewFile(InputStream inStream, TaskData taskData) {
        taskData.setPreviewFilePath(UUID.randomUUID() + ".pdf");
        try {
            OutputStream outStream = getOutFileStream(taskData.getPreviewFilePath());
            Converter converter = new PptxToPDFConverter(inStream, outStream, true,
                    true);
            converter.convert();
        } catch (Exception e) {
            log.error("解析报错");
            throw new TaskRunException("转换预览文件报错", e);
        }
        // 解析出来的预览文件存储到文件系统中
        String objId = saveFileToDFS(taskData.getPreviewFilePath(), FileFormatEnum.PDF, "preview-");
        FileDocument fileDocument = taskData.getFileDocument();
        fileDocument.setPreviewFileId(objId);

    }

    protected static OutputStream getOutFileStream(String outputFilePath) throws IOException {
        File outFile = new File(outputFilePath);

        try{
            //Make all directories up to specified
            outFile.getParentFile().mkdirs();
        } catch (NullPointerException e){
            //Ignore error since it means not parent directories
        }

        if (!outFile.createNewFile()) {
            throw new TaskRunException("create file is error!");
        }
        return new FileOutputStream(outFile);
    }

}
