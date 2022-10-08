package com.jiaruiblog.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * @ClassName TagDocRelationship
 * @Description TagDocRelationship
 * @Author luojiarui
 * @Date 2022/6/4 10:31 上午
 * @Version 1.0
 **/
@Data
public class TagDocRelationship {

    /**
     * id
     */
    @Id
    private String id;

    /**
     * 分类id
     */
    private String tagId;

    /**
     * 文件id
     */
    private String fileId;

    /**
     * create date
     */
    private Date createDate;

    /**
     * update date
     */
    private Date updateDate;

}
