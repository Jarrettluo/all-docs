package com.jiaruiblog.entity.data;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @ClassName WordCloudItem
 * @Description TODO
 * @Author luojiarui
 * @Date 2023/5/21 10:04
 * @Version 1.0
 **/
@Data
@AllArgsConstructor
public class WordCloudItem {

    private String word;

    private long count;

    private String color;

    // 构造方法
    public WordCloudItem(String word, long count) {
        this.word = word;
        this.count = count;
    }
}
