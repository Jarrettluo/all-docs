package com.jiaruiblog.common;

/**
 * @ClassName RegexConstant
 * @Description 正则表达式
 * @Author luojiarui
 * @Date 2022/11/8 23:23
 * @Version 1.0
 **/
public class RegexConstant {

    private RegexConstant() {
        throw new IllegalStateException("RegxConstant class error!");
    }

    public static final String CH_ENG_WORD = "^[\\u4E00-\\u9FA5A-Za-z0-9_-]{1,64}$";

    /**
     * @Author luojiarui
     * @Description 只能是数字，大小字母，下划线组成
     * @Date 22:17 2023/2/14
     * @Param
     * @return
     **/
    public static final String NUM_WORD_REG = "^[A-Za-z0-9_]+$";

}
