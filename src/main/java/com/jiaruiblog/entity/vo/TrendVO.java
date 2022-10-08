package com.jiaruiblog.entity.vo;

import lombok.Data;

import java.util.List;

/**
 * @ClassName TrendVO
 * @Description TrendVO
 * @Author luojiarui
 * @Date 2022/6/26 4:24 下午
 * @Version 1.0
 **/
@Data
public class TrendVO {


    private String id;

    private String name;

    private List<DocVO> docList;

}
