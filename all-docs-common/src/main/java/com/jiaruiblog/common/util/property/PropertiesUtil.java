package com.jiaruiblog.common.util.property;

import org.springframework.core.env.Environment;

/**
 * @ClassName PropertiesUtil
 * @Description 读取springboot的配置
 * @author luojiarui
 * @Date 2023/3/12 22:47
 * @Version 1.0
 **/
public class PropertiesUtil {

    private static volatile Environment env = null;

    public static void setEnvironment(Environment env) {
        PropertiesUtil.env = env;
    }

    public static String getProperty(String key) {
        return PropertiesUtil.env.getProperty(key);
    }


}