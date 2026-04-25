package com.jiaruiblog.domain.entity.po;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.Date;

/** 文档日志
 * @author luojiarui
 **/
@Data
@Table(name = "doc_log")
public class DocLog {

    @Id
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
