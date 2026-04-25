package com.jiaruiblog.infrastructure.repository.mysql;

import com.jiaruiblog.domain.entity.po.TagDocRelationship;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * TagDocRelationship Mapper
 */
@Mapper
public interface TagDocRelationshipMapper {

    void save(TagDocRelationship relationship);

    void saveAll(@Param("relationships") List<TagDocRelationship> relationships);

    void delete(TagDocRelationship relationship);

    void deleteById(@Param("id") String id);

    void deleteByTagId(@Param("tagId") String tagId);

    void deleteByFileId(@Param("fileId") String fileId);

    void deleteByIds(@Param("ids") List<String> ids);

    List<TagDocRelationship> findAll();

    List<TagDocRelationship> findByTagId(@Param("tagId") String tagId);

    List<TagDocRelationship> findByFileId(@Param("fileId") String fileId);

    List<TagDocRelationship> findByTagIdAndFileId(@Param("tagId") String tagId, @Param("fileId") String fileId);

    List<TagDocRelationship> findByTagIds(@Param("tagIds") List<String> tagIds);

    List<TagDocRelationship> findByFileIds(@Param("fileIds") List<String> fileIds);

    long count();

    long countByTagId(@Param("tagId") String tagId);

    boolean exists(@Param("tagId") String tagId, @Param("fileId") String fileId);

    List<String> findFileIdsByTagId(@Param("tagId") String tagId);

    List<String> findFileIdsByTagIds(@Param("tagIds") List<String> tagIds);
}
