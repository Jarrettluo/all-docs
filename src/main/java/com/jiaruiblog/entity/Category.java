package com.jiaruiblog.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * @ClassName Classification
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/6/4 10:28 上午
 * @Version 1.0
 **/
@Document
@Data
public class Category {

    @Id
    private Long id;

    @NotBlank(message = "")
    private String name;

    private Date createDate;

    private Date updateDate;


}
