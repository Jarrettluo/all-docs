package com.jiaruiblog.service.impl;

import com.alibaba.fastjson.JSON;
import com.jiaruiblog.DocumentSharingSiteApplication;
import com.jiaruiblog.entity.dto.BasePageDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DocumentSharingSiteApplication.class)
public class CommentServiceImplTest {

    @Autowired
    CommentServiceImpl commentService;

    @Test
    public void queryAllCommentsTest1() {

        BasePageDTO page = new BasePageDTO();
        page.setPage(1);
        page.setRows(11);
        String userId = "636f05ef852f7c3263f71d63";
        String s = JSON.toJSONString(commentService.queryAllComments(page, userId, false));
        System.out.println(s);
    }

    @Test
    public void queryAllCommentsTest2() {

        BasePageDTO page = new BasePageDTO();
        page.setPage(1);
        page.setRows(11);
        String userId = "636f05ef852f7c3263f71d63";
        String s = JSON.toJSONString(commentService.queryAllComments(page, userId, false));
        System.out.println(s);
    }
}