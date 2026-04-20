package com.jiaruiblog.application.task.executor;

import com.jiaruiblog.application.task.data.TaskData;

import java.io.*;

/**
 * @ClassName TxtExecutor
 * @Description 直接读取文本的执行器
 * @author luojiarui
 * @Date 2023/2/26 11:22
 * @Version 1.0
 **/
public class TxtExecutor extends TaskExecutor{

    @Override
    protected void readText(InputStream is, String textFilePath) throws IOException {
        StringBuilder stringBuffer = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is))) {
            String content;
            while ((content = bufferedReader.readLine()) != null) {
                stringBuffer.append(content);
            }
        }

        File file = new File(textFilePath);
        try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)))) {
            bufferedWriter.write(stringBuffer.toString());
        }
    }

    @Override
    protected void makeThumb(InputStream is, String picPath) throws IOException {
        // no action
    }

    @Override
    protected void makePreviewFile(InputStream is, TaskData taskData) {
        // no action
    }
}