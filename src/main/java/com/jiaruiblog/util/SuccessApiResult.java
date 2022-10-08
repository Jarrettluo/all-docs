package com.jiaruiblog.util;

import java.io.Serializable;

/**
 * 成功返回体.
 *
 * @author KimZing - kimzing@163.com
 * @since 2019/12/4 17:19
 */
public final class SuccessApiResult<T> extends BaseApiResult implements Serializable {

    private static final long serialVersionUID = 1L;

    public SuccessApiResult() {
        this(null);
    }

    public SuccessApiResult(T data) {
        this.timestamp = System.currentTimeMillis();
        this.code = 200;
        this.data = data;
    }

    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }



}