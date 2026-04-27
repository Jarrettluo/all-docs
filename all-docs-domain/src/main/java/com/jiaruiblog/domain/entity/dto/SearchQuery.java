package com.jiaruiblog.domain.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @ClassName SearchQuery
 * @Description 文档搜索请求参数类
 * @author luojiarui
 * @Date 2026/4/27
 * @Version 1.0
 **/
@Schema(name = "SearchQuery", description = "文档搜索请求参数")
@Data
public class SearchQuery {

    @Schema(description = "搜索关键词，默认空字符串", example = "")
    private String keyword = "";

    @Schema(description = "是否全文检索", example = "false")
    private Boolean fullText = false;

    @Schema(description = "是否分词", example = "false")
    private Boolean segment = false;

    @Schema(description = "搜索类型：all/name/description", example = "all")
    private String searchType = "all";

    @Schema(description = "标签筛选数组", example = "[\"pdf\", \"docx\"]")
    private List<String> tags;

    @Schema(description = "分类名称筛选", example = "")
    private String category = "";

    @Schema(description = "排序字段：name/size/type/category/createTime", example = "createTime")
    private String sortField = "createTime";

    @Schema(description = "排序方向：asc/desc", example = "desc")
    private String sortOrder = "desc";

    @Schema(description = "页码，从1开始", example = "1")
    private Integer page = 1;

    @Schema(description = "每页条数", example = "20")
    private Integer pageSize = 20;
}
