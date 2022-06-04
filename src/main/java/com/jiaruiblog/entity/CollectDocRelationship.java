package com.jiaruiblog.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * @ClassName CollectDocRelationship
 * @Description 用户收藏文档的关系表
 * @Author luojiarui
 * @Date 2022/6/4 10:33 上午
 * @Version 1.0
 **/
@Data
public class CollectDocRelationship {

    @Id
    private Long id;

    private Integer createUser;

    private String content;

    private Date createDate;

    private Date updateDate;

}
