package com.jiaruiblog.exception;

import com.jiaruiblog.common.ApiResult;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Locale;


@ControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    MessageSource messageSource;

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResult<Object> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMsg = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return new ApiResult<>(400, errorMsg, null);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResult<Void>> handleBusinessException(
            BusinessException ex,
            HttpServletRequest request) {
        Locale locale = request.getLocale();

        // 直接使用 ErrorCode 的缓存能力
        String mainMessage = ex.getErrorCode().getMessage(messageSource, locale,
                ex.getMessageArgs());
        // 拼接主消息和详情消息
        String fullMessage = ex.getDetailMessage() != null
                ? mainMessage + " (" + ex.getDetailMessage() + ")"
                : mainMessage;

        ApiResult<Void> result = new ApiResult<>(
                ex.getErrorCode().getCode(),
                fullMessage,
                null
        );

        return ResponseEntity
                .status(resolveHttpStatus(ex.getErrorCode()))
                .body(result);
    }

    private HttpStatus resolveHttpStatus(ErrorCode errorCode) {
        // 自定义业务错误码(≥1000)统一映射为400
        if (errorCode.getCode() >= 1000) {
            return HttpStatus.BAD_REQUEST;
        }
        // HTTP状态码直接映射
        return HttpStatus.valueOf(errorCode.getCode());
    }
}