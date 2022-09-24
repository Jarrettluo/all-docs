package com.jiaruiblog.service;

import java.util.List;

/**
 * @ClassName RedisService
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/8/14 17:03
 * @Version 1.0
 **/
public interface RedisService {

    int addSearchHistoryByUserId(String userid, String searchKey);

    Long delSearchHistoryByUserId(String userid, String searchKey);

    List<String> getSearchHistoryByUserId(String userid);

    int incrementScoreByUserId(String searchKey, String keyValue);

    void delKey(String searchKey, String keyValue);

    List<String> getHotList(String searchKey, String keyValue);

    Double score(String key, Object value);

    int incrementScore(String searchkey);


}
