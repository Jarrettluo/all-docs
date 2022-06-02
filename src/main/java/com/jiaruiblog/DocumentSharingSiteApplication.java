package com.jiaruiblog;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDbFactory;

/**
 * @ClassName DocumentSharingSiteApplication
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/6/2 10:58 下午
 * @Version 1.0
 **/

@SpringBootApplication
public class DocumentSharingSiteApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocumentSharingSiteApplication.class, args);
    }

    @Autowired
    private MongoDbFactory mongoDbFactory;

    @Bean
    public GridFSBucket getGridFSBuckets() {
        MongoDatabase db = mongoDbFactory.getDb();
        return GridFSBuckets.create(db);
    }

}
