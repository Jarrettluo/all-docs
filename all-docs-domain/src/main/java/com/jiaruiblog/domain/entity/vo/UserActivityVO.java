package com.jiaruiblog.domain.entity.vo;

import lombok.Data;

@Data
public class UserActivityVO {
    private String month;
    private Long activeUsers;
    private Long totalUsers;
}