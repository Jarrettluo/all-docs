package com.jiaruiblog.domain.entity.po;

import lombok.Data;

import java.util.Date;

/** 文档日志
 * @author luojiarui
 **/
@Data
public class DocLog {

    private String id;

    private String userId;

    private String userName;

    /**
     * 操作类型：VIEW, DOWNLOAD, UPLOAD, DELETE, SHARE
     */
    private String action;

    private String docId;

    private String docName;

    private Date createDate;

    private Date updateDate;
}
