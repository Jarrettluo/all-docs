package com.jiaruiblog.domain.entity.vo;

import com.jiaruiblog.domain.entity.dto.SearchResultItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName SearchResultVO
 * @Description 搜索结果 VO，包含总数和结果列表
 * @author luojiarui
 * @Date 2026/4/28
 * @Version 1.0
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResultVO {

    /**
     * 搜索结果总数
     */
    private long total;

    /**
     * 搜索结果列表
     */
    private List<SearchResultItem> items;
}