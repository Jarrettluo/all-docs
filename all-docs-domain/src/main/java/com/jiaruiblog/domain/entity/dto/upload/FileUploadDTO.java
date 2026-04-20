package com.jiaruiblog.domain.entity.dto.upload;

import com.jiaruiblog.common.MessageConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @ClassName FileUploadDTO
 * @Description 文件批量上传的参数
 * @author luojiarui
 * @Date 2023/4/21 23:12
 * @Version 1.0
 **/
@Data
@Schema(name = "FileUploadDTO", description = "用户进行批量上传的DTO")
public class FileUploadDTO {

    @Schema(description = "批量文件上传信息")
    private MultipartFile[] files;

    @Schema(description = "文档的分类信息，如果有则分类的长度限制为64字符，不能有空格和换行符号")
    private String category;

    @Schema(description = "文档的标签信息，如果有则标签的长度限制为64字符，不能有空格和换行符号")
    private List<String> tags;

    @Schema(description = "文档的描述信息")
    private String description;

    @Schema(description = "如果开启为True，则文件上传过程中出错是直接往后进行")
    private Boolean skipError;

}