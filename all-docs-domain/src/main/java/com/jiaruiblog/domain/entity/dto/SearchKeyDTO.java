package com.jiaruiblog.domain.entity.dto;

import com.jiaruiblog.common.MessageConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @ClassName SearchKeyDTO
 * @Description 查询信息
 * @author luojiarui
 * @Date 2023/2/25 11:20
 * @Version 1.0
 **/
@Schema(description = "用户搜索记录")
@Data
public class SearchKeyDTO {

    @Schema(description = "用户主键", required = true)
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    private String userId;

    @Schema(description = "用户搜索字符", required = true)
    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    private String searchWord;

}