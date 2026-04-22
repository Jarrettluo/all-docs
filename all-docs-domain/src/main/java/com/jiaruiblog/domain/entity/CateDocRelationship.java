package com.jiaruiblog.domain.entity;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName CateDocRelationship
 * @Description CateDocRelationship
 * @author luojiarui
 * @Date 2022/6/4 10:30 上午
 * @Version 1.0
 **/
@Table(name = "cate_doc_relationship")
@Data
public class CateDocRelationship {

    /**
     * 主键ID
     */
    @Id
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