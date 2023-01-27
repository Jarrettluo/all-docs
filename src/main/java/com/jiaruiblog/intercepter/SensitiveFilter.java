package com.jiaruiblog.intercepter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName SensitiveFilter
 * @Description 敏感词过滤器：利用DFA算法  进行敏感词过滤
 * https://houbb.github.io/2020/01/07/sensitive-word-dfa#:
 * ~:text=DFA%20%E7%AE%97%E6%B3%95.%20%E5%9C%A8%E5%AE%9E%E7%8E%B0%E6
 * %96%87%E5%AD%97%E8%BF%87%E6%BB%A4%E7%9A%84%E7%AE%97%E6%B3%95%E4%B
 * 8%AD%EF%BC%8CDFA%E6%98%AF%E6%AF%94%E8%BE%83%E5%A5%BD%E7%9A%84%E5%
 * AE%9E%E7%8E%B0%E7%AE%97%E6%B3%95%E3%80%82,DFA%20%E5%8D%B3Determin
 * istic%20Finite%20Automaton%EF%BC%8C%E4%B9%9F%E5%B0%B1%E6%98%AF%E7
 * %A1%AE%E5%AE%9A%E6%9C%89%E7%A9%B7%E8%87%AA%E5%8A%A8%E6%9C%BA
 * @Author luojiarui
 * @Date 2022/8/14 16:17
 * @Version 1.0
 **/
public class SensitiveFilter {

    private static final int MATCH_FLAG = 2;

    /**
     * 敏感词过滤器：利用DFA算法  进行敏感词过滤
     */
    private Map sensitiveWordMap;

    /**
     * 最小匹配规则
     */
    public static int minMatchType = 1;

    /**
     * 最大匹配规则
     */
    public static int maxMatchType = 2;

    /**
     * 单例
     */
    private static SensitiveFilter instance = null;

    /**
     * 构造函数，初始化敏感词库
     * @throws IOException io exception
     */
    private SensitiveFilter() throws IOException {
        sensitiveWordMap = new SensitiveWordInit().initKeyWord();
    }

    /**
     * 获取单例
     * @return filter
     * @throws IOException io exception
     */
    public static SensitiveFilter getInstance() throws IOException {
        if (null == instance) {
            instance = new SensitiveFilter();
        }
        return instance;
    }

    public void refresh() throws IOException {
        sensitiveWordMap = new SensitiveWordInit().initKeyWord();
    }

    /**
     * 获取文字中的敏感词
     * @param txt String
     * @param matchType int
     * @return Set
     */
    public Set<String> getSensitiveWord(String txt, int matchType) {
        Set<String> sensitiveWordList = new HashSet<>();
        for (int i = 0; i < txt.length(); i++) {
            // 判断是否包含敏感字符
            int length = checkSensitiveWord(txt, i, matchType);
            // 存在,加入list中
            if (length > 0) {
                sensitiveWordList.add(txt.substring(i, i + length));
                // 减1的原因，是因为for会自增
                i = i + length - 1;
            }
        }
        return sensitiveWordList;
    }
    /**
     * 检查文字中是否包含敏感字符，检查规则如下：
     * 如果存在，则返回敏感词字符的长度，不存在返回0
     * @param txt String
     * @param beginIndex int
     * @param matchType int
     * @return int
     */
    public int checkSensitiveWord(String txt, int beginIndex, int matchType) {
        // 敏感词结束标识位：用于敏感词只有1位的情况
        boolean flag = false;
        // 匹配标识数默认为0
        int matchFlag = 0;

        Map nowMap = sensitiveWordMap;

        for (int i = beginIndex; i < txt.length(); i++) {
            // 截取敏感词当中的字，在敏感词库中字为HashMap 对象的Key键值
            char word = txt.charAt(i);
            // 获取指定key的map，并且将获取到的map作为下次检索的map
            // 这也是dfa算法的精髓，
            nowMap = (Map) nowMap.get(word);
            // 存在，则判断是否为最后一个
            if (nowMap != null) {
                // 找到相应key，匹配标识+1
                matchFlag++;
                // 如果为最后一个匹配规则,结束循环，返回匹配标识数
                if ("1".equals(nowMap.get("isEnd"))) {
                    // 结束标志位为true
                    flag = true;
                    // 最小规则，直接返回,最大规则还需继续查找
                    if (SensitiveFilter.minMatchType == matchType) {
                        break;
                    }
                }
            }
            // 不存在，直接返回
            else {
                break;
            }
        }
        if (SensitiveFilter.maxMatchType == matchType){
            //长度必须大于等于1，为词
            if(matchFlag < MATCH_FLAG || !flag){
                matchFlag = 0;
            }
        }
        if (SensitiveFilter.minMatchType == matchType){
            //长度必须大于等于1，为词
            if(matchFlag < MATCH_FLAG && !flag){
                matchFlag = 0;
            }
        }
        return matchFlag;
    }

    /**
     * 替换敏感字字符
     * @param txt String
     * @param matchType int
     * @param replaceChar String
     * @return String
     */
    public String replaceSensitiveWord(String txt, int matchType,
                                       String replaceChar) {
        String resultTxt = txt;
        // 获取所有的敏感词
        Set<String> set = getSensitiveWord(txt, matchType);
        Iterator<String> iterator = set.iterator();
        String word;
        String replaceString;
        while (iterator.hasNext()) {
            word = iterator.next();
            replaceString = getReplaceChars(replaceChar, word.length());
            resultTxt = resultTxt.replaceAll(word, replaceString);
        }
        return resultTxt;
    }

    /**
     * 获取替换字符串
     *
     * @param replaceChar String
     * @param length int
     * @return String
     */
    private String getReplaceChars(String replaceChar, int length) {
        StringBuilder resultReplace = new StringBuilder(replaceChar);
        for (int i = 1; i < length; i++) {
            resultReplace.append(replaceChar);
        }
        return resultReplace.toString();
    }

}
