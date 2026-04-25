package com.jiaruiblog.infrastructure.repository.mysql;

import com.jiaruiblog.domain.entity.po.CateDocRelationship;
import com.jiaruiblog.domain.entity.po.Category;
import com.jiaruiblog.infrastructure.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * MyBatis Category Repository Implementation
 */
@Repository
public class CategoryMybatisRepository implements CategoryRepository {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public void save(Category category) {
        categoryMapper.save(category);
    }

    @Override
    public void saveRelationship(CateDocRelationship relationship) {
        // Relationship is handled via CateDocRelationshipMapper
    }

    @Override
    public void delete(Category category) {
        // Implemented via relationship deletion
    }

    @Override
    public void deleteRelationship(CateDocRelationship relationship) {
        // Implemented via CateDocRelationshipMapper
    }

    @Override
    public void deleteRelationshipsByDocId(String docId) {
        // Implemented via CateDocRelationshipMapper
    }

    @Override
    public List<Category> findByName(String name) {
        return categoryMapper.findByName(name);
    }

    @Override
    public Optional<Category> findById(String id) {
        return categoryMapper.findById(id);
    }

    @Override
    public List<Category> findAll(Sort sort) {
        return categoryMapper.findAll();
    }

    @Override
    public List<CateDocRelationship> findRelationshipsByCategoryId(String categoryId, Sort sort) {
        // Implemented via CateDocRelationshipMapper
        return null;
    }

    @Override
    public List<CateDocRelationship> findRelationshipsByDocId(String docId) {
        return null;
    }

    @Override
    public List<CateDocRelationship> findRelationshipsByCategoryAndDoc(String categoryId, String docId) {
        return null;
    }

    @Override
    public long countAll() {
        return categoryMapper.countAll();
    }

    @Override
    public List<CateDocRelationship> findByDocIdIn(List<String> docIds) {
        return null;
    }

    @Override
    public List<CateDocRelationship> findByCategoryIdAndDocIdIn(String categoryId, List<String> docIds) {
        return null;
    }
}