package com.jiaruiblog.entity.dto;

import lombok.Data;

@Data
public class PageRequestDTO {
    private int pageNum = 1;     // 默认第1页
    private int pageSize = 10;   // 默认每页10条
}