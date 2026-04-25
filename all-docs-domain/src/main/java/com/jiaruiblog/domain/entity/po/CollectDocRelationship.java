package com.jiaruiblog.domain.entity.po;

import com.jiaruiblog.common.enums.RedisActionEnum;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Date;

/**用户收藏文档的关系表
 * @author luojiarui
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