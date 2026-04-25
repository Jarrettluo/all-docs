package com.jiaruiblog.domain.entity;

import com.jiaruiblog.common.enums.RedisActionEnum;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName CollectDocRelationship
 * @Description 用户收藏文档的关系表
 * @author luojiarui
 * @Date 2022/6/4 10:33 上午
 * @Version 1.0
 **/
@Data
public class CollectDocRelationship {

    @Id
    private String id;

    private RedisActionEnum redisActionEnum;

    private String userId;

    private String docId;

    private Date createDate;

    private Date updateDate;

}