package com.jiaruiblog.service;

import com.jiaruiblog.entity.Thumbnail;
import com.jiaruiblog.enums.ThumbnailEnum;

/**
 * @ClassName ThumbnailService
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/7/23 6:02 下午
 * @Version 1.0
 **/
public interface ThumbnailService {

    // 保存缩略图信息
    void save(Thumbnail thumbnail);

    // 通过对象的id进行查询
    Thumbnail searchByObjectId(String objectId);

    // 通过对象的id进行删除
    void removeByObjectId(String objectId);

}
