package com.jiaruiblog.task.thread;

import com.jiaruiblog.entity.FileDocument;
import com.jiaruiblog.enums.DocStateEnum;
import com.jiaruiblog.enums.DocType;
import com.jiaruiblog.service.IFileService;
import com.jiaruiblog.task.data.TaskData;
import com.jiaruiblog.task.exception.TaskRunException;
import com.jiaruiblog.task.executor.TaskExecutor;
import com.jiaruiblog.task.executor.TaskExecutorFactory;
import com.jiaruiblog.util.SpringApplicationContext;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;

/**
 * @Author Jarrett Luo
 * @Date 2022/10/20 17:59
 * @Version 1.0
 */
@Slf4j
public class MainTask implements RunnableTask{

    private TaskExecutor taskExecutor;

    private TaskData taskData = new TaskData();

    /**
     * @Author luojiarui
     * @Description // 初始化任务
     * @Date 15:43 2022/11/13
     * @Param [fileDocument]
     * @return
     **/
    public MainTask(FileDocument fileDocument) {
        taskData.setFileDocument(fileDocument);
        String fileSuffix = fileDocument.getSuffix();
        log.info("查询到的文件的后缀是{}", fileSuffix);
        this.taskExecutor = TaskExecutorFactory.getTaskExecutor(DocType.getDocType(fileSuffix));
    }

    @Override
    public void success() {
        // TODO document why this method is empty
        log.info("成功啦");
    }

    @Override
    public void failed() {
        // TODO document why this method is empty
        log.error("失败啦");

    }

    @Override
    public void run() {

        if (null == taskExecutor) {
            throw new NullPointerException(MessageFormat.format("空指针异常,异常数据{}", "taskData"));
        }

        // 更新子任务数据,开始更新状态，开始进行解析等等
        IFileService fileService = SpringApplicationContext.getBean(IFileService.class);
        try {
            fileService.updateState(taskData.getFileDocument(), DocStateEnum.ON_PROCESS);
            log.info("开始进行更新，");
        } catch (TaskRunException e) {
            e.printStackTrace();
            throw new RuntimeException("方式来减肥连锁机构", e);
        }

        try {
            // 调用执行器执行任务
            this.taskExecutor.execute(taskData);
        } catch (TaskRunException e) {
            throw new RuntimeException("方式来减肥连锁机构", e);
        }

    }

    @Override
    public void fallback() {
        log.info("这里进行数据的回滚");
        // 删除es中的数据，删除thumb数据，删除存储的txt文本文件

    }
}
