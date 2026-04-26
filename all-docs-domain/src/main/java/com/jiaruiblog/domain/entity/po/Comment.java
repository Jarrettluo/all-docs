package com.jiaruiblog.domain.entity.po;

import lombok.Data;

import java.util.Date;

/**用户针对某一个文档的评论
 * @author luojiarui
 **/
@Data
public class Comment {

    private String id;

    private String createUser;

    private String userId;

    private String userName;

    private String content;

    private String docId;

    private Date createDate;

    private Date updateDate;
}