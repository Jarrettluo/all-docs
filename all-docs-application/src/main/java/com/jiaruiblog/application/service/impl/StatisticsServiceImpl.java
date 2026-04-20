package com.jiaruiblog.application.service.impl;

import com.jiaruiblog.application.service.StatisticsService;
import com.jiaruiblog.domain.entity.dto.StatisticsDTO;
import com.jiaruiblog.domain.entity.vo.MonthStatVO;
import com.jiaruiblog.domain.entity.vo.StatsVO;
import com.jiaruiblog.domain.entity.vo.TrendVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author luojiarui
 **/
@Slf4j
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Override
    public long countDocument() {
        return 0;
    }

    @Override
    public long countUser() {
        return 0;
    }

    @Override
    public long countTag() {
        return 0;
    }

    @Override
    public long countCategory() {
        return 0;
    }

    @Override
    public long countCollect() {
        return 0;
    }

    @Override
    public long countLike() {
        return 0;
    }

    @Override
    public StatisticsDTO getStatistics() {
        return new StatisticsDTO();
    }

    @Override
    public List<TrendVO> trend() {
        return List.of();
    }

    @Override
    public StatsVO all() {
        return new StatsVO();
    }

    @Override
    public List<MonthStatVO> getMonthStat() {
        return List.of();
    }
}