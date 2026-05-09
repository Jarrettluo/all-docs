package com.jiaruiblog.domain.entity.dto;

import com.jiaruiblog.common.MessageConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * @ClassName CommentListDTO
 * @Description CommentListDTO
 * @author luojiarui
 * @Date 2022/9/4 11:45
 * @Version 1.0
 **/
@Schema(name = "根据文档信息查询所属的文档评论")
@Data
@EqualsAndHashCode(callSuper = false)
public class CommentListDTO extends BasePageDTO {

    @Schema(description = "文档主键", required = true)
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    private String docId;

}