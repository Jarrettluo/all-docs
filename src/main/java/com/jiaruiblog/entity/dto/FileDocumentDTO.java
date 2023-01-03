package com.jiaruiblog.entity.dto;

import com.jiaruiblog.entity.CateDocRelationship;
import com.jiaruiblog.entity.TagDocRelationship;
import com.jiaruiblog.entity.Thumbnail;
import com.jiaruiblog.enums.DocStateEnum;
import lombok.Data;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

/**
 * @ClassName FileDocumentDTO
 * @Description TODO
 * @Author luojiarui
 * @Date 2023/1/1 15:34
 * @Version 1.0
 **/
@Data
public class FileDocumentDTO {

    private ObjectId newId;

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
     * 文件MD5值
     */
    private String md5;

    /**
     * 文件内容
     */
    private byte[] content;

    /**
     * 文件类型
     */
    private String contentType;

    /**
     * 文件后缀名
     */
    private String suffix;

    /**
     * 文件描述
     */
    private String description;

    /**
     * 大文件管理GridFS的ID
     */
    private String gridfsId;

    /**
     * 预览图的GridFS的ID
     */
    private String thumbId;

    /**
     * 文本文件的id
     **/
    private String textFileId;

    /**
     * 缩略图
     **/
    private List<Thumbnail> thumbnailList;

    /**
     * 文档的状态
     **/
    private DocStateEnum docState = DocStateEnum.WAITE;

    /**
     * 文档错误信息
     **/
    private String errorMsg;

    // true 正在审核；false 审核完毕
    private boolean reviewing = true;

    // 违禁词列表
    private List<String> wordList;

    private String userId;

    private String userName;

    private List<CateDocRelationship> abc;

    private List<TagDocRelationship> xyz;

}
