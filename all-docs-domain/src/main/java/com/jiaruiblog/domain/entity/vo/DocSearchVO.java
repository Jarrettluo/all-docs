package com.jiaruiblog.domain.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @ClassName DocSearchVO
 * @Description 文档搜索响应VO类
 * @author luojiarui
 * @Date 2026/4/27
 * @Version 1.0
 **/
@Schema(name = "DocSearchVO", description = "文档搜索响应VO")
@Data
public class DocSearchVO {

    @Schema(description = "文档ID")
    private String id;

    @Schema(description = "文档名称（带后缀）", example = "项目需求文档.pdf")
    private String name;

    @Schema(description = "文档类型（pdf/docx/xlsx/pptx/image/zip）", example = "pdf")
    private String type;

    @Schema(description = "文档大小（字节）", example = "2048576")
    private Long size;

    @Schema(description = "文档大小（格式化，如 \"2 MB\"）", example = "2 MB")
    private String sizeDisplay;

    @Schema(description = "文档描述", example = "2024年Q1项目需求文档")
    private String description;

    @Schema(description = "所属分类名称", example = "需求文档")
    private String category;

    @Schema(description = "标签列表")
    private List<TagColorVO> tags;

    @Schema(description = "当前用户是否已点赞", example = "false")
    private Boolean liked;

    @Schema(description = "当前用户是否已收藏", example = "true")
    private Boolean collected;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新时间")
    private Date updateTime;
}
