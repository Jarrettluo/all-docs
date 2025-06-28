package com.jiaruiblog.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;

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

    @NotBlank(message = "")
    private String name;

    private Date createDate;

    private Date updateDate;

}
