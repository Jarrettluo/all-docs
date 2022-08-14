package com.jiaruiblog.intercepter;

import org.junit.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.*;

public class SensitiveWordInitTest {

    /**
     * @Author luojiarui
     * @Description 从静态资源中加载敏感词库
     * @Date 16:51 2022/8/14
     * @Param []
     * @return void
     **/
    @Test
    public void initKeyWord() throws IOException {

        Map sensorWordMap = new SensitiveWordInit().initKeyWord();
        System.out.println(sensorWordMap);
    }
}