package com.jiaruiblog.config;

import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.util.BaseApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.validation.ConstraintViolationException;

/**
 * 全局统一异常处理
 * 捕获异常，产生异常时，统一返回错误信息
 *
 * @author jiarui.luo
 */
@Slf4j
@RestControllerAdvice
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
        String defaultMessage = e.getBindingResult().getFieldError().getDefaultMessage();
//        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
//        String message = allErrors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
//                .collect(Collectors.joining(";"));
        return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, defaultMessage);
    }

    /**
     * @Author luojiarui
     * @Description validation 效验post or get 方式表单方式提交转对象，效验出错
     * @Date 21:50 2022/12/8
     * @Param [e]
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    @ExceptionHandler(BindException.class)
    public BaseApiResult handleValidation(BindException e) {
        String defaultMessage = e.getFieldError().getDefaultMessage();
//        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
//        String messages = fieldErrors.stream()
//                .map(DefaultMessageSourceResolvable::getDefaultMessage)
//                .collect(Collectors.joining(";"));
        return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, defaultMessage);
    }

    /**
     * 拦截valid参数校验返回的异常，并转化成基本的返回样式
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    public BaseApiResult dealConstraintViolationException(ConstraintViolationException e) {
        String message = e.getMessage();
//        Set<ConstraintViolation<?>> allErrors = e.getConstraintViolations();
//        String message = allErrors.stream().map(ConstraintViolation::getMessage)
//                .collect(Collectors.joining(";"));
        return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, message);
    }

    /**
     * 参数类型转换错误
     *
     * @param exception 错误
     * @return 错误信息
     */
    @ExceptionHandler(HttpMessageConversionException.class)
    public BaseApiResult parameterTypeException(HttpMessageConversionException exception) {
        return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, "类型转换错误");
    }

    /**
     * @Author luojiarui
     * @Description 请求方法不正确
     * @Date 21:18 2022/12/9
     * @Param [e]
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public BaseApiResult dealHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        String message = e.getMessage();
        return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, message);
    }


}


