package com.jiaruiblog.application.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Jarrett Luo
 * @Date 2022/6/7 11:39
 * @Version 1.0
 */
public interface RedisService {

    /**
     * @author luojiarui
     * @Description 保存字符串值
     * @Date 16:12 2023/1/31
     * @Param [key, value]
     **/
    void setString(String key, String value);

    /**
     * @author luojiarui
     * @Description 保存字符串值并设置过期时间
     * @Date 16:12 2023/1/31
     * @Param [key, value, expireTime]
     **/
    void setString(String key, String value, long expireTime);

    /**
     * @author luojiarui
     * @Description 获取字符串值
     * @Date 16:12 2023/1/31
     * @Param [key]
     * @return java.lang.String
     **/
    String getString(String key);

    /**
     * @author luojiarui
     * @Description 保存hash
     * @Date 16:12 2023/1/31
     * @Param [key, field, value]
     **/
    void setHash(String key, String field, String value);

    /**
     * @author luojiarui
     * @Description 获取hash值
     * @Date 16:12 2023/1/31
     * @Param [key, field]
     * @return java.lang.String
     **/
    String getHash(String key, String field);

    /**
     * @author luojiarui
     * @Description 获取hash所有字段
     * @Date 16:12 2023/1/31
     * @Param [key]
     * @return java.util.Map<java.lang.String, java.lang.String>
     **/
    Map<String, String> getHashAll(String key);

    /**
     * @author luojiarui
     * @Description 设置有过期时间的hash
     * @Date 16:12 2023/1/31
     * @Param [key, field, value, expireTime]
     **/
    void setHash(String key, String field, String value, long expireTime);

    /**
     * @author luojiarui
     * @Description 删除hash字段
     * @Date 16:12 2023/1/31
     * @Param [key, fields]
     **/
    void deleteHash(String key, String... fields);

    /**
     * @author luojiarui
     * @Description 设置集合
     * @Date 16:12 2023/1/31
     * @Param [key, values]
     **/
    void setSet(String key, String... values);

    /**
     * @author luojiarui
     * @Description 获取集合
     * @Date 16:12 2023/1/31
     * @Param [key]
     * @return java.util.Set<java.lang.String>
     **/
    Set<String> getSet(String key);

    /**
     * @author luojiarui
     * @Description 获取集合成员数量
     * @Date 16:12 2023/1/31
     * @Param [key]
     * @return long
     **/
    long getSetSize(String key);

    /**
     * @author luojiarui
     * @Description 删除集合中的成员
     * @Date 16:12 2023/1/31
     * @Param [key, values]
     **/
    void deleteSetMember(String key, String... values);

    /**
     * 判断成员是否在集合中
     * @param key 键
     * @param value 成员值
     * @return 是否存在
     */
    boolean isSetMember(String key, String value);

    /**
     * 模糊匹配获取键列表
     * @param pattern 匹配模式，如 "like:entity:*"
     * @return 匹配的键集合
     */
    Set<String> keys(String pattern);

    /**
     * @author luojiarui
     * @Description 保存列表
     * @Date 16:12 2023/1/31
     * @Param [key, values]
     **/
    void setList(String key, String... values);

    /**
     * @author luojiarui
     * @Description 获取列表
     * @Date 16:12 2023/1/31
     * @Param [key]
     * @return java.util.List<java.lang.String>
     **/
    List<String> getList(String key);

    /**
     * @author luojiarui
     * @Description 获取列表长度
     * @Date 16:12 2023/1/31
     * @Param [key]
     * @return long
     **/
    long getListSize(String key);

    /**
     * @author luojiarui
     * @Description 删除列表中的值
     * @Date 16:12 2023/1/31
     * @Param [key, values]
     **/
    void deleteListValue(String key, String... values);

    /**
     * @author luojiarui
     * @Description 判断key是否存在
     * @Date 16:12 2023/1/31
     * @Param [key]
     * @return boolean
     **/
    boolean isKeyExist(String key);

    /**
     * @author luojiarui
     * @Description 删除key
     * @Date 16:12 2023/1/31
     * @Param [key]
     **/
    void deleteKey(String key);

    /**
     * 删除key (alias for deleteKey)
     */
    void delKey(String key);

    /**
     * @author luojiarui
     * @Description 设置key过期时间
     * @Date 16:12 2023/1/31
     * @Param [key, expireTime]
     **/
    void setExpire(String key, long expireTime);

    /**
     * @author luojiarui
     * @Description 获取key剩余过期时间
     * @Date 16:12 2023/1/31
     * @Param [key]
     * @return long
     **/
    long getExpire(String key);

    /**
     * @author luojiarui
     * @Description 自增操作
     * @Date 16:12 2023/1/31
     * @Param [key]
     * @return long
     **/
    long increment(String key);

    /**
     * @author luojiarui
     * @Description 自增操作并设置过期时间
     * @Date 16:12 2023/1/31
     * @Param [key, expireTime]
     * @return long
     **/
    long increment(String key, long expireTime);

    /**
     * 获取用户搜索历史
     */
    List<String> getSearchHistoryByUserId(String userId);

    /**
     * 获取热门列表
     */
    List<String> getHotList(String userId, String key);

    /**
     * 删除用户搜索历史
     */
    Long delSearchHistoryByUserId(String userId, String searchWord);

    /**
     * 添加用户搜索历史
     */
    void addSearchHistoryByUserId(String userId, String searchWord);

    /**
     * 获取分数
     */
    double score(String key, String docId);

    /**
     * 删除文档相关的Redis数据
     */
    void removeByDocId(String docId);

    /**
     * 增加用户搜索词分数
     */
    void incrementScoreByUserId(String searchWord, String key);
}