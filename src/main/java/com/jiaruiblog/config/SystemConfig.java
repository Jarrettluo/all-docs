package com.jiaruiblog.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @ClassName SystemConfig
 * @Description 全文档系统的设置，由管理员进行配置
 * @Author luojiarui
 * @Date 2022/11/29 23:11
 * @Version 1.0
 **/
@Data
@Component
@ConfigurationProperties(prefix = "all-docs.config")
public class SystemConfig {

    private Boolean userUpload;

    private Boolean adminReview = true;

    private Boolean prohibitedWord;

    private Boolean userRegistry;

    private String initialUsername;

    private String initialPassword;

    private Boolean coverAdmin;


}
