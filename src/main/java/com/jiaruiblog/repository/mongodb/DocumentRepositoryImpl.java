package com.jiaruiblog.repository.mongodb;

import com.jiaruiblog.entity.FileDocument;
import com.jiaruiblog.entity.vo.MonthStatVO;
import com.jiaruiblog.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Field;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * <p></p>
 * edit at 2025/7/4 20:56
 *
 * @author Jarrett Luo
 * @version 1.0
 */
public class DocumentRepositoryImpl implements DocumentRepository {

    public static final String COLLECTION_NAME = "fileDatas";

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public void save(FileDocument fileDocument) {
        mongoTemplate.save(fileDocument, COLLECTION_NAME);
    }

    @Override
    public void update(FileDocument fileDocument) {
        Query query = new Query(Criteria.where("_id").is(fileDocument.getId()));
        Update update = new Update();
        update.set("textFileId", fileDocument.getTextFileId());
        update.set("thumbId", fileDocument.getThumbId());
        update.set("previewFileId", fileDocument.getPreviewFileId());
        update.set("description", fileDocument.getDescription());
        mongoTemplate.updateFirst(query, update, FileDocument.class, COLLECTION_NAME);

    }

    @Override
    public long count() {
        Query query = new Query().addCriteria(Criteria.where("reviewing").is(false));
        return mongoTemplate.count(query, COLLECTION_NAME);
    }

    @Override
    public FileDocument findById(String fileDocumentId) {
        Query query = new Query().addCriteria(Criteria.where("_id").is(fileDocumentId));
        return mongoTemplate.findOne(query, FileDocument.class, COLLECTION_NAME);
    }

    @Override
    public List<FileDocument> findByIdList(List<String> docIdList) {
        Query query = new Query().addCriteria(Criteria.where("_id").in(docIdList));
        return mongoTemplate.find(query, FileDocument.class, COLLECTION_NAME);
    }

    @Override
    public FileDocument findByMd5(String md5) {
        Query query = new Query().addCriteria(Criteria.where("md5").is(md5));
        return mongoTemplate.findOne(query, FileDocument.class, COLLECTION_NAME);
    }

    @Override
    public List<FileDocument> findByPage(Integer pageNum, Integer pageSize, Sort sort) {
        Query query = new Query().addCriteria(Criteria.where("reviewing").is(false));
        long skip = (long) (pageNum) * pageSize;
        query.skip(skip);
        query.limit(pageSize);
        Field field = query.fields();
        field.exclude("content");
        return mongoTemplate.find(query, FileDocument.class, COLLECTION_NAME);
    }

    @Override
    public List<FileDocument> findByPageWithFussySearch(Integer pageNum, Integer pageSize, Sort sort, String keyWord) {

        Pattern pattern = Pattern.compile("^.*" + keyWord + ".*$", Pattern.CASE_INSENSITIVE);
        Query query = new Query();
        query.addCriteria(Criteria.where("name").regex(pattern));
        query.addCriteria(Criteria.where("reviewing").is(false));
        
        long skip = (long) (pageNum) * pageSize;
        query.skip(skip);
        query.limit(pageSize);
        Field field = query.fields();
        field.exclude("content");

        return mongoTemplate.find(query, FileDocument.class, COLLECTION_NAME);
    }

    @Override
    public boolean delete(String fileDocumentId) {

        Query query = new Query().addCriteria(Criteria.where("_id").is(fileDocumentId));
        mongoTemplate.remove(query, COLLECTION_NAME);
        return true;
    }

    @Override
    public boolean deleteByIdList(List<String> idList) {
        Query query = new Query().addCriteria(Criteria.where("_id").in(idList));
        mongoTemplate.remove(query, COLLECTION_NAME);
        return true;
    }

    public List<MonthStatVO> stats(Date startDate, Date endDate) {
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
                COLLECTION_NAME,
                MonthStatVO.class);
        return results.getMappedResults();
    }
}
