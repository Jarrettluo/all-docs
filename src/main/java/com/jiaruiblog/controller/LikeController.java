package com.jiaruiblog.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.service.LikeService;
import com.jiaruiblog.util.BaseApiResult;
import io.lettuce.core.RedisConnectionException;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName LikeController
 * @Description TODO
 * @Author luojiarui
 * @Date 2023/2/2 22:05
 * @Version 1.0
 * 点赞功能参考地址：https://blog.csdn.net/qq_45243783/article/details/128221372
 * redis的数据和数据库的数据保持同步的方案： https://blog.csdn.net/qq_22343483/article/details/103304826
 **/
@Api(tags = "统计模块")
@RestController
@Slf4j
@CrossOrigin
@RequestMapping("/")
public class LikeController{

    @Autowired
    private LikeService likeService;

//    @Autowired
//    private HostHolder hostHolder;

//    @Autowired
//    private EventProducer eventProducer;

    @Autowired
    private RedisTemplate redisTemplate;


    // // TODO: 2023/2/3 对评论进行点赞操作，以后再做了
    // 点赞的信息越积越多怎么统计呢？

    // entityType: 1:点赞
    // entityType: 2:收藏
    @PostMapping("like")
    public BaseApiResult like(@RequestParam("entityType") int entityType,
                              @RequestParam("entityId") String entityId,
                              HttpServletRequest request) {
        if (!StpUtil.hasPermission("like.insert")) {
            return BaseApiResult.error(MessageConstant.AUTH_ERROR_CODE, MessageConstant.NOT_PERMISSION);
        }
        if (entityType != 1 && entityType != 2) {
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_FORMAT_ERROR);
        }
        // 获取到当前用户
        String userId = (String) request.getAttribute("id");
        long likeCount = 0;
        int likeStatus = 0;
        try {
            // 点赞
            likeService.like(userId, entityType, entityId);
            // 获取点赞的数量
            likeCount = likeService.findEntityLikeCount(entityType, entityId);
            // 获取当前用户点赞的状态
            likeStatus = likeService.findEntityLikeStatus(userId, entityType, entityId);
        } catch (RedisConnectionFailureException | RedisConnectionException e) {
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, e.getMessage());
        }


        // 返回的结果，封装成一个Map集合
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);

//        //触发点赞事件
//        if (likeStatus == 1) {
//            Event event = new Event().setTopic(TOPIC_LIKE)
//                    .setEntityId(entityId)
//                    .setEntityType(entityType)
//                    .setUserId(hostHolder.getUser().getId())
//                    .setEntityUserId(entityUserId)
//                    .setData("postId", postId);
//            eventProducer.fireEvent(event);
//        }
//        if (entityType == ENTITY_TYPE_POST) {
//
//            // 计算帖子分数
//            String redisKey = RedisKeyUtil.getPostScoreKey();
//            redisTemplate.opsForSet().add(redisKey, postId);
//        }
        return BaseApiResult.success(map);
    }

    @GetMapping("queryLikeInfo")
    public BaseApiResult queryLikeInfo(@RequestParam("entityId") String entityId,
                                       HttpServletRequest request) {
        if (!StpUtil.hasPermission("like.query")) {
            return BaseApiResult.error(MessageConstant.AUTH_ERROR_CODE, MessageConstant.NOT_PERMISSION);
        }
        // 获取到当前用户
        String userId = (String) request.getAttribute("id");
        // 返回的结果，封装成一个Map集合
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", 0);
        map.put("likeStatus", 0);
        map.put("collectCount", 0);
        map.put("collectStatus", 0);

        try {
            // 获取点赞的数量
            long likeCount = likeService.findEntityLikeCount(1, entityId);
            map.put("likeCount", likeCount);
            // 获取当前用户点赞的状态
            int likeStatus = likeService.findEntityLikeStatus(userId, 1, entityId);
            map.put("likeStatus", likeStatus);

            // 获取收藏的数量
            long collectCount = likeService.findEntityLikeCount(2, entityId);
            map.put("collectCount", collectCount);
            // 获取当前用户收藏的状态
            int collectStatus = likeService.findEntityLikeStatus(userId, 2, entityId);
            map.put("collectStatus", collectStatus);

        } catch (RedisConnectionFailureException | RedisConnectionException e) {
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, e.getMessage());
        }

        return BaseApiResult.success(map);

    }
}
