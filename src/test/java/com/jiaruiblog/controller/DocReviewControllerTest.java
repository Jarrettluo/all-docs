package com.jiaruiblog.controller;

import com.jiaruiblog.DocumentSharingSiteApplication;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Objects;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DocumentSharingSiteApplication.class)
public class DocReviewControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void queryDocReviewList() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/document/list")
                // 设置返回值类型为utf-8，否则默认为ISO-8859-1
                .accept(MediaType.APPLICATION_JSON)
                .param("type", "ALL")
                .param("filterWord", "")
                .param("rows", "10")
                .param("categoryId", "1")
                .param("tagid", "1")
                .param("page", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void updateDocReview() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/document/list")
                // 设置返回值类型为utf-8，否则默认为ISO-8859-1
                .accept(MediaType.APPLICATION_JSON)
                .param("type", "ALL")
                .param("filterWord", "")
                .param("rows", "10")
                .param("categoryId", "1")
                .param("tagid", "1")
                .param("page", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void refuse() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/document/list")
                // 设置返回值类型为utf-8，否则默认为ISO-8859-1
                .accept(MediaType.APPLICATION_JSON)
                .param("type", "ALL")
                .param("filterWord", "")
                .param("rows", "10")
                .param("categoryId", "1")
                .param("tagid", "1")
                .param("page", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void refuseBatch() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/document/list")
                // 设置返回值类型为utf-8，否则默认为ISO-8859-1
                .accept(MediaType.APPLICATION_JSON)
                .param("type", "ALL")
                .param("filterWord", "")
                .param("rows", "10")
                .param("categoryId", "1")
                .param("tagid", "1")
                .param("page", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void queryReviewResultList() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/document/list")
                // 设置返回值类型为utf-8，否则默认为ISO-8859-1
                .accept(MediaType.APPLICATION_JSON)
                .param("type", "ALL")
                .param("filterWord", "")
                .param("rows", "10")
                .param("categoryId", "1")
                .param("tagid", "1")
                .param("page", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void removeDocReview() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/document/list")
                // 设置返回值类型为utf-8，否则默认为ISO-8859-1
                .accept(MediaType.APPLICATION_JSON)
                .param("type", "ALL")
                .param("filterWord", "")
                .param("rows", "10")
                .param("categoryId", "1")
                .param("tagid", "1")
                .param("page", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void queryLogList() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/document/list")
                // 设置返回值类型为utf-8，否则默认为ISO-8859-1
                .accept(MediaType.APPLICATION_JSON)
                .param("type", "ALL")
                .param("filterWord", "")
                .param("rows", "10")
                .param("categoryId", "1")
                .param("tagid", "1")
                .param("page", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

    }

    @Test
    public void removeLogTest1() throws Exception {

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get("/comment/queryDocReviewList")
                // 设置返回值类型为utf-8，否则默认为ISO-8859-1
                .accept(MediaType.APPLICATION_JSON)
                .param("type", "ALL")
                .param("filterWord", "")
                .param("rows", "10")
                .param("categoryId", "1")
                .param("tagid", "1")
                .param("page", "1"))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().json("code"))
//                .andExpect(MockMvcResultMatchers.model().attributeExists("data"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        Assert.assertEquals(result.getModelAndView().getModel().get("code"), 200);
        Assert.assertNotNull(Objects.requireNonNull(result.getResponse().getContentAsString()));

    }
}