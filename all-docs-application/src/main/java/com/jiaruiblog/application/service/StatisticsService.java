package com.jiaruiblog.application.service;

import com.jiaruiblog.domain.entity.dto.StatisticsDTO;
import com.jiaruiblog.domain.entity.vo.CategoryDistVO;
import com.jiaruiblog.domain.entity.vo.DocTypeDistVO;
import com.jiaruiblog.domain.entity.vo.HotDocVO;
import com.jiaruiblog.domain.entity.vo.MonthStatVO;
import com.jiaruiblog.domain.entity.vo.SearchHotWordVO;
import com.jiaruiblog.domain.entity.vo.StatsVO;
import com.jiaruiblog.domain.entity.vo.TrendVO;
import com.jiaruiblog.domain.entity.vo.UserActivityVO;

import java.util.List;
import java.util.Map;

/**
 * @author Jarrett Luo
 * @Date 2022/6/7 11:38
 * @Version 1.0
 */
public interface StatisticsService {

    /**
     * @author luojiarui
     * @Description 统计文档数量
     * @Date 15:03 2023/2/10
     * @Param []
     * @return long
     **/
    long countDocument();

    /**
     * @author luojiarui
     * @Description 统计用户数量
     * @Date 15:03 2023/2/10
     * @Param []
     * @return long
     **/
    long countUser();

    /**
     * @author luojiarui
     * @Description 统计标签数量
     * @Date 15:03 2023/2/10
     * @Param []
     * @return long
     **/
    long countTag();

    /**
     * @author luojiarui
     * @Description 统计分类数量
     * @Date 15:03 2023/2/10
     * @Param []
     * @return long
     **/
    long countCategory();

    /**
     * @author luojiarui
     * @Description 统计收藏数量
     * @Date 15:03 2023/2/10
     * @Param []
     * @return long
     **/
    long countCollect();

    /**
     * @author luojiarui
     * @Description 统计点赞数量
     * @Date 15:03 2023/2/10
     * @Param []
     * @return long
     **/
    long countLike();

    /**
     * @author luojiarui
     * @Description 获取统计数据
     * @Date 15:03 2023/2/10
     * @Param []
     * @return StatisticsDTO
     **/
    StatisticsDTO getStatistics();

    /**
     * 查询热度趋势
     */
    List<TrendVO> trend();

    /**
     * 获取所有统计数据
     */
    StatsVO all();

    /**
     * 获取月度统计数据
     */
    List<MonthStatVO> getMonthStat();

    /**
     * 按文档类型统计分布
     */
    List<DocTypeDistVO> docTypeDist();

    /**
     * 按分类统计分布
     */
    List<CategoryDistVO> categoryDist();

    /**
     * 获取热门文档
     */
    List<HotDocVO> hotDocs();

    /**
     * 获取搜索热词
     */
    List<SearchHotWordVO> searchHotWords();

    /**
     * 获取用户活动统计
     */
    List<UserActivityVO> userActivity();
}