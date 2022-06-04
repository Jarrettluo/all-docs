package com.jiaruiblog.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * @ClassName CateDocRelationship
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/6/4 10:30 上午
 * @Version 1.0
 **/
@Data
public class CateDocRelationship {

    @Id
    private Long id;

    private Integer createUser;

    private String content;

    private Date createDate;

    private Date updateDate;

}
