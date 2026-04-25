package com.jiaruiblog.infrastructure.repository.mysql;

import com.jiaruiblog.domain.entity.po.DocLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DocLogMapper {

    DocLog save(DocLog docLog);

    DocLog findById(@Param("id") String id);

    List<DocLog> findByDocId(@Param("docId") String docId);

    List<DocLog> findByUserId(@Param("userId") String userId);

    List<DocLog> findByAction(@Param("action") String action);

    long count();

    void deleteById(@Param("id") String id);

    void deleteAllByIdIn(@Param("idList") List<String> idList);
}