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
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @Author Jarrett Luo
 * @Date 2022/10/20 17:59
 * @Version 1.0
 */
@Slf4j
public class MainTask implements RunnableTask {

    private TaskExecutor taskExecutor;

    private TaskData taskData = new TaskData();

    /**
     * @Author luojiarui
     * @Description // 初始化任务，指定一个
     * @Date 15:43 2022/11/13
     * @Param [fileDocument]
     **/
    public MainTask(FileDocument fileDocument) {
        taskData.setFileDocument(fileDocument);
        taskData.setThumbFilePath("");
        taskData.setThumbFilePath("");
        String fileSuffix = fileDocument.getSuffix();
        DocType docType = DocType.getDocType(fileSuffix);
        taskData.setDocType(docType);
        this.taskExecutor = TaskExecutorFactory.getTaskExecutor(docType);
    }

    /**
     * @Author luojiarui
     * @Description 成功以后更新文件
     * @Date 18:17 2022/11/13
     * @Param []
     **/
    @Override
    public void success() {
        taskData.getFileDocument().setDocState(DocStateEnum.SUCCESS);
        updateTaskStatus();

        // 更新文档的数据
        IFileService fileService = SpringApplicationContext.getBean(IFileService.class);
        fileService.updateFile(taskData.getFileDocument());
    }

    @Override
    public void failed(Throwable throwable) {
        String errorMsg = throwable.getLocalizedMessage();
        taskData.getFileDocument().setDocState(DocStateEnum.FAIL);
        taskData.getFileDocument().setErrorMsg(errorMsg);
        updateTaskStatus();
    }

    @Override
    public void run() {
        FileDocument fileDocument = taskData.getFileDocument();
        if (null == taskExecutor || fileDocument == null) {
            throw new TaskRunException("执行器失败");
        }
        if (StringUtils.hasText(fileDocument.getThumbId()) || StringUtils.hasText(fileDocument.getTextFileId())) {
            removeExistGridFs();
        }
        // 更新子任务数据,开始更新状态，开始进行解析等等
        taskData.getFileDocument().setDocState(DocStateEnum.ON_PROCESS);
        updateTaskStatus();

        // 调用执行器执行任务
        this.taskExecutor.execute(taskData);

    }

    @Override
    public void fallback() {
        // 删除es中的数据，删除thumb数据，删除存储的txt文本文件

        try {
            String txtFilePath = taskData.getTxtFilePath();
            if (new File(txtFilePath).exists()) {
                Files.delete(Paths.get(txtFilePath));
            }
            String picFilePath = taskData.getThumbFilePath();
            if (new File(picFilePath).exists()) {
                Files.delete(Paths.get(picFilePath));
            }
        } catch (IOException e) {
            log.error("删除文件路径{} ==> 失败信息{}", taskData.getTxtFilePath(), e);
        }

        // 删除相关的文件
        removeExistGridFs();

        // 删除es中的数据

    }


    private void updateTaskStatus() {
        IFileService fileService = SpringApplicationContext.getBean(IFileService.class);
        FileDocument fileDocument = taskData.getFileDocument();
        try {
            fileService.updateState(fileDocument, fileDocument.getDocState(), fileDocument.getErrorMsg());
        } catch (TaskRunException e) {
            throw new TaskRunException("更新文档状态失败", e);
        }
    }

    /**
     * @Author luojiarui
     * @Description 删除已经存在的文本文件和缩略图文件
     * @Date 18:19 2022/11/13
     * @Param []
     **/
    private void removeExistGridFs() {
        FileDocument fileDocument = taskData.getFileDocument();
        String textFileId = fileDocument.getTextFileId();
        String thumbFileId = fileDocument.getThumbId();

        IFileService fileService = SpringApplicationContext.getBean(IFileService.class);
        fileService.deleteGridFs(textFileId, thumbFileId);
    }

}
