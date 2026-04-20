package com.jiaruiblog.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * //@ConditionalOnClass(RedisOperations.class)
 * //@EnableConfigurationProperties(RedisProperties.class)
 *
 * @ClassName RedisConfig
 * @Description RedisConfig
 * @author luojiarui
 * @Date 2022/8/14 17:11
 * @Version 1.0
 **/
@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    @ConditionalOnMissingBean(name = "redisTemplate")
    public RedisTemplate<Object, Object> redisTemplate(
            RedisConnectionFactory redisConnectionFactory) {
        // 创建RedisTemplate<String, Object>对象
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        // key的序列化采用StringRedisSerializer
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        // 配置连接工厂
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean
    @ConditionalOnMissingBean(StringRedisTemplate.class)
    public StringRedisTemplate stringRedisTemplate(
            RedisConnectionFactory redisConnectionFactory) {

        StringRedisTemplate template = new StringRedisTemplate();

        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

}