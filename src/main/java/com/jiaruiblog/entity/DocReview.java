package com.jiaruiblog.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * @Author Jarrett Luo
 * @Date 2022/11/25 15:39
 * @Version 1.0
 */
@Data
public class DocReview {

    /**
     * 主键
     */
    @Id
    private String id;

    /**
     * 文档id
     */
    private String docId;

    /**
     * 文档名称
     */
    private String docName;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 评审是否通过的状态
     */
    private boolean checkState;

    /**
     * 用户是否已读的状态
     */
    private boolean readState;

    /**
     * 用户是否删除的状态
     */
    private boolean userRemove;

    /**
     * 管理员删除评审意见
     **/
    private boolean adminRemove;

    /**
     * 评审意见
     */
    private String reviewLog;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 更新时间
     */
    private Date updateDate;
}
