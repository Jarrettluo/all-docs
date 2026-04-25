package com.jiaruiblog.domain.entity.po;

import lombok.Data;

import java.util.Date;

/**
 * @ClassName CateDocRelationship
 * @Description CateDocRelationship
 * @author luojiarui
 * @Date 2022/6/4 10:30 上午
 * @Version 1.0
 **/
@Data
public class CateDocRelationship {

    /**
     * 主键ID
     */
    String id;

    /**
     * 分类id
     */
    String categoryId;

    /**
     * 文件id
     */
    String fileId;

    /**
     * 创建时间
     */
    Date createDate;

    /**
     * 修改时间
     */
    Date updateDate;

}