package com.jiaruiblog.domain.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * @ClassName DocLog
 * @Description 文档日志
 * @author luojiarui
 * @Date 2022/12/10 10:58
 * @Version 1.0
 **/
@Data
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
