package com.jiaruiblog.domain.entity.po;

import com.jiaruiblog.common.MessageConstant;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

/**用户针对某一个文档的评论
 * @author luojiarui
 **/
@Data
public class Comment {

    private String id;

    private Long createUser;

    private String userId;

    private String userName;

    @Size(min = 1, max = 140, message = MessageConstant.PARAMS_LENGTH_REQUIRED)
    private String content;

    private String docId;

    private Date createDate;

    private Date updateDate;
}