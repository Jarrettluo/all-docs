package com.jiaruiblog.task.like;

import com.jiaruiblog.enums.RedisActionEnum;
import lombok.Data;

/**
 * @ClassName UserLikeDetail
 * @Description 用户通过redis点赞的信息实体
 * @Author luojiarui
 * @Date 2023/4/5 11:52
 * @Version 1.0
 **/
@Data
public class UserLikeDetail {

    private String id;

    private String entityId;

    private String userId;

    private RedisActionEnum action;

}
