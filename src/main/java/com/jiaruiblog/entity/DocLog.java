package com.jiaruiblog.entity;

import com.jiaruiblog.service.impl.DocLogServiceImpl;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * @ClassName DocLog
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/12/10 10:58
 * @Version 1.0
 **/
@Data
public class DocLog {

    @Id
    private String id;

    private String userId;

    private String userName;

    private DocLogServiceImpl.Action action;

    private String docId;

    private String docName;

    private Date createDate;

    private Date updateDate;
}
