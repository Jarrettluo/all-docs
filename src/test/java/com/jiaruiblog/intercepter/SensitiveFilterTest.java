package com.jiaruiblog.intercepter;

import lombok.extern.slf4j.Slf4j;
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
        int n = filter.CheckSensitiveWord( filterWord,0,1);
        if(n > 0){ //存在非法字符
            log.info("这个人输入了非法字符--> \"{}\",不知道他到底要查什么~",filterWord);
        }

    }

    @Test
    public void getInstanceTest2() throws IOException {

        String filterWord = "这是一个敏感词汇";
        //非法敏感词汇判断
        SensitiveFilter filter = SensitiveFilter.getInstance();
        int n = filter.CheckSensitiveWord( filterWord,0,1);
        if(n > 0){ //存在非法字符
            log.info("这个人输入了非法字符--> \"{}\",不知道他到底要查什么~",filterWord);
        }

    }

    @Test
    public void getSensitiveWord() {
    }

    @Test
    public void checkSensitiveWord() {
    }

    @Test
    public void replaceSensitiveWordTest1() throws IOException {

        String filterWord = "敏感词汇";
        //非法敏感词汇判断
        SensitiveFilter filter = SensitiveFilter.getInstance();
        filterWord = filter.replaceSensitiveWord(filterWord, 1, "*");
        log.info("这个人输入了非法字符--> \"{}\",不知道他到底要查什么~",filterWord);

    }
}