package com.jiaruiblog.repository;

import com.jiaruiblog.entity.Tag;
import com.jiaruiblog.entity.TagDocRelationship;

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
    void update(Tag tag);

    // Relationship operations
    TagDocRelationship saveRelationship(TagDocRelationship relationship);
    List<TagDocRelationship> findRelationships();
    List<TagDocRelationship> findRelationshipsByDocId(String docId);
    long countRelationships();
    void deleteRelationships(TagDocRelationship tagDocRelationship);
    boolean relationshipExists();

}