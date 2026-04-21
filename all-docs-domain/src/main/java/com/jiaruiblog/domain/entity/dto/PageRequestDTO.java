package com.jiaruiblog.domain.entity.dto;

import lombok.Data;

/**
 * @ClassName PageRequestDTO
 * @Description 分页请求数据传输对象
 * @author luojiarui
 **/
@Data
public class PageRequestDTO {
    private int pageNum = 1;     // 默认第1页
    private int pageSize = 10;   // 默认每页10条
}