package com.jiaruiblog.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName ElasticSearchConfig
 * @Description ES配置信息，支持账号密码认证
 * @author luojiarui
 * @Date 2022/7/12 10:50 下午
 * @Version 1.0
 **/
@Slf4j
@Configuration
public class ElasticSearchConfig {

    @Value("${cloud.elasticsearch.host:192.168.1.29}")
    private String esHost;

    @Value("${cloud.elasticsearch.port:1200}")
    private int esPort;

    @Value("${cloud.elasticsearch.username:elastic}")
    private String esUsername;

    @Value("${cloud.elasticsearch.password:infini_rag_flow}")
    private String esPassword;

    @Bean
    public RestClient restClient() {
        log.info("[ES Config] Creating RestClient: host={}, port={}, username={}", esHost, esPort, esUsername);
        RestClientBuilder builder = RestClient.builder(new HttpHost(esHost, esPort));
        if (esUsername != null && !esUsername.isEmpty()) {
            log.info("[ES Config] Applying basic auth for user: {}", esUsername);
            BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(esUsername, esPassword));
            builder.setHttpClientConfigCallback(httpClientBuilder ->
                    httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
        }
        return builder.build();
    }
}
