package com.jiaruiblog.config;

import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.util.BaseApiResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * 全局统一异常处理
 * 捕获异常，产生异常时，统一返回错误信息
 *
 * @author jiarui.luo
 */
@ControllerAdvice
public class CommonExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public BaseApiResult handle(Exception e) {
        e.printStackTrace();
        if (e instanceof MaxUploadSizeExceededException) {
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, "上传的文件超过大小限制");
        } else {
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, "操作失败");
        }
    }

}


