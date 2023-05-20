package com.jiaruiblog.service;

import com.jiaruiblog.util.BaseApiResult;


/**
 * @author jiarui.luo
 */
public interface StatisticsService {

    /**
     * trend
     * @return trend
     */
    BaseApiResult trend();

    /**
     * all
     * @return all
     */
    BaseApiResult all();

    /**
     * @Author luojiarui
     * @Description 统计各月的数据
     * @Date 17:10 2023/5/20
     * @Param []
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    BaseApiResult getMonthStat();

}
