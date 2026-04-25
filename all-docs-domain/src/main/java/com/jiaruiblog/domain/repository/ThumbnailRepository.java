package com.jiaruiblog.domain.repository;

import com.jiaruiblog.domain.entity.Thumbnail;

import java.util.List;

/**
 * @ClassName ThumbnailRepository
 * @Description 缩略图仓储接口
 * @author luojiarui
 * @Date 2022/6/4 10:30 上午
 * @Version 1.0
 **/
public interface ThumbnailRepository {

    /**
     * 保存缩略图
     * @param thumbnail 缩略图实体
     */
    void save(Thumbnail thumbnail);

    /**
     * 根据关联对象ID查询缩略图
     * @param objectId 关联对象ID
     * @return 缩略图实体
     */
    Thumbnail findByObjectId(String objectId);

    /**
     * 根据关联对象ID查询所有缩略图
     * @param objectId 关联对象ID
     * @return 缩略图列表
     */
    List<Thumbnail> findAllByObjectId(String objectId);

    /**
     * 根据关联对象ID删除缩略图
     * @param objectId 关联对象ID
     */
    void deleteByObjectId(String objectId);
}