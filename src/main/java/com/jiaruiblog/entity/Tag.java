package com.jiaruiblog.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * @ClassName Tag
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/6/4 10:31 上午
 * @Version 1.0
 **/

@Data
public class Tag {

    @Id
    private Long id;

    @NotBlank(message = "")
    private String name;

    private Date createDate;

    private Date updateDate;

}
