package com.jiaruiblog.service.impl;

import com.jiaruiblog.entity.Thumbnail;
import com.jiaruiblog.service.ThumbnailService;
import jakarta.annotation.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


/**
 * 缩略图服务实现类
 *
 * @author luojiarui
 * @version 1.0
 */
@Service
public class ThumbnailServiceImpl implements ThumbnailService {

    private static final String THUMB_COLLECTION_NAME = "thumbCollection";

    @Resource
    MongoTemplate mongoTemplate;


    /**
     * 保存缩略图信息
     *
     * @param thumbnail 缩略图对象
     */
    @Override
    public void save(Thumbnail thumbnail) {
        String objectId = thumbnail.getObjectId();
        if (!StringUtils.hasText(objectId) || !StringUtils.hasText(thumbnail.getGridfsId())) {
            return;
        }
        if (searchByObjectId(objectId) != null) {
            this.removeByObjectId(objectId);
        }
        mongoTemplate.save(thumbnail, THUMB_COLLECTION_NAME);
    }

    /**
     * 根据对象ID查询缩略图
     *
     * @param objectId 对象ID
     * @return 缩略图对象
     */
    @Override
    public Thumbnail searchByObjectId(String objectId) {
        if (!StringUtils.hasText(objectId)) {
            return null;
        }
        return mongoTemplate.findById(objectId, Thumbnail.class, THUMB_COLLECTION_NAME);
    }

    /**
     * 根据对象ID删除缩略图
     *
     * @param objectId 对象ID
     */
    @Override
    public void removeByObjectId(String objectId) {
        // 删除掉相关的分类关系
        Query query1 = new Query().addCriteria(Criteria.where("objectId").is(objectId));
        mongoTemplate.remove(query1, Thumbnail.class, THUMB_COLLECTION_NAME);
    }
}
