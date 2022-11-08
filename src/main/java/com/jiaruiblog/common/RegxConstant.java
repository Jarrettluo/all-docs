package com.jiaruiblog.common;

/**
 * @ClassName RegxConstant
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/11/8 23:23
 * @Version 1.0
 **/
public class RegxConstant {

    private RegxConstant() {
        throw new IllegalStateException("RegxConstant class error!");
    }

    public static final String CH_ENG_WORD = "^[\\u4E00-\\u9FA5A-Za-z0-9_-]{1,64}$";

}
