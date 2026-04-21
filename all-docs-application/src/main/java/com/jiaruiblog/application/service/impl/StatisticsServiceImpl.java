package com.jiaruiblog.application.service.impl;

import com.jiaruiblog.application.service.StatisticsService;
import com.jiaruiblog.domain.entity.dto.StatisticsDTO;
import com.jiaruiblog.domain.entity.vo.MonthStatVO;
import com.jiaruiblog.domain.entity.vo.StatsVO;
import com.jiaruiblog.domain.entity.vo.TrendVO;
import com.jiaruiblog.infrastructure.repository.DocumentRepository;
import com.jiaruiblog.infrastructure.repository.UserRepository;
import com.jiaruiblog.infrastructure.repository.TagRepository;
import com.jiaruiblog.infrastructure.repository.CategoryRepository;
import com.jiaruiblog.infrastructure.repository.CollectRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author luojiarui
 **/
@Slf4j
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Resource
    private DocumentRepository documentRepository;

    @Resource
    private UserRepository userRepository;

    @Resource
    private TagRepository tagRepository;

    @Resource
    private CategoryRepository categoryRepository;

    @Resource
    private CollectRepository collectRepository;

    @Override
    public long countDocument() {
        try {
            return documentRepository.count();
        } catch (Exception e) {
            log.error("统计文档数量失败", e);
            return 0;
        }
    }

    @Override
    public long countUser() {
        try {
            return userRepository.count();
        } catch (Exception e) {
            log.error("统计用户数量失败", e);
            return 0;
        }
    }

    @Override
    public long countTag() {
        try {
            return tagRepository.count();
        } catch (Exception e) {
            log.error("统计标签数量失败", e);
            return 0;
        }
    }

    @Override
    public long countCategory() {
        try {
            return categoryRepository.countAll();
        } catch (Exception e) {
            log.error("统计分类数量失败", e);
            return 0;
        }
    }

    @Override
    public long countCollect() {
        try {
            // CollectRepository doesn't have count method, return 0 as fallback
            return 0;
        } catch (Exception e) {
            log.error("统计收藏数量失败", e);
            return 0;
        }
    }

    @Override
    public long countLike() {
        // 点赞数量统计需要通过Redis或其他方式获取
        return 0;
    }

    @Override
    public StatisticsDTO getStatistics() {
        StatisticsDTO dto = new StatisticsDTO();
        dto.setDocumentCount(countDocument());
        dto.setUserCount(countUser());
        dto.setTagCount(countTag());
        dto.setCategoryCount(countCategory());
        dto.setCollectCount(countCollect());
        dto.setLikeCount(countLike());
        return dto;
    }

    @Override
    public List<TrendVO> trend() {
        // 返回最近7天的趋势数据
        List<TrendVO> trends = new ArrayList<>();
        Calendar cal = Calendar.getInstance();

        for (int i = 6; i >= 0; i--) {
            TrendVO vo = new TrendVO();
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_YEAR, -i);
            vo.setCreateDate(cal.getTime());
            vo.setCount(0L);
            trends.add(vo);
        }
        return trends;
    }

    @Override
    public StatsVO all() {
        StatsVO vo = new StatsVO();
        vo.setDocumentCount(countDocument());
        vo.setUserCount(countUser());
        vo.setTagCount(countTag());
        vo.setCategoryCount(countCategory());
        vo.setCollectCount(countCollect());
        vo.setLikeCount(countLike());
        return vo;
    }

    @Override
    public List<MonthStatVO> getMonthStat() {
        // 返回最近6个月的月度统计
        List<MonthStatVO> stats = new ArrayList<>();
        Calendar cal = Calendar.getInstance();

        for (int i = 5; i >= 0; i--) {
            MonthStatVO vo = new MonthStatVO();
            cal.setTime(new Date());
            cal.add(Calendar.MONTH, -i);
            vo.setMonth(cal.get(Calendar.MONTH) + 1);
            vo.setYear(cal.get(Calendar.YEAR));
            vo.setDocumentCount(0L);
            vo.setUserCount(0L);
            stats.add(vo);
        }
        return stats;
    }
}