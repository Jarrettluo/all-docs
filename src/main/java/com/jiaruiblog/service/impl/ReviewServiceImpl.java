package com.jiaruiblog.service.impl;

import com.google.common.collect.Sets;
import com.jiaruiblog.util.BaseApiResult;
import com.jiaruiblog.util.JavaSerializable;
import org.apache.commons.compress.utils.Lists;

import java.io.*;
import java.util.*;

/**
 * @Author Jarrett Luo
 * @Date 2022/11/29 15:36
 * @Version 1.0
 */
public class ReviewServiceImpl {

    /**
     * 默认的系统自带的备选项
     */
    static Set<String> selectOption = Sets.newHashSet("低质量的", "重复的", "abc");

    /**
     * 可容纳的备选项的个数
     */
    private static final Integer OPTION_SIZE = 20;

    private static final String SERIALIZABLE_FILE = "./target/selectOption.dat";

    private static final JavaSerializable SERIALIZABLE = new JavaSerializable();

    static {
        File file = new File(SERIALIZABLE_FILE);
        try {
            if (file.exists()) {
                Object object = SERIALIZABLE.loadXml(new FileInputStream(file));
                if (object instanceof Set<?>) {
                    selectOption = new HashSet<>();
                    Set<?> obj = (Set<?>) object;
                    for (Object o : obj) {
                        selectOption.add((String) o);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 序列化可选项到本地文件中
     */
    private static void saveToLocal() {
        File xmlFile = new File(SERIALIZABLE_FILE);
        try {
            SERIALIZABLE.storeXml(selectOption, new FileOutputStream(xmlFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 如果个数小于容器个数则增加其备选区的内容
     *
     * @param info String
     */
    public synchronized void add(String info) {
        int len = selectOption.size();
        if (len < OPTION_SIZE) {
            selectOption.add(info);
            saveToLocal();
        }
    }

    /**
     * 查询备选区的数据
     *
     * @return -> BaseApiResult
     */
    public BaseApiResult query() {
        List<Map<String, String>> result = Lists.newArrayList();
        for (String s : selectOption) {
            Map<String, String> reviewValue = new HashMap<>(8);
            reviewValue.put("name", s);
            reviewValue.put("value", s);
            result.add(reviewValue);
        }
        return BaseApiResult.success(result);
    }
}
