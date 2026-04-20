package com.jiaruiblog.application.service.impl;

import com.jiaruiblog.application.service.TaskExecuteService;
import com.jiaruiblog.application.task.thread.MainTask;
import com.jiaruiblog.application.task.thread.TaskThreadPool;
import com.jiaruiblog.domain.entity.FileDocument;
import com.jiaruiblog.common.enums.DocType;
import org.springframework.stereotype.Service;

import java.io.InputStream;

/**
 * @author Jarrett Luo
 * @Date 2022/10/20 18:04
 * @Version 1.0
 */
@Service
public class TaskExecuteServiceImpl implements TaskExecuteService {

    @Override
    public void execute(FileDocument document) {
        MainTask mainTask = new MainTask(document);
        TaskThreadPool.getInstance().submit(mainTask);
    }

    @Override
    public String getExecutorType(DocType docType) {
        return "";
    }

    @Override
    public String uploadAndExecute(InputStream inputStream, String fileName, String contentType) {
        return "";
    }
}