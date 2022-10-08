package com.jiaruiblog.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import java.util.Date;

/**
 * @ClassName Book
 * @Description Book
 * @Author luojiarui
 * @Date 2022/6/6 10:58 下午
 * @Version 1.0
 **/
@Data
@Document(indexName = "book",createIndex = true)
public class Book {

    @Id
    @Field(type = FieldType.Text)
    private String id;

    @Field(analyzer="ik_max_word")
    private String title;

    @Field(analyzer="ik_max_word")
    private String author;

    @Field(type = FieldType.Double)
    private Double price;

    @Field(type = FieldType.Date,format = DateFormat.basic_date_time)
    private Date createTime;

    @Field(type = FieldType.Date,format = DateFormat.basic_date_time)
    private Date updateTime;

}
