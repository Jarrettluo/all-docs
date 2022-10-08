package com.jiaruiblog.intercepter;

import lombok.extern.slf4j.Slf4j;
import org.ansj.splitWord.analysis.IndexAnalysis;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

@Slf4j
public class SensitiveFilterTest {

    @Test
    public void getInstance() throws IOException {

        String filterWord = "敏感词汇";
        //非法敏感词汇判断
        SensitiveFilter filter = SensitiveFilter.getInstance();
        int n = filter.checkSensitiveWord( filterWord,0,1);
        if(n > 0){ //存在非法字符
            log.info("这个人输入了非法字符--> \"{}\",不知道他到底要查什么~",filterWord);
        }

    }

    @Test
    public void getInstanceTest2() throws IOException {

        String filterWord = "这是一个敏感词汇";
        //非法敏感词汇判断
        SensitiveFilter filter = SensitiveFilter.getInstance();
        int n = filter.checkSensitiveWord( filterWord,0,1);
        if(n > 0){ //存在非法字符
            log.info("这个人输入了非法字符--> \"{}\",不知道他到底要查什么~",filterWord);
        }

    }

    @Test
    public void getSensitiveWord() {
        assertEquals(1, 0);
    }

    @Test
    public void checkSensitiveWord() {
        assertEquals(1, 0);
    }

    @Test
    public void replaceSensitiveWordTest1() throws IOException {

        String filterWord = "敏感词汇";
        //非法敏感词汇判断
        SensitiveFilter filter = SensitiveFilter.getInstance();
        filterWord = filter.replaceSensitiveWord(filterWord, 1, "*");
        log.info("这个人输入了非法字符--> \"{}\",不知道他到底要查什么~",filterWord);
        assertEquals(1, 0);
    }

    /**
     * 测试使用Ansj 的索引分词
     * ansjseg的官方文档： https://github.com/NLPchina/ansj_seg
     */
    @Test
    public void wordSegmentTest1() {
        long startTime = System.currentTimeMillis();
        String text = "20个左边的卡罗拉倒车镜! ";
        String analysisedText = IndexAnalysis.parse(text).toStringWithOutNature();
        long endTime = System.currentTimeMillis();
        long time = endTime - startTime;
        System.out.println("面向索引的分词: " + analysisedText + "(" + time + "ms)");

        assertEquals(1, 0);
    }
    /**
     * 测试使用Ansj 的索引分词
     * ansjseg的官方文档： https://github.com/NLPchina/ansj_seg
     */
    @Test
    public void wordSegmentTest2() {
        long startTime = System.currentTimeMillis();
        String text = "我这句话里面包含了敏感信息! ";
        String analysisedText = IndexAnalysis.parse(text).toStringWithOutNature();
        long endTime = System.currentTimeMillis();
        long time = endTime - startTime;
        System.out.println("面向索引的分词: " + analysisedText + "(" + time + "ms)");

        assertEquals(1, 0);
    }

    /**
     * 测试使用Ansj 的索引分词
     * ansjseg的官方文档： https://github.com/NLPchina/ansj_seg
     */
    @Test
    public void wordSegmentTest3() {
        long startTime = System.currentTimeMillis();
        String text = "电子 单晶 张开盛! ";
        String analysisedText = IndexAnalysis.parse(text).toStringWithOutNature();
        long endTime = System.currentTimeMillis();
        long time = endTime - startTime;
        System.out.println("面向索引的分词: " + analysisedText + "(" + time + "ms)");

        assertEquals(1, 0);
    }

}