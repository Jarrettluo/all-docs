package com.jiaruiblog.repository;

import com.jiaruiblog.entity.CateDocRelationship;
import com.jiaruiblog.entity.Category;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

/**
 * <p></p>
 * edit at 2025/7/1 13:58
 *
 * @author Jarrett Luo
 * @version 1.0
 */
public interface CategoryRepository {

    void save(Category category);

    void saveRelationship(CateDocRelationship relationship);

    void delete(Category category);

    void deleteRelationship(CateDocRelationship relationship);

    void deleteRelationshipsByDocId(String docId);

    List<Category> findByName(String name);

    Optional<Category> findById(String id);

    List<Category> findAll(Sort sort);

    List<CateDocRelationship> findRelationshipsByCategoryId(String categoryId, Sort sort);

    List<CateDocRelationship> findRelationshipsByDocId(String docId);

    List<CateDocRelationship> findRelationshipsByCategoryAndDoc(String categoryId, String docId);

    long countAll();

}
