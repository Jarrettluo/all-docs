package com.jiaruiblog.config;

//import org.elasticsearch.client.RestHighLevelClient;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.elasticsearch.client.ClientConfiguration;
//import org.springframework.data.elasticsearch.client.RestClients;
//import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;


/**
 * @ClassName RestClientConfig
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/6/5 10:28 下午
 * @Version 1.0
 **/
//@Configuration
//public class RestClientConfig extends AbstractElasticsearchConfiguration {
//    @Override
//    @Bean
//    public RestHighLevelClient elasticsearchClient() {
//        final ClientConfiguration clientConfiguration = ClientConfiguration.builder()
//                .connectedTo("localhost:9200")
//                .build();
//        return RestClients.create(clientConfiguration).rest();
//    }

//    @PostConstruct
//    void init() {
//        System.setProperty("es.set.netty.runtime.available.processors", "false");
//    }

//}

public class RestClientConfig {
}