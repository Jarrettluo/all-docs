package com.jiaruiblog.service.impl;

import com.jiaruiblog.service.RedisService;
import com.jiaruiblog.util.RedisKeyUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName RedisServiceImpl
 * @Description 用户在搜索栏输入某字符，则将该字符记录下来 以zSet格式存储的redis中
 * 每当用户查询了已在redis存在了的字符时，则直接累加个数， 用来获取平台上最热查询的十条数据
 * 热词存储功能参考：https://zhuanlan.zhihu.com/p/551125686
 * @Author luojiarui
 * @Date 2022/8/14 17:03
 * @Version 1.0
 **/
@Slf4j
@Transactional(rollbackFor = Exception.class)
@Service
public class RedisServiceImpl implements RedisService {


    /**
     * 用户存储用户的搜索关键字
     */
    public static final String SEARCH_KEY = "search_key";

    /**
     * 用于存储文档的检索关键字
     */
    public static final String DOC_KEY = "doc_key";

    /**
     * StringRedisTemplate
     */
    @Resource
    private StringRedisTemplate redisSearchTemplate;

    /**
     * 新增一条该userid用户在搜索栏的历史记录
     * searchKey 代表输入的关键词
     * @param userid String
     * @param searchKey String
     * @return int
     */
    @Override
    public int addSearchHistoryByUserId(String userid, String searchKey) {
        String searchHistoryKey = userid;
        boolean b = redisSearchTemplate.hasKey(searchHistoryKey);
        if (b) {
            Object hk = redisSearchTemplate.opsForHash().get(searchHistoryKey, searchKey);
            if (hk != null) {
                return 1;
            }else{
                redisSearchTemplate.opsForHash().put(searchHistoryKey, searchKey, "1");
            }
        }else{
            redisSearchTemplate.opsForHash().put(searchHistoryKey, searchKey, "1");
        }
        return 1;
    }

    /**
     * 删除个人历史数据
     * @param userid String 用户id
     * @param searchKey 搜索关键字
     * @return Long
     */
    @Override
    public Long delSearchHistoryByUserId(String userid, String searchKey) {
        String searchHistoryKey = RedisKeyUtils.getSearchHistoryKey(userid);
        return redisSearchTemplate.opsForHash().delete(searchHistoryKey, searchKey);
    }

    /**
     * 获取个人历史数据列表
     * @param userid String userId
     * @return List
     */
    @Override
    public List<String> getSearchHistoryByUserId(String userid) {
        List<String> stringList = Lists.newArrayList();
//        String searchHistoryKey = RedisKeyUtils.getSearchHistoryKey(userid);
        String searchHistoryKey = userid;
//        if (StringUtils.isEmpty(searchHistoryKey)) {
//            return Lists.newArrayList();
//        }
        boolean b = redisSearchTemplate.hasKey(searchHistoryKey);
        if(b){
            Cursor<Map.Entry<Object, Object>> cursor = redisSearchTemplate.opsForHash()
                    .scan(searchHistoryKey, ScanOptions.NONE);
            while (cursor.hasNext()) {
                Map.Entry<Object, Object> map = cursor.next();
                String key = map.getKey().toString();
                stringList.add(key);
            }
            try {
                cursor.close();
            } catch (Exception e) {
                log.error("游标关闭异常");
            }
            return stringList;
        }
        return Lists.newArrayList();
    }

    /**
     * 新增一条热词搜索记录，将用户输入的热词存储下来
     * @param searchKey String
     * @param value String
     * @return int
     */
    @Override
    public int incrementScoreByUserId(String searchKey, String value) {
        Long now = System.currentTimeMillis();
        ZSetOperations<String, String> zSetOperations = redisSearchTemplate.opsForZSet();
        ValueOperations<String, String> valueOperations = redisSearchTemplate.opsForValue();
        List<String> title = new ArrayList<>();
        title.add(searchKey);
        for (String tle : title) {
            try {
                // 如果没找到相应的key，则返回null
                if (zSetOperations.score(value, tle) == null) {
                    zSetOperations.add(value, tle, 0);
                    valueOperations.set(tle, String.valueOf(now));
                } else {
                    zSetOperations.incrementScore(value, tle, 1);
                    valueOperations.getAndSet(tle, String.valueOf(now));
                }
            } catch (Exception e) {
                zSetOperations.add(value, tle, 0);
                valueOperations.set(tle, String.valueOf(now));
            }
        }
        return 1;
    }

    @Override
    public void delKey(String searchKey, String keyValue){
        redisSearchTemplate.opsForZSet().remove(keyValue, searchKey);
    }

    /**
     * 根据searchKey搜索其相关最热的前十名 (如果searchKey为null空，则返回redis存储的前十最热词条)
     * @param searchKey String
     * @param keyValue String
     * @return List
     */
    @Override
    public List<String> getHotList(String searchKey, String keyValue) {
        String key = searchKey;
        Long now = System.currentTimeMillis();
        List<String> result = new ArrayList<>();
        ZSetOperations<String, String> zSetOperations = redisSearchTemplate.opsForZSet();
        ValueOperations<String, String> valueOperations = redisSearchTemplate.opsForValue();
        Set<String> value = zSetOperations.reverseRangeByScore(keyValue, 0, Double.MAX_VALUE);
        //key不为空的时候 推荐相关的最热前十名
        if(StringUtils.isNotEmpty(searchKey)){
            for (String val : value) {
                if (StringUtils.containsIgnoreCase(val, key)) {
                    //只返回最热的前十名
                    if (result.size() > 9) {
                        break;
                    }
                    Long time = Long.valueOf(valueOperations.get(val));
                    //返回最近一个月的数据
                    if ((now - time) < 2592000000L) {
                        result.add(val);
                    } else {//时间超过一个月没搜索就把这个词热度归0
                        zSetOperations.add(keyValue, val, 0);
                    }
                }
            }
        }else{
            for (String val : value) {
                //只返回最热的前十名
                if (result.size() > 9) {
                    break;
                }
                Long time = Long.valueOf(valueOperations.get(val));
                //返回最近一个月的数据
                if ((now - time) < 2592000000L) {
                    result.add(val);
                } else {
                    //时间超过一个月没搜索就把这个词热度归0
                    zSetOperations.add(keyValue, val, 0);
                }
            }
        }
        return result;
    }

    /**
     * @Author luojiarui
     * @Description 查询某个value的分数
     * @Date 15:38 2022/9/11
     * @Param [key, value]
     * @return java.lang.Double
     **/
    @Override
    public Double score(String key, Object value) {
        return redisSearchTemplate.opsForZSet().score(key, value);
    }

    /**
     * 每次点击给相关词searchKey热度 +1
     * @param searchKey String
     * @return int
     * @deprecated 暂时废弃
     */
    @Override
    @Deprecated
    public int incrementScore(String searchKey) {
        String key = searchKey;
        Long now = System.currentTimeMillis();
        ZSetOperations<String, String> zSetOperations = redisSearchTemplate.opsForZSet();
        ValueOperations<String, String> valueOperations = redisSearchTemplate.opsForValue();
        zSetOperations.incrementScore("title", key, 1);
        valueOperations.getAndSet(key, String.valueOf(now));
        return 1;
    }

    @Override
    public void removeByDocId(String docId) {
        delKey(docId, DOC_KEY);
    }
}
