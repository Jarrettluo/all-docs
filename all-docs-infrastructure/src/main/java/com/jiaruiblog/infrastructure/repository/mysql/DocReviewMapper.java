package com.jiaruiblog.infrastructure.repository.mysql;

import com.jiaruiblog.domain.entity.po.DocReview;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DocReviewMapper {

    int save(DocReview docReview);

    void saveAll(@Param("list") List<DocReview> docReviews);

    long countByUserId(@Param("userId") String userId);

    long countByUserIdWithAdmin(@Param("userId") String userId, @Param("isAdmin") boolean isAdmin);

    List<DocReview> findByPage(@Param("offset") int offset, @Param("limit") int limit, @Param("userId") String userId);

    List<DocReview> findByPageWithAdmin(@Param("offset") int offset, @Param("limit") int limit, @Param("userId") String userId, @Param("isAdmin") boolean isAdmin);

    long deleteByQuery();

    void deleteByIdList(@Param("idList") List<String> idList);

    boolean existsByDocIdIn(@Param("docIdList") List<String> docIdList);
}