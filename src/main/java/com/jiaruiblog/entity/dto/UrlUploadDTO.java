package com.jiaruiblog.entity.dto;

import com.jiaruiblog.common.MessageConstant;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @ClassName UrlUploadDTO
 * @Description 通过url上传的参数
 * @Author luojiarui
 * @Date 2023/4/21 23:49
 * @Version 1.0
 **/
@Data
public class UrlUploadDTO {

    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    @Size(min = 1, max = 512, message = MessageConstant.PARAMS_LENGTH_REQUIRED)
    private String url;

    private String category;

    private List<String> tags;

    private String description;

}
