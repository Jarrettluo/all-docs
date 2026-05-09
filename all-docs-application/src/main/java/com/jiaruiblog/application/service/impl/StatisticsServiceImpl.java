package com.jiaruiblog.application.service.impl;

import com.jiaruiblog.application.service.StatisticsService;
import com.jiaruiblog.domain.entity.dto.StatisticsDTO;
import com.jiaruiblog.domain.entity.po.FileDocument;
import com.jiaruiblog.domain.entity.vo.CategoryDistVO;
import com.jiaruiblog.domain.entity.vo.DocTypeDistVO;
import com.jiaruiblog.domain.entity.vo.HotDocVO;
import com.jiaruiblog.domain.entity.vo.MonthStatVO;
import com.jiaruiblog.domain.entity.vo.SearchHotWordVO;
import com.jiaruiblog.domain.entity.vo.StatsVO;
import com.jiaruiblog.domain.entity.vo.TrendVO;
import com.jiaruiblog.domain.entity.vo.UserActivityVO;
import com.jiaruiblog.common.enums.RedisActionEnum;
import com.jiaruiblog.infrastructure.repository.DocumentRepository;
import com.jiaruiblog.infrastructure.repository.UserRepository;
import com.jiaruiblog.infrastructure.repository.TagRepository;
import com.jiaruiblog.infrastructure.repository.CategoryRepository;
import com.jiaruiblog.infrastructure.repository.CollectRepository;
import com.jiaruiblog.infrastructure.repository.CommentRepository;
import com.jiaruiblog.application.service.RedisService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    @Resource
    private CommentRepository commentRepository;

    @Resource
    private RedisService redisService;

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
            return collectRepository.count();
        } catch (Exception e) {
            log.error("统计收藏数量失败", e);
            return 0;
        }
    }

    @Override
    public long countLike() {
        try {
            // 从 Redis 获取所有点赞 key 的总数
            String likePattern = MessageFormat.format("like:entity:{0}:*", RedisActionEnum.LIKE.getCode());
            Set<String> keys = redisService.keys(likePattern);
            long total = 0;
            for (String key : keys) {
                total += redisService.getSetSize(key);
            }
            return total;
        } catch (Exception e) {
            log.error("统计点赞数量失败", e);
            return 0;
        }
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
        try {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -6);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            Date startDate = cal.getTime();
            Date endDate = new Date();

            List<MonthStatVO> dailyStats = documentRepository.trend(startDate, endDate);
            List<TrendVO> trends = new ArrayList<>();

            for (MonthStatVO stat : dailyStats) {
                TrendVO vo = new TrendVO();
                vo.setId(stat.getDate());
                vo.setName(stat.getDate() + " (" + stat.getCount() + ")");
                trends.add(vo);
            }
            return trends;
        } catch (Exception e) {
            log.error("获取趋势数据失败", e);
            return List.of();
        }
    }

    @Override
    public StatsVO all() {
        StatsVO vo = new StatsVO();
        vo.setDocNum(countDocument());
        vo.setUserNum(userRepository.count());
        vo.setCategoryNum(countCategory());
        vo.setTagNum(countTag());
        vo.setCommentNum(commentRepository.count());
        // 以下暂无数据源，返回 0
        vo.setDownloadNum(0L);
        vo.setSearchNum(0L);
        vo.setViewNum(0L);
        return vo;
    }

    @Override
    public List<MonthStatVO> getMonthStat() {
        try {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -5);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            Date startDate = cal.getTime();
            Date endDate = new Date();
            return documentRepository.stats(startDate, endDate);
        } catch (Exception e) {
            log.error("获取月度统计失败", e);
            return List.of();
        }
    }

    @Override
    public List<DocTypeDistVO> docTypeDist() {
        List<Map<String, Object>> rawList = documentRepository.countByDocType();
        List<DocTypeDistVO> result = new ArrayList<>();
        for (Map<String, Object> map : rawList) {
            DocTypeDistVO vo = new DocTypeDistVO();
            vo.setType((String) map.get("type"));
            Object countObj = map.get("count");
            vo.setCount(countObj != null ? ((Number) countObj).longValue() : 0L);
            result.add(vo);
        }
        return result;
    }

    @Override
    public List<CategoryDistVO> categoryDist() {
        List<Map<String, Object>> rawList = documentRepository.countByCategory();
        List<CategoryDistVO> result = new ArrayList<>();
        for (Map<String, Object> map : rawList) {
            CategoryDistVO vo = new CategoryDistVO();
            vo.setCategory((String) map.get("category"));
            Object countObj = map.get("count");
            vo.setCount(countObj != null ? ((Number) countObj).longValue() : 0L);
            result.add(vo);
        }
        return result;
    }

    @Override
    public List<HotDocVO> hotDocs() {
        List<String> docIdList = redisService.getHotList(null, RedisServiceImpl.DOC_KEY);
        List<HotDocVO> result = new ArrayList<>();
        if (docIdList == null || docIdList.isEmpty()) {
            return result;
        }
        int limit = Math.min(docIdList.size(), 10);
        for (int i = 0; i < limit; i++) {
            String docId = docIdList.get(i);
            FileDocument doc = documentRepository.findById(docId);
            if (doc == null) {
                continue;
            }
            HotDocVO vo = new HotDocVO();
            vo.setId(docId);
            vo.setTitle(doc.getName());
            vo.setViewCount((long) redisService.score(RedisServiceImpl.DOC_KEY, docId));
            result.add(vo);
        }
        return result;
    }

    @Override
    public List<SearchHotWordVO> searchHotWords() {
        List<String> hotList = redisService.getHotList(null, RedisServiceImpl.SEARCH_KEY);
        List<SearchHotWordVO> result = new ArrayList<>();
        if (hotList == null || hotList.isEmpty()) {
            return result;
        }
        int limit = Math.min(hotList.size(), 10);
        for (int i = 0; i < limit; i++) {
            String keyword = hotList.get(i);
            SearchHotWordVO vo = new SearchHotWordVO();
            vo.setKeyword(keyword);
            vo.setCount((long) redisService.score(RedisServiceImpl.SEARCH_KEY, keyword));
            result.add(vo);
        }
        return result;
    }

    @Override
    public List<UserActivityVO> userActivity() {
        // 暂无数据源，返回空列表
        return new ArrayList<>();
    }
}