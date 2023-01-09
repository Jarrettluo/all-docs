package com.jiaruiblog.entity.vo;

import com.jiaruiblog.entity.dto.CommentDTO;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName CommentWithUserVO
 * @Description TODO
 * @Author luojiarui
 * @Date 2023/1/9 22:19
 * @Version 1.0
 **/
@Data
public class CommentWithUserVO extends CommentDTO {

    private String id;

    private String userName;

    private Date createDate;

    private String userId;

    private String docId;

    private String docName;
}
