package com.jiaruiblog.infrastructure.repository.mongodb;

import com.jiaruiblog.domain.entity.CateDocRelationship;
import com.jiaruiblog.domain.entity.Category;
import com.jiaruiblog.infrastructure.repository.CategoryRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CategoryRepositoryImpl implements CategoryRepository {

    private static final String COLLECTION_NAME = "categoryCollection";
    private static final String RELATE_COLLECTION_NAME = "relateCateCollection";

    private final MongoTemplate mongoTemplate;

    public CategoryRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(Category category) {
        mongoTemplate.save(category, COLLECTION_NAME);
    }

    @Override
    public void saveRelationship(CateDocRelationship relationship) {
        mongoTemplate.save(relationship, RELATE_COLLECTION_NAME);
    }

    @Override
    public void delete(Category category) {
        Query query = new Query(Criteria.where("_id").is(category.getId()));
        mongoTemplate.remove(query, Category.class, COLLECTION_NAME);
    }

    @Override
    public void deleteRelationship(CateDocRelationship relationship) {
        Query query = new Query(Criteria.where("categoryId").is(relationship.getCategoryId())
                .and("fileId").is(relationship.getFileId()));
        mongoTemplate.remove(query, CateDocRelationship.class, RELATE_COLLECTION_NAME);
    }

    @Override
    public void deleteRelationshipsByDocId(String docId) {
        Query query = new Query(Criteria.where("fileId").is(docId));
        mongoTemplate.remove(query, CateDocRelationship.class, RELATE_COLLECTION_NAME);
    }

    @Override
    public List<Category> findByName(String name) {
        Query query = new Query(Criteria.where("name").is(name));
        return mongoTemplate.find(query, Category.class, COLLECTION_NAME);
    }

    @Override
    public Optional<Category> findById(String id) {
        return Optional.ofNullable(mongoTemplate.findById(id, Category.class, COLLECTION_NAME));
    }

    @Override
    public List<Category> findAll(Sort sort) {
        Query query = new Query().with(sort);
        return mongoTemplate.find(query, Category.class, COLLECTION_NAME);
    }

    @Override
    public List<CateDocRelationship> findRelationshipsByCategoryId(String categoryId, Sort sort) {
        Query query = new Query(Criteria.where("categoryId").is(categoryId)).with(sort);
        return mongoTemplate.find(query, CateDocRelationship.class, RELATE_COLLECTION_NAME);
    }

    @Override
    public List<CateDocRelationship> findRelationshipsByDocId(String docId) {
        Query query = new Query(Criteria.where("fileId").is(docId));
        return mongoTemplate.find(query, CateDocRelationship.class, RELATE_COLLECTION_NAME);
    }

    @Override
    public List<CateDocRelationship> findRelationshipsByCategoryAndDoc(String categoryId, String docId) {
        Query query = new Query(Criteria.where("categoryId").is(categoryId)
                .and("fileId").is(docId));
        return mongoTemplate.find(query, CateDocRelationship.class, RELATE_COLLECTION_NAME);
    }

    @Override
    public long countAll() {
        return mongoTemplate.getCollection(COLLECTION_NAME).estimatedDocumentCount();
    }

    @Override
    public List<CateDocRelationship> findByDocIdIn(List<String> docIds) {
        Query query = new Query(Criteria.where("fileId").in(docIds));
        return mongoTemplate.find(query, CateDocRelationship.class, RELATE_COLLECTION_NAME);
    }

    @Override
    public List<CateDocRelationship> findByCategoryIdAndDocIdIn(String categoryId, List<String> docIds) {
        Query query = new Query(Criteria.where("categoryId").is(categoryId).and("fileId").in(docIds));
        return mongoTemplate.find(query, CateDocRelationship.class, RELATE_COLLECTION_NAME);
    }
}