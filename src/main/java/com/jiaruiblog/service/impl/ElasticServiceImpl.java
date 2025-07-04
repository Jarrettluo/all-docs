package com.jiaruiblog.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.jiaruiblog.entity.FileDocument;
import com.jiaruiblog.entity.FileObj;
import com.jiaruiblog.entity.data.WordCloudItem;
import com.jiaruiblog.entity.vo.PageVO;
import com.jiaruiblog.service.ElasticService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Lazy
@Service
public class ElasticServiceImpl implements ElasticService {

    private static final String INDEX_NAME = "docwrite";
    private static final String PIPELINE_NAME = "attachment.content";

    @Autowired
    private ElasticsearchClient client;

    private DocumentServiceImpl documentServiceImpl;

    @Autowired
    public void setFileServiceImpl(DocumentServiceImpl fileService) {
        this.documentServiceImpl = fileService;
    }

    @Override
    public void upload(FileObj file) throws IOException {
        if (file == null) throw new IllegalArgumentException("文件对象不能为空");
        client.index(i -> i
            .index(INDEX_NAME)
            .document(file)
            .pipeline("attachment")
        );
    }

    @Override
    public List<FileDocument> search(String keyword) throws IOException {
        SearchResponse<FileDocument> response = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q.match(m -> m.field(PIPELINE_NAME).query(keyword)))
            .highlight(h -> h
                .fields(PIPELINE_NAME, f -> f
                    .preTags("<em>")
                    .postTags("</em>")
                )
            )
            .source(sc -> sc
                .filter(f -> f
                    .includes("name", "id")
                    .excludes(PIPELINE_NAME)
                )
            )
            .size(10)
            .from(0),
            FileDocument.class
        );

        return response.hits().hits().stream()
            .map(hit -> {
                FileDocument doc = documentServiceImpl.getByMd5(hit.source().getId());
                if (doc != null) {
                    doc.setDescription(getHighlightContent(hit.highlight()));
                }
                return doc;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
    @Override
    public Map<String, List<PageVO>> search(String keyword, Set<String> docIdSet) throws IOException {
        SearchResponse<Map> response = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q.matchPhrase(m -> m.field(PIPELINE_NAME).query(keyword)))
            .highlight(h -> h
                .fields(PIPELINE_NAME, f -> f
                    .preTags("<em>")
                    .postTags("</em>")
                    .fragmentSize(100)
                    .numberOfFragments(100)
                )
            )
            .source(sc -> sc
                .filter(f -> f
                    .includes("name", "id")
                    .excludes(PIPELINE_NAME)
                )
            )
            .size(10)
            .from(0),
            Map.class
        );

    AtomicInteger counter = new AtomicInteger(0);
//        return response.hits().hits().stream()
//            .collect(Collectors.toMap(
//                hit -> (String) hit.source().get("id"),
//                hit -> hit.highlight().get(PIPELINE_NAME).stream()
////                .map(text -> {
//                    // ToDO 待修改
////                    PageVO pageVO = new PageVO();
////                    pageVO.setOrder(counter.getAndIncrement());
////                    pageVO.setContent(text);
////                    return pageVO;
////                })
//                .collect(Collectors.toList())
//            ));

        return new HashMap<>();
    }

    @Override
    public void removeByDocId(String docMd5) {
        if (!StringUtils.hasText(docMd5)) return;
        try {
            client.delete(d -> d.index(INDEX_NAME).id(docMd5));
        } catch (Exception e) {
            log.error("删除文档失败", e);
        }
    }

    @Override
    public List<WordCloudItem> getWordStat() throws IOException {
        SearchResponse<Void> response = client.search(s -> s
            .index(INDEX_NAME)
            .query(Query.of(q -> q.matchAll(m -> m)))
            .aggregations("word_cloud", a -> a
                .terms(t -> t.field(PIPELINE_NAME).size(100))
            ),
            Void.class
        );

        List<WordCloudItem> wordCloudItems = response.aggregations()
            .get("word_cloud")
            .sterms()
            .buckets()
            .array()
            .stream()
            .map(b -> new WordCloudItem(b.key().stringValue(), b.docCount()))
            .sorted(Comparator.comparingLong(WordCloudItem::getCount).reversed())
            .collect(Collectors.toList());
        return wordCloudItems;
    }

    private String getHighlightContent(Map<String, List<String>> highlight) {
        return highlight.getOrDefault(PIPELINE_NAME, Collections.emptyList()).stream()
            .map(fragment -> "📖 " + fragment)
            .collect(Collectors.joining("<br/>"));
    }
}
