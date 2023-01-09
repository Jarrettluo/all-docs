package com.jiaruiblog.entity.dto;

import com.jiaruiblog.entity.FileDocument;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @ClassName CommentWithUserDTO
 * @Description TODO
 * @Author luojiarui
 * @Date 2023/1/8 22:58
 * @Version 1.0
 **/
@Data
public class CommentWithUserDTO extends CommentDTO {

    private String id;

    private String userName;

    private Date createDate;

    private String userId;

    private List<FileDocument> abc;

}
