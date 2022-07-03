package com.jiaruiblog.repository;

import com.jiaruiblog.entity.FileObj;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ESFileObjRepository extends ElasticsearchRepository<FileObj, String> {

//    @Query("{\"match\":{\"attachment.content\":\"?0\"}}")
//    SearchHits<FileObj> find(String keyword);

}
