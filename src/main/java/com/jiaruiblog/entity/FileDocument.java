package com.jiaruiblog.entity;

import com.jiaruiblog.enums.DocStateEnum;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * @author jiarui.luo
 */
@Data
@Document
public class FileDocument {

    /**
     * 主键
     */
    @Id
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

    private String previewFileId;

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


}
