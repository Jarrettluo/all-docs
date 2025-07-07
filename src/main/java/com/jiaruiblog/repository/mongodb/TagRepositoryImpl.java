package com.jiaruiblog.repository.mongodb;

import com.jiaruiblog.entity.Tag;
import com.jiaruiblog.entity.TagDocRelationship;
import com.jiaruiblog.repository.TagRepository;
import com.mongodb.client.result.UpdateResult;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

@Repository
public class TagRepositoryImpl implements TagRepository {

    @Resource
    private MongoTemplate mongoTemplate;

    private static final String TAG_ID = "tagId";
    private static final String FILE_ID = "fileId";

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
    public void update(Tag tag) {
        Query query = new Query(Criteria.where("_id").is(tag.getId()));
        Update update = new Update();
        update.set("name", tag.getName());
        update.set("updateDate", tag.getUpdateDate());
        mongoTemplate.updateFirst(query, update, TAG_COLLECTION);
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
    public List<TagDocRelationship> findRelationships() {
        return mongoTemplate.find(new Query(), TagDocRelationship.class, RELATION_COLLECTION);
    }

    @Override
    public List<TagDocRelationship> findRelationshipsByDocId(String docId) {
        Query query = new Query().addCriteria(Criteria.where(FILE_ID).is(docId));
        return mongoTemplate.find(query, TagDocRelationship.class, RELATION_COLLECTION);
    }

    @Override
    public long countRelationships() {
        return mongoTemplate.getCollection(RELATION_COLLECTION).estimatedDocumentCount();
    }

    @Override
    public void deleteRelationships(TagDocRelationship tagDocRelationship) {
        Query query = new Query(Criteria.where(TAG_ID).is(tagDocRelationship.getTagId())
                .and(FILE_ID).is(tagDocRelationship.getFileId()));
        mongoTemplate.remove(query, TagDocRelationship.class, RELATION_COLLECTION);
    }

    @Override
    public boolean relationshipExists() {
        return mongoTemplate.exists(new Query(), TagDocRelationship.class, RELATION_COLLECTION);
    }

    @Override
    public List<TagDocRelationship> findRelationshipsByTagId(String tagId) {
        Query query = new Query().addCriteria(Criteria.where(TAG_ID).is(tagId));
        return mongoTemplate.find(query, TagDocRelationship.class, RELATION_COLLECTION);
    }

    @Override
    public List<TagDocRelationship> findRelationshipsByTagIdAndFileId(String tagId, String fileId) {
        Query query = new Query(Criteria.where(TAG_ID).is(tagId)
                .and(FILE_ID).is(fileId));
        return mongoTemplate.find(query, TagDocRelationship.class, RELATION_COLLECTION);
    }

    @Override
    public boolean relationshipExists(String tagId, String fileId) {
        Query query = new Query(Criteria.where(TAG_ID).is(tagId)
                .and(FILE_ID).is(fileId));
        return mongoTemplate.exists(query, TagDocRelationship.class, RELATION_COLLECTION);
    }

    @Override
    public void deleteRelationshipsByDocId(String docId) {
        Query query = new Query(Criteria.where(FILE_ID).is(docId));
        mongoTemplate.remove(query, TagDocRelationship.class, RELATION_COLLECTION);
    }

    @Override
    public void deleteRelationshipsByTagId(String tagId) {
        Query query = new Query(Criteria.where(TAG_ID).is(tagId));
        mongoTemplate.remove(query, TagDocRelationship.class, RELATION_COLLECTION);
    }

    @Override
    public List<Tag> findByNameRegex(String pattern) {
        Pattern regexPattern = Pattern.compile("^.*" + pattern + ".*$", Pattern.CASE_INSENSITIVE);
        Query query = new Query().addCriteria(Criteria.where("name").regex(regexPattern));
        return mongoTemplate.find(query, Tag.class, TAG_COLLECTION);
    }

    @Override
    public List<String> findFileIdsByTagIds(List<String> tagIds) {
        Query query = new Query().addCriteria(Criteria.where(TAG_ID).in(tagIds));
        List<TagDocRelationship> relationships = mongoTemplate.find(query, TagDocRelationship.class, RELATION_COLLECTION);
        return relationships.stream()
                .map(TagDocRelationship::getFileId)
                .distinct()
                .toList();
    }

    @Override
    public List<String> findFileIdsByTagNameRegex(String pattern) {
        // 先根据名称模式查找标签
        List<Tag> tags = findByNameRegex(pattern);
        if (tags.isEmpty()) {
            return List.of();
        }
        
        // 根据标签ID查找文件ID
        List<String> tagIds = tags.stream()
                .map(Tag::getId)
                .toList();
        return findFileIdsByTagIds(tagIds);
    }

    @Override
    public void deleteRelationshipsByIds(List<String> relationshipIds) {
        Query query = new Query(Criteria.where("_id").in(relationshipIds));
        mongoTemplate.remove(query, TagDocRelationship.class, RELATION_COLLECTION);
    }

    @Override
    public long countRelationshipsByTagId(String tagId) {
        Query query = new Query(Criteria.where(TAG_ID).is(tagId));
        return mongoTemplate.count(query, TagDocRelationship.class, RELATION_COLLECTION);
    }

    /**
     * 聚合查询方法，用于获取标签列表及其关联文档数量
     */
    public <T> AggregationResults<T> aggregate(Aggregation aggregation, String collectionName, Class<T> outputType) {
        return mongoTemplate.aggregate(aggregation, collectionName, outputType);
    }

    /**
     * 分页查询标签关系
     */
    public List<TagDocRelationship> findRelationshipsByPage(int pageIndex, int pageSize, String tagId) {
        Query query = new Query().with(Sort.by(Sort.Direction.DESC, "createDate"));
        long skip = (long) pageIndex * pageSize;
        query.skip(skip);
        query.limit(pageSize);
        if (tagId != null) {
            query.addCriteria(Criteria.where(TAG_ID).is(tagId));
        }
        return mongoTemplate.find(query, TagDocRelationship.class, RELATION_COLLECTION);
    }
}