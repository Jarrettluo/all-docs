package com.jiaruiblog.infrastructure.repository;

import com.jiaruiblog.domain.entity.Tag;
import com.jiaruiblog.domain.entity.TagDocRelationship;
import org.springframework.data.domain.Sort;

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
    List<Tag> findAll(Sort sort);
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

    // Additional business methods
    List<TagDocRelationship> findRelationshipsByTagId(String tagId);
    List<TagDocRelationship> findRelationshipsByTagIdAndFileId(String tagId, String fileId);
    boolean relationshipExists(String tagId, String fileId);
    void deleteRelationshipsByDocId(String docId);
    void deleteRelationshipsByTagId(String tagId);

    // Search and aggregation methods
    List<Tag> findByNameRegex(String pattern);
    List<String> findFileIdsByTagIds(List<String> tagIds);
    List<String> findFileIdsByTagNameRegex(String pattern);

    // Batch operations
    void deleteRelationshipsByIds(List<String> relationshipIds);
    long countRelationshipsByTagId(String tagId);

    List<TagDocRelationship> findByDocIdIn(List<String> docIds);
}