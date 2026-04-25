package com.jiaruiblog.domain.entity.vo;

import lombok.Data;

/**
 * @ClassName LikeVO
 * @Description 点赞视图对象
 * @author Jarrett
 * @Date 2024/11/9
 * @Version 1.0
 **/
@Data
public class LikeVO {

    private long likeCount;

    private int likeStatus;

    private long collectCount;

    private int collectStatus;
}