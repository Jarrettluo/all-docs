package com.jiaruiblog.infrastructure.repository.mysql;

import com.jiaruiblog.domain.entity.po.Thumbnail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ThumbnailMapper {

    void save(Thumbnail thumbnail);

    Thumbnail findByObjectId(@Param("objectId") String objectId);

    List<Thumbnail> findAllByObjectId(@Param("objectId") String objectId);

    void deleteByObjectId(@Param("objectId") String objectId);
}