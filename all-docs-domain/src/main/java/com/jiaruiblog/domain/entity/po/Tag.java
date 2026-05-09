package com.jiaruiblog.domain.entity.po;

import lombok.Data;

import java.util.Date;

/**
 **/
@Data
public class Tag {

    private String id;

    private String name;

    private String color;

    private Date createDate;

    private Date updateDate;

}