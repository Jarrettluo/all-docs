package com.jiaruiblog.controller;

import com.alibaba.fastjson.JSON;
import com.jiaruiblog.DocumentSharingSiteApplication;
import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.config.SystemConfig;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import java.nio.charset.Charset;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DocumentSharingSiteApplication.class)
public class SystemConfigControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Resource
    SystemConfig systemConfig;

    /**
     * @Author luojiarui
     * @Description 查询系统设置信息
     * @Date 22:12 2023/1/27
     * @Param []
     * @return void
     **/
    @Test
    public void getSystemConfig() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get("/system/getConfig")
                .contentType(MediaType.APPLICATION_JSON)
                // 设置返回值类型为utf-8，否则默认为ISO-8859-1
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String result1 = result.getResponse().getContentAsString(Charset.defaultCharset());
        Assert.assertEquals(200, JSON.parseObject(result1).get("code"));
        Assert.assertEquals(systemConfig, JSON.parseObject(result1).getObject("data", SystemConfig.class));
    }

    /**
     * @Author luojiarui
     * @Description 自定义设置用户配置
     * @Date 22:12 2023/1/27
     * @Param []
     * @return void
     **/
    @Test
    public void systemConfigTest1() throws Exception {
        SystemConfig systemConfigParam = new SystemConfig();
        systemConfigParam.setAdminReview(true);
        systemConfigParam.setProhibitedWord(true);
        systemConfigParam.setUserRegistry(false);
        systemConfigParam.setUserUpload(true);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .put("/system/updateConfig")
                .content(JSON.toJSONString(systemConfigParam))
                .contentType(MediaType.APPLICATION_JSON)
                // 设置返回值类型为utf-8，否则默认为ISO-8859-1
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String result1 = result.getResponse().getContentAsString(Charset.defaultCharset());
        Assert.assertEquals(200, JSON.parseObject(result1).get("code"));
        Assert.assertEquals(systemConfigParam, systemConfig);
    }

    /**
     * @Author luojiarui
     * @Description 提交用户设置，反例
     * @Date 22:12 2023/1/27
     * @Param []
     * @return void
     **/
    @Test
    public void systemConfigTest2() throws Exception {
        SystemConfig systemConfigParam = new SystemConfig();
        systemConfigParam.setAdminReview(true);
        systemConfigParam.setProhibitedWord(true);
        systemConfigParam.setUserRegistry(true);
        systemConfigParam.setUserUpload(null);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .put("/system/updateConfig")
                .content(JSON.toJSONString(systemConfigParam))
                .contentType(MediaType.APPLICATION_JSON)
                // 设置返回值类型为utf-8，否则默认为ISO-8859-1
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String result1 = result.getResponse().getContentAsString(Charset.defaultCharset());
        Assert.assertEquals(MessageConstant.PARAMS_ERROR_CODE, JSON.parseObject(result1).get("code"));
        Assert.assertEquals(MessageConstant.PARAMS_FORMAT_ERROR, JSON.parseObject(result1).get("message"));
    }

}