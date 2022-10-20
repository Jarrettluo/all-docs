package com.jiaruiblog.service.impl;

import com.jiaruiblog.service.TaskExecuteService;
import com.jiaruiblog.task.thread.MainTask;
import com.jiaruiblog.task.thread.TaskThreadPool;

/**
 * @Author Jarrett Luo
 * @Date 2022/10/20 18:04
 * @Version 1.0
 */
public class TaskExecuteServiceImpl implements TaskExecuteService {

    @Override
    public void execute() {
        MainTask mainTask = new MainTask();
        TaskThreadPool.getInstance().submit(mainTask);
    }
}
