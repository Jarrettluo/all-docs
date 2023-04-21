package com.jiaruiblog.entity.po;

import lombok.Data;

import java.util.List;

/**
 * @ClassName FileUploadPO
 * @Description 文档上传时指定的分类id和标签id列表
 * @Author luojiarui
 * @Date 2023/4/21 23:46
 * @Version 1.0
 **/
@Data
public class FileUploadPO {

    private String categoryId;

    private List<String> tagIds;

}
