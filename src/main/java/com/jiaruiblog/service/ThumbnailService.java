package com.jiaruiblog.service;

import com.jiaruiblog.entity.Thumbnail;

/**
 * @ClassName ThumbnailService
 * @Description ThumbnailService
 * @Author luojiarui
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
     * 通过对象的id进行查询
     * @param objectId 对象id
     * @return Thumbnail 缩略图
     */
    Thumbnail searchByObjectId(String objectId);

    /**
     * 通过对象的id进行删除
     * @param objectId 对象id
     */
    void removeByObjectId(String objectId);

}
