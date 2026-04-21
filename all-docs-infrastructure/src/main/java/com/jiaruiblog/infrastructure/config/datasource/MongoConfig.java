package com.jiaruiblog.config.datasource;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

@Configuration
@Conditional(DataSourceCondition.MongoDBCondition.class)
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.host}")
    private String host;

    @Value("${spring.data.mongodb.port}")
    private int port;

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    @Override
    @Bean(destroyMethod = "close")
    public MongoClient mongoClient() {
        return MongoClients.create(String.format(
                "mongodb://%s:%d/%s?connectTimeoutMS=10000&socketTimeoutMS=10000",
                host, port, databaseName
        ));
    }

//    @Bean(destroyMethod = "")
//    public GridFSBucket getGridFsBucket() {
//        MongoDatabase mongoDatabase = mongoClient().getDatabase(this.getDatabaseName());
//        return GridFSBuckets.create(mongoDatabase);
//    }
}