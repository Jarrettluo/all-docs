package com.jiaruiblog.entity.dto.upload;

import com.jiaruiblog.common.MessageConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "用户进行url上传的", description = "各类分页数据列表查询的实体")
public class UrlUploadDTO {

    @ApiModelProperty(value = "文件地址", notes = "文件上传信息")
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    @Size(min = 1, max = 512, message = MessageConstant.PARAMS_LENGTH_REQUIRED)
    private String url;

    @ApiModelProperty(value = "文档的名字",notes = "当文档的url无法提取出有效的名字时候，则需要手动给名称")
    private String name;

    @ApiModelProperty(value = "分类", notes = "文档的分类信息，如果有则分类的长度限制为64字符，不能有空格和换行符号")
    private String category;

    @ApiModelProperty(value = "标签", notes = "文档的标签信息，如果有则标签的长度限制为64字符，不能有空格和换行符号")
    private List<String> tags;

    @ApiModelProperty(value = "描述",notes = "文档的描述信息")
    private String description;

}
