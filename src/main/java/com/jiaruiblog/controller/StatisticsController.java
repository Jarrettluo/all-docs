package com.jiaruiblog.controller;

import com.jiaruiblog.service.StatisticsService;
import com.jiaruiblog.utils.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName StatisticsController
 * @Description 统计模块
 * @Author luojiarui
 * @Date 2022/6/26 2:24 下午
 * @Version 1.0
 **/
@Api(tags = "统计模块")
@RestController
@Slf4j
@CrossOrigin
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    StatisticsService statisticsService;

    @ApiOperation(value = "4.1 查询热度榜", notes = "查询列表")
    @GetMapping(value = "/trend")
    public ApiResult trend(){
        return statisticsService.trend();
    }

    @ApiOperation(value = "4.2 查询统计数据", notes = "查询列表")
    @GetMapping(value = "/all")
    public ApiResult all(){
        return statisticsService.all();
    }
}
