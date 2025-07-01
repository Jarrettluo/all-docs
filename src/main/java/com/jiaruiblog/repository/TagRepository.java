package com.jiaruiblog.repository;

import com.jiaruiblog.entity.Tag;
import com.jiaruiblog.entity.TagDocRelationship;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;

import java.util.Collection;
import java.util.List;

public interface TagRepository {

    // Tag operations
    Tag save(Tag tag);
    Collection<Tag> saveAll(Collection<Tag> tags);
    Tag findById(String id);
    List<Tag> findByIds(List<String> ids);
    List<Tag> findByName(String name);
    List<Tag> findByNames(List<String> names);
    long count();
    void delete(Tag tag);
    UpdateResult update(Query query, Update update);

    // Relationship operations
    TagDocRelationship saveRelationship(TagDocRelationship relationship);
    List<TagDocRelationship> findRelationships(Query query);
    long countRelationships(Query query);
    void deleteRelationships(Query query);
    boolean relationshipExists(Query query);

    // Aggregation operations
    <T> AggregationResults<T> aggregate(Aggregation aggregation, String collectionName, Class<T> outputType);
}