package com.jiaruiblog.domain.entity.data;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @ClassName WordCloudItem
 * @Description 词云数据项，包含词汇、出现次数及颜色配置
 * @author luojiarui
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