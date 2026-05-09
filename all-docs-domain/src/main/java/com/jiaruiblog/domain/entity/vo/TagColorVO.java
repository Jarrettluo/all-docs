package com.jiaruiblog.domain.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @ClassName TagColorVO
 * @Description 标签颜色VO类
 * @author luojiarui
 * @Date 2026/4/27
 * @Version 1.0
 **/
@Schema(name = "TagColorVO", description = "标签颜色VO")
@Data
public class TagColorVO {

    @Schema(description = "标签名称", example = "重要")
    private String name;

    @Schema(description = "标签颜色", example = "red")
    private String color;
}
