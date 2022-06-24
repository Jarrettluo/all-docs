package com.jiaruiblog.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * @ClassName TagDocRelationship
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/6/4 10:31 上午
 * @Version 1.0
 **/
@Data
public class TagDocRelationship {

    @Id
    private String id;

    // 分类id
    String tagId;

    // 文件id
    String fileId;

    private Date createDate;

    private Date updateDate;

}
