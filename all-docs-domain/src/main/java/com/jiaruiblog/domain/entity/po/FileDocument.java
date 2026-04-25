package com.jiaruiblog.domain.entity.po;

import com.jiaruiblog.common.enums.DocStateEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Date;

/**
 * @author jiarui.luo
 */
@Data
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
     * 文件内容
     */
    private byte[] content;

    /**
     * 文档的状态
     **/
    @Enumerated(EnumType.STRING)
    private DocStateEnum docState = DocStateEnum.WAIT;

    /**
     * 文档错误信息
     **/
    private String errorMsg;

    // true 正在审核；false 审核完毕
    private boolean reviewing = true;

    private String userId;

    private String userName;

    private Date createDate;

    private Date updateDate;

}