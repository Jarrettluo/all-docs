package com.jiaruiblog.domain.entity.vo;

import lombok.Data;

/**
 * @ClassName StatsVO
 * @Description StatsVO
 * @author luojiarui
 * @Date 2022/6/26 4:26 下午
 * @Version 1.0
 **/
@Data
public class StatsVO {

    private Long docNum;

    private Long categoryNum;

    private Long tagNum;

    private Long commentNum;

    private Long userNum;

    private Long downloadNum;

    private Long searchNum;

    private Long viewNum;

}