package com.jiaruiblog.entity.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PageVO<T> {

    private long total;        // 总记录数

    private int pageNum;       // 当前页码

    private int pageSize;      // 每页大小

    private List<T> list;      // 当前页数据列表

}