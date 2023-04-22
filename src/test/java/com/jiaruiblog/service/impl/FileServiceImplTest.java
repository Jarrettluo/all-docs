package com.jiaruiblog.service.impl;

import com.alibaba.fastjson.JSON;
import com.jiaruiblog.DocumentSharingSiteApplication;
import com.jiaruiblog.entity.FileDocument;
import com.jiaruiblog.entity.dto.DocumentDTO;
import com.jiaruiblog.enums.FilterTypeEnum;
import com.jiaruiblog.service.IFileService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = DocumentSharingSiteApplication.class)
public class FileServiceImplTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Autowired
    IFileService iFileService;

    @Test
    public void listWithCategory() {
        DocumentDTO documentDTO = new DocumentDTO();
        documentDTO.setType(FilterTypeEnum.CATEGORY);
        documentDTO.setFilterWord("");
        documentDTO.setPage(0);
        documentDTO.setRows(20);
        documentDTO.setCategoryId("");
        String s = JSON.toJSONString(iFileService.listWithCategory(documentDTO));
        System.out.println(s);
    }

    @Test
    public void listWithCategoryTest2() {
        DocumentDTO documentDTO = new DocumentDTO();
        documentDTO.setType(FilterTypeEnum.CATEGORY);
        documentDTO.setFilterWord("");
        documentDTO.setPage(0);
        documentDTO.setRows(10);
        documentDTO.setCategoryId("62b6814b77914c7fa8fa959c");
        String s = JSON.toJSONString(iFileService.listWithCategory(documentDTO));
        System.out.println(s);
    }

    @Test
    public void queryByIdTest() {
        String docId = "62b843695f74b25a63f5427b";
        FileDocument fileDocument = iFileService.queryById(docId);
        System.out.println(fileDocument);
    }

    @Test
    public void uploadByUrlTest1() {
        String category = "";
        List<String> tags = new ArrayList<>();
        String name = "";
        String description = "";
        String urlStr = "https://docs.spring.io/spring-framework/docs/1.0.0/license.txt";
        String userId = "";
        String username = "";
        iFileService.uploadByUrl(category, tags, name, description, urlStr, userId, username);


    }
}