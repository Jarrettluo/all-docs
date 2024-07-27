package com.jiaruiblog.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jiaruiblog.DocumentSharingSiteApplication;
import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.entity.dto.RegistryUserDTO;
import com.jiaruiblog.entity.dto.UserDTO;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.Charset;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DocumentSharingSiteApplication.class)
public class UserControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @After
    public void tearDown(){
    }

    /**
     * @Author luojiarui
     * @Description 参数为空
     * @Date 22:28 2023/3/14
     * @Param []
     **/
    @Rollback
    @Transactional
    @Test
    public void insertObjTest1() throws Exception {
        RegistryUserDTO registryUserDTO = new RegistryUserDTO();
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .post("/user/insert")
                .content(JSON.toJSONString(registryUserDTO))
                .contentType(MediaType.APPLICATION_JSON)
                // 设置返回值类型为utf-8，否则默认为ISO-8859-1
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String result1 = result.getResponse().getContentAsString(Charset.defaultCharset());
        JSONObject jsonObject = JSON.parseObject(result1);
        System.out.println(jsonObject);
        Assert.assertEquals(MessageConstant.PARAMS_ERROR_CODE, jsonObject.get("code"));
        Assert.assertEquals(MessageConstant.PARAMS_IS_NOT_NULL, jsonObject.get("message"));
    }

    /**
     * @Author luojiarui
     * @Description 用户名长度不足
     * @Date 22:29 2023/3/14
     * @Param []
     **/
    @Rollback
    @Transactional
    @Test
    public void insertObjTest2() throws Exception {
        RegistryUserDTO registryUserDTO = new RegistryUserDTO();
        registryUserDTO.setUsername(RandomStringUtils.randomAlphanumeric(1));
        registryUserDTO.setPassword(RandomStringUtils.randomAlphanumeric(32));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .post("/user/insert")
                .content(JSON.toJSONString(registryUserDTO))
                .contentType(MediaType.APPLICATION_JSON)
                // 设置返回值类型为utf-8，否则默认为ISO-8859-1
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String result1 = result.getResponse().getContentAsString(Charset.defaultCharset());
        JSONObject jsonObject = JSON.parseObject(result1);
        System.out.println(jsonObject);
        Assert.assertEquals(MessageConstant.PARAMS_ERROR_CODE, jsonObject.get("code"));
        Assert.assertEquals(MessageConstant.PARAMS_LENGTH_REQUIRED, jsonObject.get("message"));
    }

    /**
     * @Author luojiarui
     * @Description 用户名长度超长
     * @Date 22:33 2023/3/14
     * @Param []
     * @return void
     **/
    @Rollback
    @Transactional
    @Test
    public void insertObjTest3() throws Exception {
        RegistryUserDTO registryUserDTO = new RegistryUserDTO();
        registryUserDTO.setUsername(RandomStringUtils.randomAlphabetic(33));
        registryUserDTO.setPassword(RandomStringUtils.randomAlphabetic(32));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .post("/user/insert")
                .content(JSON.toJSONString(registryUserDTO))
                .contentType(MediaType.APPLICATION_JSON)
                // 设置返回值类型为utf-8，否则默认为ISO-8859-1
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String result1 = result.getResponse().getContentAsString(Charset.defaultCharset());
        JSONObject jsonObject = JSON.parseObject(result1);
        System.out.println(jsonObject);
        Assert.assertEquals(MessageConstant.PARAMS_ERROR_CODE, jsonObject.get("code"));
        Assert.assertEquals(MessageConstant.PARAMS_LENGTH_REQUIRED, jsonObject.get("message"));
    }

    /**
     * @Author luojiarui
     * @Description 密码长度不足
     * @Date 22:29 2023/3/14
     * @Param []
     **/
    @Rollback
    @Transactional
    @Test
    public void insertObjTest4() throws Exception {
        RegistryUserDTO registryUserDTO = new RegistryUserDTO();
        registryUserDTO.setUsername(RandomStringUtils.randomAlphanumeric(6));
        registryUserDTO.setPassword(RandomStringUtils.randomAlphanumeric(1));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .post("/user/insert")
                .content(JSON.toJSONString(registryUserDTO))
                .contentType(MediaType.APPLICATION_JSON)
                // 设置返回值类型为utf-8，否则默认为ISO-8859-1
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String result1 = result.getResponse().getContentAsString(Charset.defaultCharset());
        JSONObject jsonObject = JSON.parseObject(result1);
        System.out.println(jsonObject);
        Assert.assertEquals(MessageConstant.PARAMS_ERROR_CODE, jsonObject.get("code"));
        Assert.assertEquals(MessageConstant.PARAMS_LENGTH_REQUIRED, jsonObject.get("message"));
    }

    /**
     * @Author luojiarui
     * @Description 密码长度超长
     * @Date 22:33 2023/3/14
     * @Param []
     **/
    @Rollback
    @Transactional
    @Test
    public void insertObjTest5() throws Exception {
        RegistryUserDTO registryUserDTO = new RegistryUserDTO();
        registryUserDTO.setUsername(RandomStringUtils.randomAlphabetic(10));
        registryUserDTO.setPassword(RandomStringUtils.randomAlphanumeric(33));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .post("/user/insert")
                .content(JSON.toJSONString(registryUserDTO))
                .contentType(MediaType.APPLICATION_JSON)
                // 设置返回值类型为utf-8，否则默认为ISO-8859-1
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String result1 = result.getResponse().getContentAsString(Charset.defaultCharset());
        JSONObject jsonObject = JSON.parseObject(result1);
        System.out.println(jsonObject);
        Assert.assertEquals(MessageConstant.PARAMS_ERROR_CODE, jsonObject.get("code"));
        Assert.assertEquals(MessageConstant.PARAMS_LENGTH_REQUIRED, jsonObject.get("message"));
    }

    /**
     * @Author luojiarui
     * @Description 密码格式不对
     * @Date 22:33 2023/3/14
     * @Param []
     **/
    @Rollback
    @Transactional
    @Test
    public void insertObjTest6() throws Exception {
        RegistryUserDTO registryUserDTO = new RegistryUserDTO();
        registryUserDTO.setUsername(RandomStringUtils.randomAlphabetic(10));
        registryUserDTO.setPassword(RandomStringUtils.random(5, new char[]{'a','@','#','-','中','f', '1', '2', '3'}));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .post("/user/insert")
                .content(JSON.toJSONString(registryUserDTO))
                .contentType(MediaType.APPLICATION_JSON)
                // 设置返回值类型为utf-8，否则默认为ISO-8859-1
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String result1 = result.getResponse().getContentAsString(Charset.defaultCharset());
        JSONObject jsonObject = JSON.parseObject(result1);
        System.out.println(jsonObject);
        Assert.assertEquals(MessageConstant.PARAMS_ERROR_CODE, jsonObject.get("code"));
        Assert.assertEquals(MessageConstant.PARAMS_FORMAT_ERROR, jsonObject.get("message"));
    }

    @Test
    public void batchInsert() {
    }

    @Test
    public void getById() {
    }

    @Test
    public void getByUsername() {
    }

    @Test
    public void updateUser() {
    }

    @Test
    public void deleteByID() {
    }

    @Test
    public void login() {
    }

    @Test
    public void allUsers() {
    }

    @Test
    public void testLogin() {
    }

    @Test
    public void testCheckUserDTOParams() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId("6662d1f26238ff0284d4c957");
        userDTO.setPhone("123456789098");
        userDTO.setMail("jiarui.luo@163.com");
        userDTO.setMale(false);
        userDTO.setDescription("这是我的个人签名");
        userDTO.setBirthtime(new Date());

        boolean b = UserController.checkUserDTOParams(userDTO);
        Assert.assertEquals(true, b);
    }
}