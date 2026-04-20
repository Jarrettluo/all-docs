package com.jiaruiblog.infrastructure.repository;

import com.jiaruiblog.domain.entity.Thumbnail;

import java.util.List;

/**
 * 缩略图数据访问接口
 *
 * @author luojiarui
 * @version 1.0
 */
public interface ThumbnailRepository {

    /**
     * 保存缩略图
     *
     * @param thumbnail 缩略图对象
     */
    void save(Thumbnail thumbnail);

    /**
     * 根据对象ID查询缩略图
     *
     * @param objectId 对象ID
     * @return 缩略图对象
     */
    Thumbnail findByObjectId(String objectId);

    /**
     * 根据对象ID查询所有缩略图
     *
     * @param objectId 对象ID
     * @return 缩略图列表
     */
    List<Thumbnail> findAllByObjectId(String objectId);

    /**
     * 根据对象ID删除缩略图
     *
     * @param objectId 对象ID
     */
    void deleteByObjectId(String objectId);

    /**
     * 根据对象ID和缩略图类型查询
     *
     * @param objectId 对象ID
     * @param thumbnailEnum 缩略图类型
     * @return 缩略图对象
     */
    Thumbnail findByObjectIdAndType(String objectId, String thumbnailEnum);

    /**
     * 根据对象ID和尺寸查询
     *
     * @param objectId 对象ID
     * @param thumbSizeEnum 缩略图尺寸
     * @return 缩略图对象
     */
    Thumbnail findByObjectIdAndSize(String objectId, String thumbSizeEnum);
}