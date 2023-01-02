package com.jiaruiblog.service;

import com.jiaruiblog.DocumentSharingSiteApplication;
import com.jiaruiblog.service.impl.CategoryServiceImpl;
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
    public void list() {
    }

    @Test
    public void addRelationShip() {
    }

    @Test
    public void cancelCategoryRelationship() {
    }

    @Test
    public void testQueryTest() {
        categoryServiceImpl.testQuery();
    }
}