package com.jiaruiblog.api.exception;

import com.jiaruiblog.common.ApiResult;
import com.jiaruiblog.common.exception.BusinessException;
import com.jiaruiblog.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
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
import java.util.stream.Collectors;


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
        String mainMessage = ex.getErrorCode().getMessage(messageSource, locale,
                ex.getMessageArgs());

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

    @ResponseBody
    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMsg = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return new ApiResult<>(
                ErrorCode.INVALID_PARAM.getCode(),
                "参数校验失败: " + errorMsg,
                null
        );
    }

    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<Object> handleConstraintViolation(ConstraintViolationException ex) {
        String errorMsg = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining("; "));

        return new ApiResult<>(
                ErrorCode.INVALID_PARAM.getCode(),
                "路径参数校验失败: " + errorMsg,
                null
        );
    }

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

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResult<Void>> handleAuthenticationException(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResult.error(403, "Authentication failed"));
    }

    private HttpStatus resolveHttpStatus(ErrorCode errorCode) {
        if (errorCode.getCode() >= 1000) {
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.valueOf(errorCode.getCode());
    }
}
