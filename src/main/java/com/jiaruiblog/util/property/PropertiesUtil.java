package com.jiaruiblog.util.property;

/**
 * @ClassName PropertiesUtil
 * @Description 读取springboot的配置
 * @Author luojiarui
 * @Date 2023/3/12 22:47
 * @Version 1.0
 **/
import org.springframework.core.env.Environment;

// 用户解决一般的工具类需要从属性中获取配置信息
public class PropertiesUtil {

    private static Environment env = null;

    public static void setEnvironment(Environment env) {
        PropertiesUtil.env = env;
    }

    public static String getProperty(String key) {
        return PropertiesUtil.env.getProperty(key);
    }


}
