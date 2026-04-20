package com.jiaruiblog.config.datasource;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    @Autowired
    private MongoClient mongoClient;

    @Bean(destroyMethod = "close")
    public GridFSBucket getGridFsBucket() {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(this.databaseName);
        return GridFSBuckets.create(mongoDatabase);
    }

}