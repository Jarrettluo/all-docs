package com.jiaruiblog.task.thread;

/**
 * @Author Jarrett Luo
 * @Date 2022/10/20 18:00
 * @Version 1.0
 */
public interface RunnableTask extends Runnable {

    /**
     * 任务执行成功以后的方法
     */
    void success();

    /**
     * 任务执行失败以后的方法
     */
    void failed(Throwable throwable);

    void fallback();

}
