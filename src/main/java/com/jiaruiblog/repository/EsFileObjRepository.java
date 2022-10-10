package com.jiaruiblog.repository;

import com.jiaruiblog.entity.FileObj;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * // 另外一种方式
 * //    @Query("{\"match\":{\"attachment.content\":\"?0\"}}")
 * //    SearchHits<FileObj> find(String keyword);
 * @author jiarui.luo
 */
public interface EsFileObjRepository extends ElasticsearchRepository<FileObj, String> {


}
