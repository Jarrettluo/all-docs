package com.jiaruiblog.service.impl;

import com.jiaruiblog.entity.CateDocRelationship;
import com.jiaruiblog.entity.Category;
import com.jiaruiblog.entity.FileDocument;
import com.jiaruiblog.entity.vo.DocVO;
import com.jiaruiblog.entity.vo.MonthStatVO;
import com.jiaruiblog.entity.vo.StatsVO;
import com.jiaruiblog.entity.vo.TrendVO;
import com.jiaruiblog.service.*;
import com.jiaruiblog.util.BaseApiResult;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName StatisticsServiceImpl
 * @Description StatisticsServiceImpl
 * @Author luojiarui
 * @Date 2022/6/26 2:28 下午
 * @Version 1.0
 **/
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Resource
    CategoryService categoryService;

    @Resource
    IFileService fileService;

    @Resource
    TagService tagService;

    @Resource
    ICommentService commentService;

    @Resource
    private MongoTemplate mongoTemplate;


    /**
     * @return com.jiaruiblog.utils.ApiResult
     * @Author luojiarui
     * @Description // 统计随机的三个分类
     * @Date 2:29 下午 2022/6/26
     * @Param []
     **/
    @Override
    public BaseApiResult trend() {
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
        return BaseApiResult.success(trendVos);
    }

    /**
     * @return com.jiaruiblog.utils.ApiResult
     * @Author luojiarui
     * @Description // 统计数量
     * @Date 2:29 下午 2022/6/26
     * @Param []
     **/
    @Override
    public BaseApiResult all() {
        StatsVO statsVO = new StatsVO();
        statsVO.setDocNum(fileService.countAllFile());
        statsVO.setCommentNum(commentService.countAllFile());
        statsVO.setCategoryNum(categoryService.countAllFile());
        statsVO.setTagNum(tagService.countAllFile());
        return BaseApiResult.success(statsVO);
    }

    /**
     * @Author luojiarui
     * @Description 统计过去一个月每天的数据
     * @Date 17:13 2023/5/20
     * @Param []
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    @Override
    public BaseApiResult getMonthStat() {
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

        Aggregation aggregation = Aggregation.newAggregation(
                // 使用$match操作符筛选出在过去一个月内的文档
                Aggregation.match(Criteria.where("uploadDate").gte(startDate).lte(endDate)),
                // 使用$project操作符提取日期字段的年、月、日部分，并合并为日期字符串字段
                Aggregation.project()
                        .andExpression("dateToString('%Y-%m-%d', uploadDate)").as("date"),
                // 使用$group操作符按日期分组，并计算每天的统计数据
                Aggregation.group("date").count().as("count"),
                // 使用$project操作符进行投影和重命名字段
                // 使用$group操作符进行分组时，默认会将分组字段的结果存储在_id字段中，无法直接将其命名为其他字段名称
                Aggregation.project("count")
                        .and("$_id").as("date"),
                // 使用$sort操作符按日期排序
                Aggregation.sort(Sort.Direction.ASC, "date")
        );

        // 执行聚合操作并获取结果
        AggregationResults<MonthStatVO> results = mongoTemplate.aggregate(aggregation,
                FileServiceImpl.COLLECTION_NAME,
                MonthStatVO.class);
        List<MonthStatVO> resultList = results.getMappedResults();

        for (MonthStatVO monthStatVO : resultList) {
            monthStatResult.replace(monthStatVO.getDate(), monthStatVO.getCount());
        }

        return BaseApiResult.success(monthStatResult);
    }
}
