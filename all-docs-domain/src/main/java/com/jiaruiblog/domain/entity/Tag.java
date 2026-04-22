package com.jiaruiblog.domain.entity;

import com.jiaruiblog.common.MessageConstant;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName Tag
 * @Description Tag
 * @author luojiarui
 * @Date 2022/6/4 10:31 上午
 * @Version 1.0
 **/

@Data
public class Tag {

    @Id
    private String id;

    @NotBlank(message = MessageConstant.PARAMS_IS_NOT_NULL)
    private String name;

    private Date createDate;

    private Date updateDate;

}