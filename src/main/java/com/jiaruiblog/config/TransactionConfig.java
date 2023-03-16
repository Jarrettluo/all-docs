package com.jiaruiblog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;

/**
 * @ClassName TransactionConfig
 * @Description 添加配置类，开启MongoDb事务
 * doc https://docs.spring.io/spring-data/mongodb/docs/current
 * /api/org/springframework/data/mongodb/MongoDatabaseFactory.html
 * @Author luojiarui
 * @Date 2023/3/14 22:12
 * @Version 1.0
 **/
@Configuration
public class TransactionConfig {
    @Bean
    MongoTransactionManager transactionManager(MongoDatabaseFactory factory){
        return new MongoTransactionManager(factory);
    }
}