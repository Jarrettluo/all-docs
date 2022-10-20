package com.jiaruiblog.task.thread;

/**
 * @Author Jarrett Luo
 * @Date 2022/10/20 18:00
 * @Version 1.0
 */
public interface RunnableTask extends Runnable {

    void success();

    void failed();

}
