package com.jiaruiblog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;


/**
 * @ClassName DocumentSharingSiteApplication
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/6/2 10:58 下午
 * @Version 1.0
 **/
@ServletComponentScan(basePackages = "com.jiaruiblog.filter")
@SpringBootApplication
public class DocumentSharingSiteApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocumentSharingSiteApplication.class, args);
    }


}
