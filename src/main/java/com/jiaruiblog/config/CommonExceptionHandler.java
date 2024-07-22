package com.jiaruiblog.config;

import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.util.BaseApiResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.AuthenticationException;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.servlet.http.HttpServletResponse;
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
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.FILE_SIZE_ERROR);
        } else if (e instanceof HttpRequestMethodNotSupportedException) {
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.REQUEST_METHOD_ERROR);
        }else {
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
    }


    /**
     * 拦截valid参数校验返回的异常，并转化成基本的返回样式
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public BaseApiResult dealMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        e.printStackTrace();
        return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, "参数异常");
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
        e.printStackTrace();
        return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, "请求异常，请检查");
    }

    /**
     * 拦截valid参数校验返回的异常，并转化成基本的返回样式
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    public BaseApiResult dealConstraintViolationException(ConstraintViolationException e) {
        String message = e.getMessage();
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
        exception.printStackTrace();
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


    /**
     * @Author luojiarui
     * @Description 管理员设置的禁止操作的错误
     * @Date 21:18 2022/12/9
     * @Param [e]
     **/
    @ExceptionHandler(AuthenticationException.class)
    public void dealAuthenticationException(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    /**
     * @Author luojiarui
     * @Description 管理员设置的禁止操作的错误
     * @Date 21:18 2022/12/9
     * @Param [e]
     **/
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public BaseApiResult dealAuthenticationException(MissingServletRequestParameterException e, HandlerMethod handlerMethod) {
        String errorMessage = String.format("MissingServletRequestParameterException（遗漏Servlet请求参数异常）：%s",
                e.getMessage());
        return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, errorMessage);
    }

}


