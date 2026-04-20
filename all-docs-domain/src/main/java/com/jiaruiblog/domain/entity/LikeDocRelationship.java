package com.jiaruiblog.domain.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 点赞/收藏关系实体
 *
 * @author Jarrett
 * @Date 2024/11/9
 * @Version 1.0
 **/
@Data
@Document(collection = "like_relationships")
public class LikeDocRelationship {

    @Id
    private String id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 实体类型：0-文档点赞，1-文档收藏
     */
    private Integer entityType;

    /**
     * 实体ID（文档ID）
     */
    private String entityId;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 实体类型常量
     */
    public static final int TYPE_LIKE = 0;
    public static final int TYPE_COLLECT = 1;
}
