package com.jiaruiblog.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @ClassName SystemConfig
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/11/29 23:11
 * @Version 1.0
 **/
@Data
@Component
@ConfigurationProperties(prefix = "all-docs.config")
public class SystemConfig {

    private Boolean userUpload;

    private Boolean adminReview;

    private Boolean prohibitedWord;

    private Boolean userRegistry;

}
