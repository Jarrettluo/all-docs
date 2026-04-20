package com.jiaruiblog.domain.entity;

import lombok.Data;

@Data
public class LikeRequest {
    private int entityType;
    private String entityId;
}