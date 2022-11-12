package com.jiaruiblog.service.impl;

import com.jiaruiblog.entity.FileDocument;
import com.jiaruiblog.service.TaskExecuteService;
import com.jiaruiblog.task.thread.MainTask;
import com.jiaruiblog.task.thread.TaskThreadPool;
import org.springframework.stereotype.Service;

/**
 * @Author Jarrett Luo
 * @Date 2022/10/20 18:04
 * @Version 1.0
 */
@Service
public class TaskExecuteServiceImpl implements TaskExecuteService {

    @Override
    public void execute(FileDocument fileDocument) {
        MainTask mainTask = new MainTask(fileDocument);
        TaskThreadPool.getInstance().submit(mainTask);
    }
}
