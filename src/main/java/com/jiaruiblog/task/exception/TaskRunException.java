package com.jiaruiblog.task.exception;

/**
 * @Author Jarrett Luo
 * @Date 2022/10/26 17:57
 * @Version 1.0
 */
public class TaskRunException extends RuntimeException {


    static final long serialVersionUID = 781837582814609045L;


    public TaskRunException(){
        super();
    }

    public TaskRunException(String message) {
        super(message);
    }

    public TaskRunException(Throwable cause) {
        super(cause);
    }

    public TaskRunException(String message, Throwable cause) {
        super(message, cause);
    }
}
