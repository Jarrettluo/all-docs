package com.jiaruiblog.entity.dto.upload;

import com.jiaruiblog.common.MessageConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * @ClassName UrlUploadDTO
 * @Description 通过url上传的参数
 * @author luojiarui
 * @Date 2023/4/21 23:49
 * @Version 1.0
 **/
@Data
@Schema(name = "UrlUploadDTO", description = "用户进行url上传的各类分页数据列表查询的实体")
public class UrlUploadDTO {

    @Schema(description = "文件地址", minLength = 1, maxLength = 512)
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    @Size(min = 1, max = 512, message = MessageConstant.PARAMS_LENGTH_REQUIRED)
    private String url;

    @Schema(description = "文档的名字（当文档的url无法提取出有效的名字时候，则需要手动给名称）")
    private String name;

    @Schema(description = "文档的分类信息，如果有则分类的长度限制为64字符，不能有空格和换行符号")
    private String category;

    @Schema(description = "文档的标签信息，如果有则标签的长度限制为64字符，不能有空格和换行符号")
    private List<String> tags;

    @Schema(description = "文档的描述信息")
    private String description;

}
