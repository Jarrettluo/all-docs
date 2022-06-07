package com.jiaruiblog.service;

import com.jiaruiblog.entity.CollectDocRelationship;
import com.jiaruiblog.entity.Tag;
import com.jiaruiblog.utils.ApiResult;

/**
 * @Author Jarrett Luo
 * @Date 2022/6/7 11:39
 * @Version 1.0
 */
public interface CollectService {

    /**
     * 新增文档收藏
     * @param collect -> ColllectDocRelationship
     * @return -> ApiResult
     */
    ApiResult insert(CollectDocRelationship collect);

    /**
     * 移除文档收藏
     * @param collect -> ColllectDocRelationship
     * @return -> ApiResult
     */
    ApiResult remove(CollectDocRelationship collect);
}
