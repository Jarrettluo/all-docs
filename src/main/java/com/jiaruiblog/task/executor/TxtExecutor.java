package com.jiaruiblog.task.executor;

import com.jiaruiblog.task.data.TaskData;

import java.io.*;

/**
 * @ClassName TxtExecutor
 * @Description 直接读取文本的执行器
 * @Author luojiarui
 * @Date 2023/2/26 11:22
 * @Version 1.0
 **/
public class TxtExecutor extends TaskExecutor{

    @Override
    protected void readText(InputStream is, String textFilePath) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(is);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuilder stringBuffer = new StringBuilder();
        String content;
        while ((content = bufferedReader.readLine()) != null) {
            stringBuffer.append(content);
        }
        bufferedReader.close();
        inputStreamReader.close();
        is.close();

        File file = new File(textFilePath);
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));

        bufferedWriter.write(stringBuffer.toString());
        bufferedWriter.close();
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
