package com.jiaruiblog.entity.dto;

import lombok.Data;

/**
 * @ClassName QueryDocByTagCateDTO
 * @Description TODO
 * @Author luojiarui
 * @Date 2023/1/6 21:54
 * @Version 1.0
 **/
@Data
public class QueryDocByTagCateDTO extends BasePageDTO{

    String cateId;

    String tagId;

    String keyword;

}
