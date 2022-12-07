package com.jiaruiblog.config;

import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.util.BaseApiResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 全局统一异常处理
 * 捕获异常，产生异常时，统一返回错误信息
 *
 * @author jiarui.luo
 */
@Slf4j
@ControllerAdvice
public class CommonExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public BaseApiResult handle(Exception e) {
        e.printStackTrace();
        if (e instanceof MaxUploadSizeExceededException) {
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, "上传的文件超过大小限制");
        } else if (e instanceof HttpRequestMethodNotSupportedException) {
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, "请求方法不对！");
        }else {
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, "操作失败");
        }
    }


    /**
     * 拦截valid参数校验返回的异常，并转化成基本的返回样式
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public BaseApiResult dealMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("this is controller MethodArgumentNotValidException,param valid failed", e);
        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        String message = allErrors.stream().map(s -> s.getDefaultMessage()).collect(Collectors.joining(";"));
        return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, message);
    }

    /**
     * 拦截valid参数校验返回的异常，并转化成基本的返回样式
     */
    @ExceptionHandler(value = BindException.class)
    public BaseApiResult dealBindException(BindException e) {
        log.error("this is controller MethodArgumentNotValidException,param valid failed", e);

        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        List<String> msgList = fieldErrors.stream()
                .map(o -> o.getDefaultMessage())
                .collect(Collectors.toList());
        String messages = StringUtils.join(msgList.toArray(), ";");

        return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, messages);
    }


}


