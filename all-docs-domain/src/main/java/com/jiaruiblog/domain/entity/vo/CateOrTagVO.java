package com.jiaruiblog.domain.entity.vo;

import lombok.Data;

/**
 * @ClassName CateOrTagVO
 * @Description 分类或标签视图对象，包含id、名称及关联数量
 * @author luojiarui
 * @Date 2023/5/20 16:28
 * @Version 1.0
 **/
@Data
public class CateOrTagVO {

    private String id;

    private String name;

    private Integer num;

}