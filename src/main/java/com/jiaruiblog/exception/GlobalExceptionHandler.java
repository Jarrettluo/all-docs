package com.jiaruiblog.exception;

import com.jiaruiblog.perfect_cv.common.ApiResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResult<Object> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMsg = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return new ApiResult<>(400, errorMsg, null);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handleBusinessException(BusinessException ex, WebRequest request) {
        Map<String, String> response = new HashMap<>();
        response.put("errorCode", ex.getCode().toString());
        response.put("errorMessage", ex.getErrorMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}