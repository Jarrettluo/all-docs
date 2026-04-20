package com.jiaruiblog.domain.entity.dto;

import com.jiaruiblog.common.MessageConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


/**
 * @ClassName RefuseDTO
 * @Description 拒绝文档的实体类
 * @author luojiarui
 * @Date 2022/12/8 21:02
 * @Version 1.0
 **/
@Schema(name = "拒绝文档的传入参数")
@Data
public class RefuseDTO {

    @Schema(description = "文档id", example = "id长度最小为1最大为64", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = MessageConstant.PARAMS_LENGTH_REQUIRED)
    @Size(min = 1, max = 64, message = MessageConstant.PARAMS_LENGTH_REQUIRED)
    private String docId;

    @Schema(description = "拒绝文档的原因", example = "拒绝原因最小为1，最大为128", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = MessageConstant.PARAMS_LENGTH_REQUIRED)
    @Size(min = 1, max = 128, message = MessageConstant.PARAMS_LENGTH_REQUIRED)
    private String reason;

}