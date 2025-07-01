package com.jiaruiblog.repository.mongodb;

import com.jiaruiblog.entity.Tag;
import com.jiaruiblog.entity.TagDocRelationship;
import com.jiaruiblog.repository.TagRepository;
import com.mongodb.client.result.UpdateResult;
import jakarta.annotation.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public class TagRepositoryImpl implements TagRepository {

    @Resource
    private MongoTemplate mongoTemplate;

    private static final String TAG_COLLECTION = "tagCollection";
    private static final String RELATION_COLLECTION = "relateTagCollection";

    @Override
    public Tag save(Tag tag) {
        return mongoTemplate.save(tag, TAG_COLLECTION);
    }

    @Override
    public Collection<Tag> saveAll(Collection<Tag> tags) {
        mongoTemplate.insert(tags, TAG_COLLECTION);
        return tags;
    }

    @Override
    public Tag findById(String id) {
        return mongoTemplate.findById(id, Tag.class, TAG_COLLECTION);
    }

    @Override
    public List<Tag> findByIds(List<String> ids) {
        Query query = Query.query(Criteria.where("_id").in(ids));
        return mongoTemplate.find(query, Tag.class, TAG_COLLECTION);
    }

    @Override
    public List<Tag> findByName(String name) {
        Query query = Query.query(Criteria.where("name").is(name));
        return mongoTemplate.find(query, Tag.class, TAG_COLLECTION);
    }

    @Override
    public List<Tag> findByNames(List<String> names) {
        Query query = Query.query(Criteria.where("name").in(names));
        return mongoTemplate.find(query, Tag.class, TAG_COLLECTION);
    }

    @Override
    public long count() {
        return mongoTemplate.getCollection(TAG_COLLECTION).estimatedDocumentCount();
    }

    @Override
    public void delete(Tag tag) {
        mongoTemplate.remove(tag, TAG_COLLECTION);
    }

    @Override
    public UpdateResult update(Query query, Update update) {
        return mongoTemplate.updateFirst(query, update, Tag.class, TAG_COLLECTION);
    }

    @Override
    public TagDocRelationship saveRelationship(TagDocRelationship relationship) {
        return mongoTemplate.save(relationship, RELATION_COLLECTION);
    }

    @Override
    public List<TagDocRelationship> findRelationships(Query query) {
        return mongoTemplate.find(query, TagDocRelationship.class, RELATION_COLLECTION);
    }

    @Override
    public long countRelationships(Query query) {
        return mongoTemplate.count(query, TagDocRelationship.class, RELATION_COLLECTION);
    }

    @Override
    public void deleteRelationships(Query query) {
        mongoTemplate.remove(query, TagDocRelationship.class, RELATION_COLLECTION);
    }

    @Override
    public boolean relationshipExists(Query query) {
        return mongoTemplate.exists(query, TagDocRelationship.class, RELATION_COLLECTION);
    }

    @Override
    public <T> AggregationResults<T> aggregate(Aggregation aggregation, String collectionName, Class<T> outputType) {
        return mongoTemplate.aggregate(aggregation, collectionName, outputType);
    }
}