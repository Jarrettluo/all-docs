package com.jiaruiblog.entity.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @ClassName DocumentVO
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/6/21 9:03 下午
 * @Version 1.0
 **/
@Data
public class DocumentVO {

    private Long id;

    private String title;

    private String description;

    private Long size;

    private Integer collectNum;

    private Integer commentNum;

    private CategoryVO categoryVO;

    private List<TagVO> tagVOList;

    private String userName;

    private Date createTime;

}
