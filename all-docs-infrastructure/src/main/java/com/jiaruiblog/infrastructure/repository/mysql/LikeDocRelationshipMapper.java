package com.jiaruiblog.infrastructure.repository.mysql;

import com.jiaruiblog.domain.entity.po.LikeDocRelationship;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * LikeDocRelationship Mapper
 */
@Mapper
public interface LikeDocRelationshipMapper {

    void save(LikeDocRelationship like);

    LikeDocRelationship findById(@Param("id") String id);

    List<LikeDocRelationship> findByUserId(@Param("userId") String userId);

    List<LikeDocRelationship> findByEntityId(@Param("entityId") String entityId);

    List<LikeDocRelationship> findByEntityType(@Param("entityType") Integer entityType);

    List<LikeDocRelationship> findByUserIdAndEntityType(@Param("userId") String userId, @Param("entityType") Integer entityType);

    long count();

    long countByEntityId(@Param("entityId") String entityId);

    void deleteById(@Param("id") String id);

    void deleteByUserId(@Param("userId") String userId);

    void deleteByEntityId(@Param("entityId") String entityId);

    boolean exists(@Param("userId") String userId, @Param("entityType") Integer entityType, @Param("entityId") String entityId);
}
