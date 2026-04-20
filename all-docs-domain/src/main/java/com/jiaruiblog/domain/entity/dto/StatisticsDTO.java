package com.jiaruiblog.domain.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统计数据传输对象
 *
 * @author Jarrett Luo
 * @Date 2022/6/7 11:38
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsDTO {

    private long documentCount;

    private long userCount;

    private long tagCount;

    private long categoryCount;

    private long collectCount;

    private long likeCount;
}
