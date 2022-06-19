package com.jiaruiblog.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @ClassName CateDocRelationship
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/6/4 10:30 上午
 * @Version 1.0
 **/
@Document
@Data
public class CateDocRelationship {

    // 主键ID
    @Id
    Long id;

    // 分类id
    Long categoryId;

    // 文件id
    Long fileId;

    // 创建时间
    Date createDate;

    // 修改时间
    Date updateDate;

}
