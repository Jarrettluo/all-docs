package com.jiaruiblog.repository.mongodb;

import com.jiaruiblog.entity.vo.MonthStatVO;
import com.jiaruiblog.repository.DocumentRepository;
import com.jiaruiblog.service.impl.DocumentServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.Date;
import java.util.List;

/**
 * <p></p>
 * edit at 2025/7/4 20:56
 *
 * @author Jarrett Luo
 * @version 1.0
 */
public class DocumentRepositoryImpl implements DocumentRepository {

    @Autowired
    MongoTemplate mongoTemplate;

    public List<MonthStatVO> xx(Date startDate, Date endDate) {
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
                DocumentServiceImpl.COLLECTION_NAME,
                MonthStatVO.class);
        return results.getMappedResults();
    }
}
