package com.jiaruiblog.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jiaruiblog.DocumentSharingSiteApplication;
import com.jiaruiblog.service.impl.CategoryServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = DocumentSharingSiteApplication.class)
public class CategoryServiceTest {

    @Resource
    CategoryServiceImpl categoryServiceImpl;

    @Test
    public void insert() {
    }

    @Test
    public void update() {
    }

    @Test
    public void remove() {
    }

    @Test
    public void queryById() {
    }

    @Test
    public void search() {
    }

    @Test
    public void listTest() {
        JSONObject jsonObject = (JSONObject) JSON.toJSON( categoryServiceImpl.list());
        System.out.println(jsonObject);
        Assert.assertEquals(200, jsonObject.get("code"));
    }

    @Test
    public void addRelationShip() {
    }

    @Test
    public void cancelCategoryRelationship() {
    }

    /**
     * @return void
     * @Author luojiarui
     * @Description 无参数查找
     * @Date 21:45 2023/1/3
     * @Param []
     **/
    @Test
    public void testQueryTest() {
        String cateId = "";
        String tagId = "";
        Long pageNum = 0L;
        Long pageSize = 20L;
        String keyword = "";
        JSONObject result = (JSONObject) JSON.toJSON(
                categoryServiceImpl.getDocByTagAndCate(cateId, tagId, keyword, pageNum, pageSize));
        System.out.println(result);
        Assert.assertEquals(200, result.get("code"));
    }


    /**
     * @return void
     * @Author luojiarui
     * @Description 通过标签id进行查找
     * @Date 21:45 2023/1/3
     * @Param []
     **/
    @Test
    public void testQueryTest1() {
        String cateId = "";
        String tagId = "62b68b4fb7859f613263e83d";
        Long pageNum = 0L;
        Long pageSize = 20L;
        String keyword = "";
        JSONObject result = (JSONObject) JSON.toJSON(
                categoryServiceImpl.getDocByTagAndCate(cateId, tagId, keyword, pageNum, pageSize));
        System.out.println(result);
        Assert.assertEquals(200, result.get("code"));
    }

    /**
     * @return void
     * @Author luojiarui
     * @Description 通过分类id进行查找
     * @Date 21:46 2023/1/3
     * @Param []
     **/
    @Test
    public void testQueryTest2() {
        String cateId = "62b68278b7251d2c780e37d7";
        String tagId = "";
        Long pageNum = 0L;
        Long pageSize = 20L;
        String keyword = "";
        JSONObject result = (JSONObject) JSON.toJSON(
                categoryServiceImpl.getDocByTagAndCate(cateId, tagId, keyword, pageNum, pageSize));
        System.out.println(result);
        Assert.assertEquals(200, result.get("code"));
    }

    /**
     * @return void
     * @Author luojiarui
     * @Description 联合查找
     * @Date 21:46 2023/1/3
     * @Param []
     **/
    @Test
    public void testQueryTest3() {
        String cateId = "62b6814377914c7fa8fa959b";
        String tagId = "636f52d21d19a36d975850ad";
        Long pageNum = 0L;
        Long pageSize = 20L;
        String keyword = "";
        JSONObject result = (JSONObject) JSON.toJSON(
                categoryServiceImpl.getDocByTagAndCate(cateId, tagId, keyword, pageNum, pageSize));
        System.out.println(result);
        Assert.assertEquals(200, result.get("code"));
    }

    /**
     * @Author luojiarui
     * @Description 联合查找
     * @Date 21:46 2023/1/3
     * @Param []
     **/
    @Test
    public void testQueryTest4() {
        String cateId = "62b6814377914c7fa8fa959b";
        String tagId = "636f52d21d19a36d975850ad";
        Long pageNum = 0L;
        Long pageSize = 20L;
        String keyword = "白皮书";
        JSONObject result = (JSONObject) JSON.toJSON(
                categoryServiceImpl.getDocByTagAndCate(cateId, tagId, keyword, pageNum, pageSize));
        System.out.println(result);
        Assert.assertEquals(200, result.get("code"));
    }

    /**
     * @Author luojiarui
     * @Description 联合查找
     * @Date 21:46 2023/1/3
     * @Param []
     **/
    @Test
    public void testQueryTest5() {
        String cateId = "";
        String tagId = "";
        Long pageNum = 0L;
        Long pageSize = 20L;
        String keyword = "白皮书";
        JSONObject result = (JSONObject) JSON.toJSON(
                categoryServiceImpl.getDocByTagAndCate(cateId, tagId, keyword, pageNum, pageSize));
        System.out.println(result);
        Assert.assertEquals(200, result.get("code"));
    }
}