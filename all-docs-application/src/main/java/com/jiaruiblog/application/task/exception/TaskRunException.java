package com.jiaruiblog.application.task.exception;

/**
 * @ClassName TaskRunException
 * @Description 任务执行异常
 * @author luojiarui
 * @Date 2023/2/25 16:26
 * @Version 1.0
 **/
public class TaskRunException extends RuntimeException {

    public TaskRunException(String message) {
        super(message);
    }

    public TaskRunException(String message, Throwable cause) {
        super(message, cause);
    }
}