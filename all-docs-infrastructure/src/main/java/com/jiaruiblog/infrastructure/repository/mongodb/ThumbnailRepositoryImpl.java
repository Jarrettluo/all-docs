package com.jiaruiblog.infrastructure.repository.mongodb;

import com.jiaruiblog.config.datasource.DataSourceCondition;
import com.jiaruiblog.domain.entity.Thumbnail;
import com.jiaruiblog.infrastructure.repository.ThumbnailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 缩略图数据访问实现类
 *
 * @author luojiarui
 * @version 1.0
 */
@Repository
@Conditional(DataSourceCondition.MongoDBCondition.class)
public class ThumbnailRepositoryImpl implements ThumbnailRepository {

    private static final String COLLECTION_NAME = "thumbCollection";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void save(Thumbnail thumbnail) {
        mongoTemplate.save(thumbnail, COLLECTION_NAME);
    }

    @Override
    public Thumbnail findByObjectId(String objectId) {
        return mongoTemplate.findById(objectId, Thumbnail.class, COLLECTION_NAME);
    }

    @Override
    public List<Thumbnail> findAllByObjectId(String objectId) {
        Query query = new Query().addCriteria(Criteria.where("objectId").is(objectId));
        return mongoTemplate.find(query, Thumbnail.class, COLLECTION_NAME);
    }

    @Override
    public void deleteByObjectId(String objectId) {
        Query query = new Query().addCriteria(Criteria.where("objectId").is(objectId));
        mongoTemplate.remove(query, Thumbnail.class, COLLECTION_NAME);
    }

    @Override
    public Thumbnail findByObjectIdAndType(String objectId, String thumbnailEnum) {
        Query query = new Query()
                .addCriteria(Criteria.where("objectId").is(objectId))
                .addCriteria(Criteria.where("thumbnailEnum").is(thumbnailEnum));
        return mongoTemplate.findOne(query, Thumbnail.class, COLLECTION_NAME);
    }

    @Override
    public Thumbnail findByObjectIdAndSize(String objectId, String thumbSizeEnum) {
        Query query = new Query()
                .addCriteria(Criteria.where("objectId").is(objectId))
                .addCriteria(Criteria.where("thumbSizeEnum").is(thumbSizeEnum));
        return mongoTemplate.findOne(query, Thumbnail.class, COLLECTION_NAME);
    }
}