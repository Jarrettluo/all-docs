package com.jiaruiblog.infrastructure.repository.mysql;

import com.jiaruiblog.domain.entity.po.Thumbnail;
import com.jiaruiblog.infrastructure.repository.ThumbnailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MyBatis Thumbnail Repository Implementation
 *
 * @author luojiarui
 */
@Repository
public class ThumbnailMybatisRepository implements ThumbnailRepository {

    @Autowired
    private ThumbnailMapper thumbnailMapper;

    @Override
    public void save(Thumbnail thumbnail) {
        thumbnailMapper.save(thumbnail);
    }

    @Override
    public Thumbnail findByObjectId(String objectId) {
        return thumbnailMapper.findByObjectId(objectId);
    }

    @Override
    public List<Thumbnail> findAllByObjectId(String objectId) {
        return thumbnailMapper.findAllByObjectId(objectId);
    }

    @Override
    public void deleteByObjectId(String objectId) {
        thumbnailMapper.deleteByObjectId(objectId);
    }

    @Override
    public Thumbnail findByObjectIdAndType(String objectId, String thumbnailEnum) {
        return thumbnailMapper.findByObjectIdAndType(objectId, thumbnailEnum);
    }

    @Override
    public Thumbnail findByObjectIdAndSize(String objectId, String thumbSizeEnum) {
        return thumbnailMapper.findByObjectIdAndSize(objectId, thumbSizeEnum);
    }
}
