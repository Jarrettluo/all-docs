package com.jiaruiblog.entity.dto.upload;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "用户进行批量上传的", description = "各类分页数据列表查询的实体")
public class FileUploadDTO {

    @ApiModelProperty(value = "文件", notes = "批量文件上传信息")
    private MultipartFile[] files;

    @ApiModelProperty(value = "分类", notes = "文档的分类信息，如果有则分类的长度限制为64字符，不能有空格和换行符号")
    private String category;

    @ApiModelProperty(value = "标签", notes = "文档的标签信息，如果有则标签的长度限制为64字符，不能有空格和换行符号")
    private List<String> tags;

    @ApiModelProperty(value = "描述",notes = "文档的描述信息")
    private String description;

    @ApiModelProperty(value = "跳过错误", notes = "如果开启为True，则文件上传过程中出错是直接往后进行")
    private Boolean skipError;

}

