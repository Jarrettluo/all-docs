package com.jiaruiblog.application.service.impl;

import com.jiaruiblog.application.service.TaskExecuteService;
import com.jiaruiblog.application.task.data.TaskData;
import com.jiaruiblog.application.task.executor.TaskExecutor;
import com.jiaruiblog.application.task.executor.TaskExecutorFactory;
import com.jiaruiblog.application.task.thread.MainTask;
import com.jiaruiblog.application.task.thread.TaskThreadPool;
import com.jiaruiblog.common.enums.DocType;
import com.jiaruiblog.domain.entity.FileDocument;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.UUID;

/**
 * @author Jarrett Luo
 * @Date 2022/10/20 18:04
 * @Version 1.0
 */
@Slf4j
@Service
public class TaskExecuteServiceImpl implements TaskExecuteService {

    @Resource
    private TaskThreadPool taskThreadPool;

    @Override
    public void execute(FileDocument document) {
        if (document == null) {
            log.warn("执行任务失败：文档对象为空");
            return;
        }
        MainTask mainTask = new MainTask(document);
        TaskThreadPool.getInstance().submit(mainTask);
        log.info("提交文档处理任务：docId={}, docName={}", document.getId(), document.getName());
    }

    @Override
    public String getExecutorType(DocType docType) {
        if (docType == null) {
            return "";
        }
        TaskExecutor executor = TaskExecutorFactory.getTaskExecutor(docType);
        if (executor == null) {
            log.warn("未找到对应的执行器：docType={}", docType);
            return "";
        }
        return executor.getClass().getSimpleName();
    }

    @Override
    public String uploadAndExecute(InputStream inputStream, String fileName, String contentType) {
        if (inputStream == null || fileName == null || fileName.isEmpty()) {
            log.warn("上传并执行任务失败：参数为空");
            return "";
        }

        try {
            // 生成唯一ID
            String taskId = UUID.randomUUID().toString();

            // 根据文件扩展名获取文档类型
            String suffix = getFileExtension(fileName);
            DocType docType = DocType.getDocType(suffix);

            // 获取对应的执行器
            TaskExecutor executor = TaskExecutorFactory.getTaskExecutor(docType);
            if (executor == null) {
                log.error("未找到执行器：docType={}", docType);
                return "";
            }

            log.info("开始处理文件：fileName={}, docType={}, executor={}",
                    fileName, docType, executor.getClass().getSimpleName());

            // 这里可以添加文件上传和处理的逻辑
            // 由于是异步处理，返回任务ID
            return taskId;

        } catch (Exception e) {
            log.error("上传并执行任务异常：fileName={}", fileName, e);
            return "";
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return fileName.substring(lastDotIndex).toLowerCase();
        }
        return "";
    }
}