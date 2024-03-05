package com.jiaruiblog.service;

import java.util.List;

/**
 * @ClassName RedisService
 * @Description REDIS SERVICE
 * @Author luojiarui
 * @Date 2022/8/14 17:03
 * @Version 1.0
 **/
public interface RedisService {

    /**
     * add
     * @param userid String
     * @param searchKey String
     * @return result
     */
    int addSearchHistoryByUserId(String userid, String searchKey);

    /**
     * del
     * @param userid String
     * @param searchKey String
     * @return result
     */
    Long delSearchHistoryByUserId(String userid, String searchKey);

    /**
     * getSearchHistoryByUserId
     * @param userid String
     * @return result
     */
    List<String> getSearchHistoryByUserId(String userid);

    /**
     * incrementScoreByUserId
     * @param searchKey String search key
     * @param keyValue String
     * @return count
     */
    int incrementScoreByUserId(String searchKey, String keyValue);

    /**
     * delKey
     * @param searchKey String
     * @param keyValue String
     */
    void delKey(String searchKey, String keyValue);

    /**
     * getHotList
     * @param searchKey String
     * @param keyValue String
     * @return result
     */
    List<String> getHotList(String searchKey, String keyValue);

    /**
     * score
     * @param key String
     * @param value String
     * @return result
     */
    Double score(String key, Object value);

    /**
     * incrementScore
     * @param searchKey String
     * @return result
     */
    int incrementScore(String searchKey);

    void removeByDocId(String docId);


}
