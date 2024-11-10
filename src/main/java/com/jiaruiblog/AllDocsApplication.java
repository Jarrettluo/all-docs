package com.jiaruiblog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;


/**
 * <p>DocumentSharingSiteApplication</p>
 * @author luojiarui
 **/
@EnableAsync(proxyTargetClass=true)
@ServletComponentScan(basePackages = "com.jiaruiblog.filter")
@SpringBootApplication
public class AllDocsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AllDocsApplication.class, args);
    }

}