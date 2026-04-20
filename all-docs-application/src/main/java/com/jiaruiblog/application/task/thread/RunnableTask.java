package com.jiaruiblog.application.task.thread;

/**
 * @author Jarrett Luo
 * @Date 2022/10/20 17:59
 * @Version 1.0
 */
public interface RunnableTask {
    void success();
    void failed(Throwable throwable);
    void run();
    void fallback();
}