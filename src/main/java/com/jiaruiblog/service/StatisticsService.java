package com.jiaruiblog.service;

import com.jiaruiblog.entity.vo.StatsVO;
import com.jiaruiblog.entity.vo.TrendVO;

import java.util.List;
import java.util.Map;


/**
 * @author jiarui.luo
 */
public interface StatisticsService {

    /**
     * trend
     * @return trend
     */
    List<TrendVO> trend();

    /**
     * all
     * @return all
     */
    StatsVO all();

    /**
     * @author luojiarui
     * @Description 统计各月的数据
     * @Date 17:10 2023/5/20
     * @Param []
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    Map<String, Integer> getMonthStat();

}
