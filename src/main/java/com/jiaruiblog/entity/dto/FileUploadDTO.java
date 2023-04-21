package com.jiaruiblog.entity.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @ClassName FileUploadDTO
 * @Description 文件批量上传的参数
 * @Author luojiarui
 * @Date 2023/4/21 23:12
 * @Version 1.0
 **/
@Data
public class FileUploadDTO {

    private MultipartFile[] files;

    private String category;

    private List<String> tags;

    private String description;

    private Boolean skipError;

}

