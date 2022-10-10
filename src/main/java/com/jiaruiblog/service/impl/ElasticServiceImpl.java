package com.jiaruiblog.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Sets;
import com.jiaruiblog.entity.FileDocument;
import com.jiaruiblog.entity.FileObj;
import com.jiaruiblog.service.ElasticService;
import com.jiaruiblog.util.MsExcelParse;
import com.jiaruiblog.util.PdfUtil;
import org.apache.commons.compress.utils.Lists;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;

/**
 * @ClassName ElasticServiceImpl
 * @Description ElasticServiceImpl
 * @Author luojiarui
 * @Date 2022/7/12 10:54 下午
 * @Version 1.0
 **/
@Service
public class ElasticServiceImpl implements ElasticService {

    private static final String INDEX_NAME = "docwrite";

    private static final String PIPELINE_NAME = "attachment.content";

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private FileOperationServiceImpl fileOperationServiceImpl;

    @Autowired
    private FileServiceImpl fileServiceImpl;


    /**
     * 有三种类型
     * 1.文件的名字
     * 2.文件type
     * 3.文件的data 64编码
     */
    public void upload(FileObj file) throws IOException {
        IndexRequest indexRequest = new IndexRequest(INDEX_NAME);
        //上传同时，使用attachment pipline进行提取文件
        indexRequest.source(JSON.toJSONString(file), XContentType.JSON);
        indexRequest.setPipeline("attachment");
        client.index(indexRequest, RequestOptions.DEFAULT);
    }



    /**
     * 根据关键词，搜索对应的文件信息
     * 查询文件中的文本内容
     * 默认会search出所有的东西来
     * SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
     *
     * // srb.query(QueryBuilders.matchQuery("attachment.content", keyword).analyzer("ik_smart"));
     * @param keyword String
     * @throws IOException ioexception
     * @return list
     */
    @Override
    public List<FileDocument> search(String keyword) throws IOException {

        List<FileDocument> fileDocumentList = new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
        // 使用lk分词器查询，会把插入的字段分词，然后进行处理
        SearchSourceBuilder srb = new SearchSourceBuilder();
        srb.query(QueryBuilders.matchQuery(PIPELINE_NAME, keyword));

        // 每页10个数据
        srb.size(10);
        // 起始位置从0开始
        srb.from(0);

        //设置highlighting
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        HighlightBuilder.Field highlightContent = new HighlightBuilder.Field(PIPELINE_NAME);
        highlightContent.highlighterType();
        highlightBuilder.field(highlightContent);
        highlightBuilder.preTags("<em>");
        highlightBuilder.postTags("</em>");

        //highlighting会自动返回匹配到的文本，所以就不需要再次返回文本了
        String[] includeFields = new String[]{"name", "id"};
        String[] excludeFields = new String[]{PIPELINE_NAME};
        srb.fetchSource(includeFields, excludeFields);

        //把刚才设置的值导入进去
        srb.highlighter(highlightBuilder);
        searchRequest.source(srb);
        SearchResponse res = client.search(searchRequest, RequestOptions.DEFAULT);

        if ( res== null || res.getHits() == null ) {
            return Lists.newArrayList();
        }
        //获取hits，这样就可以获取查询到的记录了
        SearchHits hits = res.getHits();

        //hits是一个迭代器，所以需要迭代返回每一个hits
        Iterator<SearchHit> iterator = hits.iterator();
        int count = 0;

        StringBuilder stringBuilder = new StringBuilder();

        Set<String> idSet = Sets.newHashSet();

        while (iterator.hasNext()) {
            SearchHit hit = iterator.next();

            //获取返回的字段
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();

            //统计找到了几条
            count++;

            //这个就会把匹配到的文本返回，而且只返回匹配到的部分文本
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();

            HighlightField highlightField = highlightFields.get(PIPELINE_NAME);

            StringBuilder stringBuilder1 = new StringBuilder();
            for (Text fragment : highlightField.getFragments()) {
                stringBuilder1.append(fragment.toString());
            }
            String abstractString = stringBuilder1.toString();
            if(abstractString.length() > 500) {
                abstractString = abstractString.substring(0, 500);
            }

            if(sourceAsMap.containsKey("id")){
                String id = (String) sourceAsMap.get("id");
                if ( id != null && !idSet.contains(id)) {
                    idSet.add(id);
                    FileDocument fileDocument = fileServiceImpl.getByMd5(id);
                    if ( fileDocument == null ) {
                        //从redis中剔除该doc，并跳过循环
                        continue;
                    }
                    fileDocument.setDescription(abstractString);
                    fileDocumentList.add(fileDocument);
                }
            }

            stringBuilder.append(highlightFields);
        }

        stringBuilder.append("查询到" + count + "条记录");
        return fileDocumentList;
    }

    @Async
    @Override
    public void uploadFileToEs(InputStream is, FileDocument fileDocument) {

        String textFilePath = fileDocument.getMd5() + fileDocument.getName() + ".txt";

        try {
            PdfUtil.readPdfText(is, textFilePath);
            FileObj fileObj = fileOperationServiceImpl.readFile(textFilePath);
            fileObj.setId(fileDocument.getMd5());
            fileObj.setName(fileDocument.getName());
            fileObj.setType(fileDocument.getContentType());
            this.upload(fileObj);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 删除临时的txt文件
            File file = new File(textFilePath);
            if(file.exists()) {
                file.deleteOnExit();
            }
        }
    }

    /**
     * @Author luojiarui
     * @Description // 转换各类office文档到es中
     * @Date 23:00 2022/8/28
     * @Param [is, fileDocument]
     * @return void
     **/
    @Override
    public void uploadFileToEsDocx(InputStream is, FileDocument fileDocument) {
        String textFilePath = fileDocument.getMd5() + fileDocument.getName() + ".txt";
        try {
            MsExcelParse.readPdfText(is, textFilePath);
            FileObj fileObj = fileOperationServiceImpl.readFile(textFilePath);
            fileObj.setId(fileDocument.getMd5());
            fileObj.setName(fileDocument.getName());
            fileObj.setType(fileDocument.getContentType());
            this.upload(fileObj);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 删除临时的txt文件
            File file = new File(textFilePath);
            if(file.exists()) {
                file.deleteOnExit();
            }
        }
    }

}
