package com.jiaruiblog.service.impl;

import com.jiaruiblog.entity.CateDocRelationship;
import com.jiaruiblog.entity.Category;
import com.jiaruiblog.entity.FileDocument;
import com.jiaruiblog.entity.vo.DocVO;
import com.jiaruiblog.entity.vo.MonthStatVO;
import com.jiaruiblog.entity.vo.StatsVO;
import com.jiaruiblog.entity.vo.TrendVO;
import com.jiaruiblog.repository.DocumentRepository;
import com.jiaruiblog.service.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author luojiarui
 **/
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Resource
    CategoryService categoryService;

    @Resource
    DocumentService fileService;

    @Resource
    TagService tagService;

    @Resource
    ICommentService commentService;

    @Resource
    DocumentRepository documentRepository;


    /**
     * @return com.jiaruiblog.utils.ApiResult
     * @author luojiarui
     * 统计随机的三个分类
     **/
    @Override
    public List<TrendVO> trend() {
        List<Category> categoryList = categoryService.getRandom();
        List<TrendVO> trendVos = new ArrayList<>(3);

        for (Category category : categoryList) {
            category = Optional.ofNullable(category).orElse(new Category());
            TrendVO trendVO = new TrendVO();
            trendVO.setId(category.getId());
            trendVO.setName(category.getName());
            List<DocVO> docVos = new ArrayList<>();

            if (category.getId() != null) {
                List<FileDocument> documents;
                List<CateDocRelationship> relationships = categoryService.getRelateByCateId(category.getId());
                List<String> ids = relationships.stream().map(CateDocRelationship::getFileId).collect(Collectors.toList());
                documents = fileService.listAndFilterByPage(0, 4, ids);
                documents = Optional.ofNullable(documents).orElse(new ArrayList<>(8));
                for (FileDocument document : documents) {
                    document = Optional.ofNullable(document).orElse(new FileDocument());
                    DocVO docVO = new DocVO();
                    docVO.setId(document.getId());
                    docVO.setName(document.getName());
                    docVos.add(docVO);
                }
            }

            trendVO.setDocList(docVos);
            trendVos.add(trendVO);
        }
        return trendVos;
    }

    /**
     * @return com.jiaruiblog.utils.ApiResult
     * @author luojiarui
     * 统计数量
     **/
    @Override
    public StatsVO all() {
        StatsVO statsVO = new StatsVO();
        statsVO.setDocNum(fileService.countAllFile());
        statsVO.setCommentNum(commentService.countAllFile());
        statsVO.setCategoryNum(categoryService.countAllFile());
        statsVO.setTagNum(tagService.countAllFile());
        return statsVO;
    }

    /**
     * @author luojiarui
     * 统计过去一个月每天的数据
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    @Override
    public Map<String, Integer> getMonthStat() {
        // 获取当前日期
        LocalDate currentDate = LocalDate.now();

        // 获取当前月份的第一天
        LocalDate firstDayOfMonth = currentDate.withDayOfMonth(1);
        // 获取当前月份的天数
        int daysInMonth = currentDate.lengthOfMonth();
        // 定义日期格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Map<String, Integer> monthStatResult = new LinkedHashMap<>();

        // 输出当前月份的每一天
        for (int i = 0; i < daysInMonth; i++) {
            LocalDate date = firstDayOfMonth.plusDays(i);
            String formattedDate = date.format(formatter);
            monthStatResult.put(formattedDate, 0);
        }

        // 转换为java.util.Date
        Date startDate = Date.from(firstDayOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
        LocalDate lastDateOfMonth = currentDate.withDayOfMonth(currentDate.lengthOfMonth());
        Date endDate = Date.from(lastDateOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());

        List<MonthStatVO> resultList = documentRepository.xx(startDate, endDate);

        for (MonthStatVO monthStatVO : resultList) {
            monthStatResult.replace(monthStatVO.getDate(), monthStatVO.getCount());
        }

        return monthStatResult;
    }
}
