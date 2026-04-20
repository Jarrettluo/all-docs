package com.jiaruiblog.application.service.impl;

import com.jiaruiblog.application.service.RedisService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName RedisServiceImpl
 * @Description Redis服务实现类
 * @author luojiarui
 * @Date 2022/8/14 17:03
 * @Version 1.0
 **/
@Slf4j
@Service
public class RedisServiceImpl implements RedisService {

    public static final String SEARCH_KEY = "search:hot";
    public static final String DOC_KEY = "doc:hot";

    @Resource
    private StringRedisTemplate redisSearchTemplate;

    @Override
    public void setString(String key, String value) {
        redisSearchTemplate.opsForValue().set(key, value);
    }

    @Override
    public void setString(String key, String value, long expireTime) {
        redisSearchTemplate.opsForValue().set(key, value, java.time.Duration.ofSeconds(expireTime));
    }

    @Override
    public String getString(String key) {
        return redisSearchTemplate.opsForValue().get(key);
    }

    @Override
    public void setHash(String key, String field, String value) {
        redisSearchTemplate.opsForHash().put(key, field, value);
    }

    @Override
    public String getHash(String key, String field) {
        return (String) redisSearchTemplate.opsForHash().get(key, field);
    }

    @Override
    public Map<String, String> getHashAll(String key) {
        Map<Object, Object> entries = redisSearchTemplate.opsForHash().entries(key);
        Map<String, String> result = new java.util.HashMap<>();
        entries.forEach((k, v) -> result.put(String.valueOf(k), String.valueOf(v)));
        return result;
    }

    @Override
    public void setHash(String key, String field, String value, long expireTime) {
        redisSearchTemplate.opsForHash().put(key, field, value);
        redisSearchTemplate.expire(key, java.time.Duration.ofSeconds(expireTime));
    }

    @Override
    public void deleteHash(String key, String... fields) {
        redisSearchTemplate.opsForHash().delete(key, fields);
    }

    @Override
    public void setSet(String key, String... values) {
        redisSearchTemplate.opsForSet().add(key, values);
    }

    @Override
    public Set<String> getSet(String key) {
        return redisSearchTemplate.opsForSet().members(key);
    }

    @Override
    public long getSetSize(String key) {
        Long size = redisSearchTemplate.opsForSet().size(key);
        return size != null ? size : 0;
    }

    @Override
    public void deleteSetMember(String key, String... values) {
        redisSearchTemplate.opsForSet().remove(key, values);
    }

    @Override
    public void setList(String key, String... values) {
        redisSearchTemplate.opsForList().rightPushAll(key, values);
    }

    @Override
    public List<String> getList(String key) {
        return redisSearchTemplate.opsForList().range(key, 0, -1);
    }

    @Override
    public long getListSize(String key) {
        Long size = redisSearchTemplate.opsForList().size(key);
        return size != null ? size : 0;
    }

    @Override
    public void deleteListValue(String key, String... values) {
        for (String value : values) {
            redisSearchTemplate.opsForList().remove(key, 1, value);
        }
    }

    @Override
    public boolean isKeyExist(String key) {
        return Boolean.TRUE.equals(redisSearchTemplate.hasKey(key));
    }

    @Override
    public void deleteKey(String key) {
        redisSearchTemplate.delete(key);
    }

    @Override
    public void delKey(String key) {
        redisSearchTemplate.delete(key);
    }

    @Override
    public void setExpire(String key, long expireTime) {
        redisSearchTemplate.expire(key, java.time.Duration.ofSeconds(expireTime));
    }

    @Override
    public long getExpire(String key) {
        Long expire = redisSearchTemplate.getExpire(key);
        return expire != null ? expire : -1;
    }

    @Override
    public long increment(String key) {
        Long result = redisSearchTemplate.opsForValue().increment(key);
        return result != null ? result : 0;
    }

    @Override
    public long increment(String key, long expireTime) {
        Long result = redisSearchTemplate.opsForValue().increment(key);
        if (result != null && result == 1) {
            redisSearchTemplate.expire(key, java.time.Duration.ofSeconds(expireTime));
        }
        return result != null ? result : 0;
    }

    @Override
    public List<String> getSearchHistoryByUserId(String userId) {
        return List.of();
    }

    @Override
    public List<String> getHotList(String userId, String key) {
        return List.of();
    }

    @Override
    public Long delSearchHistoryByUserId(String userId, String searchWord) {
        return 0L;
    }

    @Override
    public double score(String key, String docId) {
        return 0.0;
    }

    @Override
    public void removeByDocId(String docId) {
    }

    @Override
    public void incrementScoreByUserId(String searchWord, String key) {
    }

    @Override
    public void addSearchHistoryByUserId(String userId, String searchWord) {
    }
}