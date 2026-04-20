package com.jiaruiblog.domain.entity.dto;

import com.jiaruiblog.common.MessageConstant;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * @ClassName CollectDTO
 * @Description 收藏的dto
 * @author luojiarui
 * @Date 2022/6/19 5:22 下午
 * @Version 1.0
 **/
@Data
public class CollectDTO {

    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    private String docId;
}