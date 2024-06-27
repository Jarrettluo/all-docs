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
    public static final Integer AUTH_ERROR_CODE = 1203;

    public static final String PARAMS_IS_NOT_NULL = "参数是必需的！";
    public static final String PARAMS_LENGTH_REQUIRED = "参数的长度必须符合要求！";
    public static final String PARAMS_FORMAT_ERROR = "参数格式错误！";
    public static final String PARAMS_TYPE_ERROR = "类型转换错误";
    public static final String DATA_HAS_EXIST = "数据已经存在！";
    public static final String DATA_IS_NULL = "数据为空！";
    public static final String FORMAT_ERROR = "格式不支持！";
    public static final String DATA_DUPLICATE = "已经重复！";
    public static final String REQUEST_METHOD_ERROR = "请求方法不对！";
    public static final String FILE_SIZE_ERROR = "上传的文件超过大小限制!";

    public static final String USER_HAS_BANNED = "该账号已经被屏蔽，请联系管理员！";

    public static final String OPERATE_FAILED = "操作失败！";
    public static final String SUCCESS = "SUCCESS";

    public static final String NOT_PERMISSION = "无权限";

    public static final String FILE_NOT_FOUND = "File was not found";
}
