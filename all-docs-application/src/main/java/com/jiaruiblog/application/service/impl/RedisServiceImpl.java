package com.jiaruiblog.application.service.impl;

import com.jiaruiblog.application.service.RedisService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
        redisSearchTemplate.opsForHash().delete(key, (Object[]) fields);
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
        redisSearchTemplate.opsForSet().remove(key, (Object[]) values);
    }

    @Override
    public boolean isSetMember(String key, String value) {
        Boolean isMember = redisSearchTemplate.opsForSet().isMember(key, value);
        return Boolean.TRUE.equals(isMember);
    }

    @Override
    public Set<String> keys(String pattern) {
        Set<String> keys = redisSearchTemplate.keys(pattern);
        return keys != null ? keys : Set.of();
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
        if (!StringUtils.hasText(userId)) {
            return List.of();
        }
        String key = "search:history:" + userId;
        List<String> history = redisSearchTemplate.opsForList().range(key, 0, 9);
        return history != null ? history : List.of();
    }

    @Override
    public List<String> getHotList(String userId, String key) {
        if (key == null || key.isEmpty()) {
            key = SEARCH_KEY;
        }
        // 使用ZSet获取热门搜索词
        Set<String> hotSet = redisSearchTemplate.opsForZSet().reverseRange(key, 0, 9);
        return hotSet != null ? List.copyOf(hotSet) : List.of();
    }

    @Override
    public Long delSearchHistoryByUserId(String userId, String searchWord) {
        if (!StringUtils.hasText(userId) || !StringUtils.hasText(searchWord)) {
            return 0L;
        }
        String key = "search:history:" + userId;
        Long removed = redisSearchTemplate.opsForList().remove(key, 1, searchWord);
        return removed != null ? removed : 0L;
    }

    @Override
    public double score(String key, String docId) {
        if (!StringUtils.hasText(key) || !StringUtils.hasText(docId)) {
            return 0.0;
        }
        Double score = redisSearchTemplate.opsForZSet().score(key, docId);
        return score != null ? score : 0.0;
    }

    @Override
    public void removeByDocId(String docId) {
        if (!StringUtils.hasText(docId)) {
            return;
        }
        // 删除文档相关的搜索热度和历史记录
        redisSearchTemplate.opsForZSet().remove(SEARCH_KEY, docId);
        redisSearchTemplate.opsForZSet().remove(DOC_KEY, docId);
        log.info("删除文档相关的Redis数据：docId={}", docId);
    }

    @Override
    public void incrementScoreByUserId(String searchWord, String key) {
        if (!StringUtils.hasText(searchWord)) {
            return;
        }
        if (!StringUtils.hasText(key)) {
            key = SEARCH_KEY;
        }
        // 增加搜索词的热度分数
        redisSearchTemplate.opsForZSet().incrementScore(key, searchWord, 1);
        // 设置过期时间，避免热词永不消失
        redisSearchTemplate.expire(key, java.time.Duration.ofDays(7));
        log.info("搜索词热度增加：word={}, key={}", searchWord, key);
    }

    @Override
    public void addSearchHistoryByUserId(String userId, String searchWord) {
        if (!StringUtils.hasText(userId) || !StringUtils.hasText(searchWord)) {
            return;
        }
        String key = "search:history:" + userId;
        // 先移除已存在的相同记录，避免重复
        redisSearchTemplate.opsForList().remove(key, 1, searchWord);
        // 添加到列表头部
        redisSearchTemplate.opsForList().leftPush(key, searchWord);
        // 只保留最近10条记录
        redisSearchTemplate.opsForList().trim(key, 0, 9);
        // 设置过期时间
        redisSearchTemplate.expire(key, java.time.Duration.ofDays(30));
        log.info("添加搜索历史：userId={}, word={}", userId, searchWord);
    }
}