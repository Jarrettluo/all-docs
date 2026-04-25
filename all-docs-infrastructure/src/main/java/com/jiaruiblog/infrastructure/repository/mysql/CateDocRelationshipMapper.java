package com.jiaruiblog.infrastructure.repository.mysql;

import com.jiaruiblog.domain.entity.po.CateDocRelationship;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * CateDocRelationship Mapper
 */
@Mapper
public interface CateDocRelationshipMapper {

    void save(CateDocRelationship relationship);

    void delete(CateDocRelationship relationship);

    void deleteById(@Param("id") String id);

    void deleteByCategoryId(@Param("categoryId") String categoryId);

    void deleteByFileId(@Param("fileId") String fileId);

    void deleteByIds(@Param("ids") List<String> ids);

    List<CateDocRelationship> findAll();

    List<CateDocRelationship> findByCategoryId(@Param("categoryId") String categoryId);

    List<CateDocRelationship> findByFileId(@Param("fileId") String fileId);

    List<CateDocRelationship> findByCategoryIdAndFileId(@Param("categoryId") String categoryId, @Param("fileId") String fileId);

    List<CateDocRelationship> findByCategoryIds(@Param("categoryIds") List<String> categoryIds);

    List<CateDocRelationship> findByFileIds(@Param("fileIds") List<String> fileIds);

    long count();

    long countByCategoryId(@Param("categoryId") String categoryId);

    long countByFileId(@Param("fileId") String fileId);

    boolean exists(@Param("categoryId") String categoryId, @Param("fileId") String fileId);

    List<String> findFileIdsByCategoryId(@Param("categoryId") String categoryId);

    List<String> findFileIdsByCategoryIds(@Param("categoryIds") List<String> categoryIds);

    List<String> findCategoryIdsByFileId(@Param("fileId") String fileId);
}
