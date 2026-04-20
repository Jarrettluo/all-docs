package com.jiaruiblog.domain.entity.vo;

import lombok.Data;

import java.util.Date;

/**
 * @ClassName DocLogVO
 * @Description TODO
 * @author luojiarui
 * @Date 2024/8/17 10:40
 * @Version 1.0
 **/
@Data
public class DocLogVO {

    private String id;

    private String userName;

    private String action;

    private String docName;

    private Date createDate;

}