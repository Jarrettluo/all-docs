package com.jiaruiblog.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @Author Jarrett Luo
 * @Date 2022/11/29 16:34
 * @Version 1.0
 */
public class ReviewServiceImplTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void addInfo() {
        final ReviewServiceImpl reviewService = new ReviewServiceImpl();
        reviewService.add("罗佳45434354瑞");
        // JSONObject result = (JSONObject) JSON.toJSON(reviewService.query());
        // System.out.println(result);
    }

    @Test
    public void queryInfoTest() {
        final ReviewServiceImpl reviewService = new ReviewServiceImpl();
        JSONObject result = (JSONObject) JSON.toJSON(reviewService.query());
        System.out.println(result);
    }
}