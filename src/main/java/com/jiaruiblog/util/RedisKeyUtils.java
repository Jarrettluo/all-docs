package com.jiaruiblog.util;

/**
 * @ClassName RedisKeyUtils
 * @Description RedisKeyUtils
 * @Author luojiarui
 * @Date 2022/8/14 17:13
 * @Version 1.0
 **/
public class RedisKeyUtils {

    private RedisKeyUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String getSearchHistoryKey(String userid) {
        return userid;
    }



}
