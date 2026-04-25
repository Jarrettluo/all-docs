package com.jiaruiblog.domain.entity.po;

import lombok.Data;

import java.util.Date;

/**
 * @ClassName TagDocRelationship
 * @Description TagDocRelationship
 * @author luojiarui
 * @Date 2022/6/4 10:31 上午
 * @Version 1.0
 **/
@Data
public class TagDocRelationship {

    /**
     * id
     */
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