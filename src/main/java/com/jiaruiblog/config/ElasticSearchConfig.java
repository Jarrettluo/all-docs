package com.jiaruiblog.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @ClassName ElasticSearchConfig
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/7/12 10:50 下午
 * @Version 1.0
 **/
@Component
public class ElasticSearchConfig {

    @Value("${cloud.elasticsearch.host}")
    private String esHost;

    @Value("${cloud.elasticsearch.port}")
    private int esPort;

    @Bean
    public RestHighLevelClient restClient() {
        RestHighLevelClient restClient = new RestHighLevelClient(RestClient.builder(new HttpHost(esHost, esPort)));
        return restClient;
    }
}
