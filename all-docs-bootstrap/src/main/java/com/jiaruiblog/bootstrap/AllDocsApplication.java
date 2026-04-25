package com.jiaruiblog.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * All-Docs Application Bootstrap
 *
 * @author jiarui.luo
 */
@SpringBootApplication
@ComponentScan(basePackages = {
        "com.jiaruiblog.bootstrap",
        "com.jiaruiblog.api",
        "com.jiaruiblog.application",
        "com.jiaruiblog.common",
        "com.jiaruiblog.infrastructure"
})
public class AllDocsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AllDocsApplication.class, args);
    }
}
