package com.jiaruiblog.exception;

import com.jiaruiblog.common.ApiResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.http.auth.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;


@ControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    MessageSource messageSource;

    @Autowired
    LocaleResolver localeResolver;

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResult<Void>> handleBusinessException(
            BusinessException ex,
            HttpServletRequest request) {
        Locale locale = localeResolver.resolveLocale(request);
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


    // Validation Exception Handling (combined from both)
    @ResponseBody
    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<Object> handleValidationExceptions(Exception ex) {
        String errorMsg = ex instanceof MethodArgumentNotValidException
                ? ((MethodArgumentNotValidException) ex).getBindingResult().getAllErrors().get(0).getDefaultMessage()
                : ErrorCode.INVALID_PARAM.getLocalizedMessage(messageSource, null);
        return new ApiResult<>(ErrorCode.INVALID_PARAM.getCode(), errorMsg, null);
    }

    // Other common exceptions (from CommonExceptionHandler)
    @ResponseBody
    @ExceptionHandler({
            MaxUploadSizeExceededException.class,
            HttpRequestMethodNotSupportedException.class,
            HttpMessageConversionException.class,
            MissingServletRequestParameterException.class
    })
    public ApiResult<Object> handleCommonExceptions(Exception e, HandlerMethod handlerMethod) {
        if (e instanceof MaxUploadSizeExceededException) {
            return ApiResult.error(ErrorCode.FILE_SIZE_EXCEEDED.getCode(),
                    ErrorCode.FILE_SIZE_EXCEEDED.getLocalizedMessage(messageSource, null));
        } else if (e instanceof HttpRequestMethodNotSupportedException) {
            return ApiResult.error(ErrorCode.INVALID_PARAM.getCode(), e.getMessage());
        } else if (e instanceof HttpMessageConversionException) {
            return ApiResult.error(ErrorCode.INVALID_PARAM.getCode(),
                    ErrorCode.INVALID_PARAM.getLocalizedMessage(messageSource, null));
        } else if (e instanceof MissingServletRequestParameterException) {
            return ApiResult.error(ErrorCode.INVALID_PARAM.getCode(), e.getMessage());
        }
        return ApiResult.error(ErrorCode.PROCESS_ERROR.getCode(),
                ErrorCode.PROCESS_ERROR.getLocalizedMessage(messageSource, null));
    }


    // Authentication Exception (from CommonExceptionHandler)
    @ExceptionHandler(AuthenticationException.class)
    public void handleAuthenticationException(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
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