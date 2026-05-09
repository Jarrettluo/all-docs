package com.jiaruiblog.domain.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName SearchResultItem
 * @Description 搜索结果项，包含ID和高亮片段
 * @author luojiarui
 * @Date 2026/4/28
 * @Version 1.0
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResultItem {

    /**
     * 文档ID
     */
    private String id;

    /**
     * 高亮片段列表（最多3段，可能是content、name、tagNames或categoryName中的匹配内容）
     */
    private List<String> highlightFragments;

    /**
     * 高亮来源：content/name/tagNames/categoryName
     */
    private String highlightSource;
}