package com.jiaruiblog.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jiaruiblog.DocumentSharingSiteApplication;
import com.jiaruiblog.service.StatisticsService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DocumentSharingSiteApplication.class)
public class StatisticsServiceImplTest {

    @Resource
    StatisticsService statisticsService;

    @Test
    public void getMonthStat() {

        JSONObject jsonObject = (JSONObject) JSON.toJSON(statisticsService.getMonthStat());
        System.out.println(jsonObject);
        Assert.assertEquals(200, jsonObject.get("code"));

    }
}