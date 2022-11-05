package com.jiaruiblog.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName MongoConfig
 * @Description mongodb 配置
 * @Author luojiarui
 * @Date 2022/7/12 10:30 下午
 * @Version 1.0
 **/
@Configuration
public class MongoConfig {

    /**
     * 获取配置文件中数据库信息
     */
    @Value("${spring.data.mongodb.database}")
    String db;

    /**
     * GridFSBucket用于打开下载流
     *
     * @param mongoClient MongoClient
     * @return GridFSBucket
     */
    @Bean
    public GridFSBucket getGridFsBucket(MongoClient mongoClient) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(db);
        return GridFSBuckets.create(mongoDatabase);
    }

}
