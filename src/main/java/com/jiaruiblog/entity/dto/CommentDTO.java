package com.jiaruiblog.entity.dto;

import com.jiaruiblog.common.MessageConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @ClassName CommentDTO
 * @Description 评论的dto
 * @author luojiarui
 * @Date 2022/6/19 5:27 下午
 * @Version 1.0
 **/
@Schema(name = "用户评论信息")
@Data
public class CommentDTO {

    @Schema(description = "评论内容", example = "评论信息长度限制")
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    protected String content;

    @Schema(description = "文档的主键")
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    protected String docId;

}
