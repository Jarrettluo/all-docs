package com.jiaruiblog.common;

/**
 * @ClassName MessageConstant
 * @Description 接口返回的各类常量信息
 * @Author luojiarui
 * @Date 2022/6/4 5:12 下午
 * @Version 1.0
 **/
public class MessageConstant {

    private MessageConstant() {
        throw new IllegalStateException("MessageConstant class");
    }

    public static final Integer PARAMS_ERROR_CODE = 1201;
    public static final Integer PROCESS_ERROR_CODE = 1202;

    public static final String PARAMS_IS_NOT_NULL = "参数是必需的！";
    public static final String PARAMS_LENGTH_REQUIRED = "参数的长度必须符合要求！";
    public static final String PARAMS_FORMAT_ERROR = "参数格式错误！";
    public static final String PARAMS_TYPE_ERROR = "类型转换错误";

    public static final String OPERATE_FAILED = "操作失败！";
    public static final String SUCCESS = "SUCCESS";

    public static final String FILE_NOT_FOUND = "File was not found";
}
