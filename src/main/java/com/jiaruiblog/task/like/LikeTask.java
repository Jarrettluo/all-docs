package com.jiaruiblog.task.like;

import com.jiaruiblog.service.LikeService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import javax.annotation.Resource;

/**
 * @ClassName LikeTask
 * @Description 点赞的定时任务
 * @Author luojiarui
 * @Date 2023/4/3 22:10
 * @Version 1.0
 **/
@Slf4j
public class LikeTask extends QuartzJobBean {

    @Resource
    LikeService likeService;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //将 Redis 里的点赞信息同步到数据库里
        likeService.transLikedFromRedis2DB();
    }
}