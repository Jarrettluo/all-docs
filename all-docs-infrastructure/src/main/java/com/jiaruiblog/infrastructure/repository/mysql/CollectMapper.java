package com.jiaruiblog.infrastructure.repository.mysql;

import com.jiaruiblog.domain.entity.CollectDocRelationship;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CollectMapper {

    void save(CollectDocRelationship collect);

    void remove(CollectDocRelationship collect);

    CollectDocRelationship findByDocIdAndUserId(@Param("docId") String docId, @Param("userId") String userId);

    long countByDocId(@Param("docId") String docId);

    List<CollectDocRelationship> findAllByDocId(@Param("docId") String docId);

    List<CollectDocRelationship> findByUserId(@Param("userId") String userId);
}