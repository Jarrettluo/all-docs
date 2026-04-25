package com.jiaruiblog.domain.request;

import lombok.Data;

/**
 * @ClassName LikeRequest
 * @Description 点赞请求对象，包含被点赞实体类型和实体ID
 * @author luojiarui
 **/
@Data
public class LikeRequest {
    private int entityType;
    private String entityId;
}