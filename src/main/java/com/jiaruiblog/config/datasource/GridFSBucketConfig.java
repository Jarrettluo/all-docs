package com.jiaruiblog.config.datasource;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p></p>
 * edit at 2025/7/1 13:42
 *
 * @author Jarrett Luo
 * @version 1.0
 */
@Configuration
public class GridFSBucketConfig {

    @Value("${spring.data.mongodb.host}")
    private String host;

    @Value("${spring.data.mongodb.port}")
    private int port;

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    public MongoClient mongoClient() {
        return MongoClients.create(String.format(
                "mongodb://%s:%d/%s?connectTimeoutMS=10000&socketTimeoutMS=10000",
                host, port, databaseName
        ));
    }

    @Bean(destroyMethod = "")
    public GridFSBucket getGridFsBucket() {
        MongoDatabase mongoDatabase = mongoClient().getDatabase(this.databaseName);
        return GridFSBuckets.create(mongoDatabase);
    }

}
