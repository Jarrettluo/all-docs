package com.jiaruiblog.service;

import com.jiaruiblog.entity.Thumbnail;

import java.util.List;

/**
 * @ClassName ThumbnailService
 * @Description ThumbnailService
 * @author luojiarui
 * @Date 2022/7/23 6:02 下午
 * @Version 1.0
 **/
public interface ThumbnailService {

    /**
     * 保存缩略图信息
     * @param thumbnail 缩略图
     */
    void save(Thumbnail thumbnail);

    /**
     * 批量保存缩略图信息
     * @param thumbnails 缩略图列表
     */
    void saveBatch(List<Thumbnail> thumbnails);

    /**
     * 通过对象的id进行查询
     * @param objectId 对象id
     * @return Thumbnail 缩略图
     */
    Thumbnail searchByObjectId(String objectId);

    /**
     * 通过对象的id查询所有缩略图
     * @param objectId 对象id
     * @return List<Thumbnail> 缩略图列表
     */
    List<Thumbnail> searchAllByObjectId(String objectId);

    /**
     * 根据对象ID和缩略图类型查询
     * @param objectId 对象ID
     * @param thumbnailEnum 缩略图类型
     * @return 缩略图对象
     */
    Thumbnail searchByObjectIdAndType(String objectId, String thumbnailEnum);

    /**
     * 根据对象ID和尺寸查询
     * @param objectId 对象ID
     * @param thumbSizeEnum 缩略图尺寸
     * @return 缩略图对象
     */
    Thumbnail searchByObjectIdAndSize(String objectId, String thumbSizeEnum);

    /**
     * 通过对象的id进行删除
     * @param objectId 对象id
     */
    void removeByObjectId(String objectId);

}
