package com.jiaruiblog.entity.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @ClassName DocWithCateVO
 * @Description 专门用于分类和标签的备选列表
 * @Author luojiarui
 * @Date 2022/11/15 22:24
 * @Version 1.0
 **/
@Data
public class DocWithCateVO {

    /**
     * 是否已经被选中，专门用于分类和标签的备选列表
     **/
    private boolean checked;

    /**
     * 文档的id
     **/
    private String id;

    /**
     * 文档的标题
     **/
    private String title;

    /**
     * 文档的大小
     **/
    private Long size;

    /**
     * 文档的分类属性
     **/
    private CategoryVO categoryVO;

    /**
     * 文档标签列表
     **/
    private List<TagVO> tagVOList;

    /**
     * 创建人
     **/
    private String userName;

    /**
     * 创建时间
     **/
    private Date createTime;

}
