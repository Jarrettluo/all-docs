package com.jiaruiblog.infrastructure.repository.mysql;

import com.jiaruiblog.domain.entity.po.Tag;
import com.jiaruiblog.domain.entity.po.TagDocRelationship;
import com.jiaruiblog.infrastructure.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * MyBatis Tag Repository Implementation
 */
@Repository
public class TagMybatisRepository implements TagRepository {

    @Autowired
    private TagMapper tagMapper;

    @Override
    public Tag save(Tag tag) {
        tagMapper.save(tag);
        return tag;
    }

    @Override
    public Collection<Tag> saveAll(Collection<Tag> tags) {
        for (Tag tag : tags) {
            tagMapper.save(tag);
        }
        return tags;
    }

    @Override
    public Tag findById(String id) {
        return tagMapper.findById(id);
    }

    @Override
    public List<Tag> findByIds(List<String> ids) {
        return tagMapper.findByIds(ids);
    }

    @Override
    public List<Tag> findByName(String name) {
        return tagMapper.findByName(name);
    }

    @Override
    public List<Tag> findByNames(List<String> names) {
        return tagMapper.findByNames(names);
    }

    @Override
    public List<Tag> findAll(Sort sort) {
        return tagMapper.findAll();
    }

    @Override
    public long count() {
        return tagMapper.count();
    }

    @Override
    public void delete(Tag tag) {
        tagMapper.delete(tag);
    }

    @Override
    public void update(Tag tag) {
        // MyBatis doesn't have update method, use save instead
        tagMapper.save(tag);
    }

    @Override
    public TagDocRelationship saveRelationship(TagDocRelationship relationship) {
        return relationship;
    }

    @Override
    public List<TagDocRelationship> findRelationships() {
        return null;
    }

    @Override
    public List<TagDocRelationship> findRelationshipsByDocId(String docId) {
        return null;
    }

    @Override
    public long countRelationships() {
        return 0;
    }

    @Override
    public void deleteRelationships(TagDocRelationship tagDocRelationship) {
    }

    @Override
    public boolean relationshipExists() {
        return false;
    }

    @Override
    public List<TagDocRelationship> findRelationshipsByTagId(String tagId) {
        return null;
    }

    @Override
    public List<TagDocRelationship> findRelationshipsByTagIdAndFileId(String tagId, String fileId) {
        return null;
    }

    @Override
    public boolean relationshipExists(String tagId, String fileId) {
        return false;
    }

    @Override
    public void deleteRelationshipsByDocId(String docId) {
    }

    @Override
    public void deleteRelationshipsByTagId(String tagId) {
    }

    @Override
    public List<Tag> findByNameRegex(String pattern) {
        return null;
    }

    @Override
    public List<String> findFileIdsByTagIds(List<String> tagIds) {
        return null;
    }

    @Override
    public List<String> findFileIdsByTagNameRegex(String pattern) {
        return null;
    }

    @Override
    public void deleteRelationshipsByIds(List<String> relationshipIds) {
    }

    @Override
    public long countRelationshipsByTagId(String tagId) {
        return 0;
    }

    @Override
    public List<TagDocRelationship> findByDocIdIn(List<String> docIds) {
        return null;
    }
}