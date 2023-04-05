package com.jiaruiblog.entity.dto;

import lombok.Data;

import java.util.Date;

/**
 * @ClassName FileDocumentDTO
 * @Description 多表联查返回结果
 * @Author luojiarui
 * @Date 2023/1/1 15:34
 * @Version 1.0
 **/
@Data
public class FileDocumentDTO {

    private String id;

    /**
     * 文件名称
     */
    private String name;

    /**
     * 文件大小
     */
    private long size;

    /**
     * 上传时间
     */
    private Date uploadDate;

    /**
     * 预览图的GridFS的ID
     */
    private String thumbId;


    // true 正在审核；false 审核完毕
    private boolean reviewing = true;

    private String userId;
}
