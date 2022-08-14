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

    int addSearchHistoryByUserId(String userid, String searchkey);

    Long delSearchHistoryByUserId(String userid, String searchkey);

    List<String> getSearchHistoryByUserId(String userid);

    int incrementScoreByUserId(String searchkey);

    List<String> getHotList(String searchkey);

    int incrementScore(String searchkey);


}
