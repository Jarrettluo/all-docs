package com.jiaruiblog.service;

import com.jiaruiblog.entity.CollectDocRelationship;
import com.jiaruiblog.util.BaseApiResult;

/**
 * @Author Jarrett Luo
 * @Date 2022/6/7 11:39
 * @Version 1.0
 */
public interface CollectService {

    /**
     * 新增文档收藏
     * @param collect -> Collect Doc Relationship
     * @return -> ApiResult
     */
    BaseApiResult insert(CollectDocRelationship collect);

    /**
     * @Author luojiarui
     * @Description 保存点赞/收藏信息到数据库中
     * @Date 13:43 2023/4/5
     * @Param [collect]
     * @return java.lang.Boolean
     **/
    Boolean insertRelationShip(CollectDocRelationship collect);

    /**
     * 移除文档收藏
     * @param collect -> CollectDocRelationship
     * @return -> ApiResult
     */
    BaseApiResult remove(CollectDocRelationship collect);


    void removeRelateByDocId(String docId);

    Long collectNum(String docId);
}
