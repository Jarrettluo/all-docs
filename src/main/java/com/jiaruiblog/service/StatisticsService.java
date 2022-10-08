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

}
