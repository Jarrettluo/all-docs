package com.jiaruiblog.domain.entity.po;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

/**
 * Elasticsearch 文档实体，用于全文检索索引
 *
 * @ClassName SearchDocument
 * @Description SearchDocument
 * @author luojiarui
 * @Date 2022/7/3 10:47 下午
 * @Version 1.0
 **/
@Slf4j
@Data
@Document(indexName = "all_docs_document_index")
public class SearchDocument {

    /**
     * 文档唯一标识 (使用文件 MD5)
     */
    @Id
    @Field(type = FieldType.Keyword)
    private String id;

    /**
     * 文件名，用于全文检索
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String name;

    /**
     * 文件类型 (pdf, word, txt 等)
     */
    @Field(type = FieldType.Keyword)
    private String type;

    /**
     * 文件提取后的文本内容，用于全文检索
     * 注意：存储原始文本内容，不做 base64 编码
     */
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String content;

    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private List<String> tagNames;

    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String categoryName;
}
