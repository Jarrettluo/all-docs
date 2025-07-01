package com.jiaruiblog.service.impl;

import com.jiaruiblog.entity.CateDocRelationship;
import com.jiaruiblog.entity.Category;
import com.jiaruiblog.entity.dto.FileDocumentDTO;
import com.jiaruiblog.entity.vo.CateOrTagVO;
import com.jiaruiblog.entity.vo.CategoryVO;
import com.jiaruiblog.entity.vo.PageVO;
import com.jiaruiblog.exception.BusinessExceptionBuilder;
import com.jiaruiblog.exception.ErrorCode;
import com.jiaruiblog.repository.CategoryRepository;
import com.jiaruiblog.service.CategoryService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Jarrett Luo
 * @Date 2022/6/7 11:39
 * @Version 1.0
 */
@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

//    private static final String COLLECTION_NAME = "categoryCollection";
//
//    private static final String RELATE_COLLECTION_NAME = "relateCateCollection";

    private static final String CATEGORY_ID = "categoryId";

    private static final String UPDATE_DATE = "uploadDate";

    private static final String FILE_ID = "fileId";
    public static final String DOC_ID = "docId";

//    @Resource
//    MongoTemplate mongoTemplate;

    @Resource
    CategoryRepository categoryRepository;

    /**
     * 新增一条分类记录
     * 这里需要考虑并发插入的事务问题
     *
     * @param category -> Category 实体
     */
    @Override
    public void insert(Category category) {
        if (!isNameExist(category.getName()).isEmpty()) {
            throw BusinessExceptionBuilder.of(ErrorCode.OPERATE_FAILED).build();
        }
        // mongoTemplate.save(category, COLLECTION_NAME);
        categoryRepository.save(category);
    }

    /**
     * 更新一条已经存在的记录
     *
     * @param category -> Category 实体
     */
    @Override
    public void update(Category category) {
        if (categoryRepository.findByName(category.getName()).isEmpty()) {
            throw BusinessExceptionBuilder.of(ErrorCode.CATEGORY_NOT_FOUND).build();
        }
        Optional<Category> existing = categoryRepository.findById(category.getId());
        if (existing.isPresent()) {
            Category toUpdate = existing.get();
            toUpdate.setName(category.getName());
            toUpdate.setUpdateDate(category.getUpdateDate());
            categoryRepository.save(toUpdate);
        }
    }

    /**
     * 有就返回分类的id；没有的话就新增后返回id
     *
     * @return java.lang.String
     * @author luojiarui
     **/
    @Override
    public String saveOrUpdateCate(String cateName) {
        if (!StringUtils.hasText(cateName)) {
            return null;
        }
        List<Category> nameExist = categoryRepository.findByName(cateName);
        if (nameExist.isEmpty()) {
            Category category = new Category();
            category.setUpdateDate(new Date());
            category.setCreateDate(new Date());
            category.setName(cateName);
            categoryRepository.save(category);
            return category.getId();
        } else {
            return nameExist.stream().findFirst().map(Category::getId).orElse(null);
        }
    }

    /**
     * 判断该名字是否存在，如果是存在的则返回true，否则返回false
     *
     * @return boolean
     * @author luojiarui
     **/
    private List<Category> isNameExist(String name) {
        return categoryRepository.findByName(name);
    }

    /**
     * @param category -> Category 实体
     */
    @Override
    public void remove(Category category) {
        categoryRepository.delete(category);
        // 删除掉相关的分类关系
//        categoryRepository.deleteRelationshipsByCategoryId(category.getId());
    }

    @Override
    public void search(Category category) {
        // TODO 待开发
    }

    @Override
    public List<CateOrTagVO> list() {
        List<Category> categories = categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "updateDate"));
        return categories.stream().map(category -> {
            CateOrTagVO vo = new CateOrTagVO();
            vo.setId(category.getId());
            vo.setName(category.getName());
            vo.setCreateDate(category.getCreateDate());
            vo.setUpdateDate(category.getUpdateDate());
            vo.setNum(categoryRepository.findRelationshipsByCategoryId(category.getId(), Sort.unsorted()).size());
            return vo;
        }).collect(Collectors.toList());
    }
    /**
     * 增加某个文件的分类关系
     *
     * @param relationship -> CateDocRelationship
     */
    @Override
    public void addRelationShip(CateDocRelationship relationship) {
        if (relationship.getCategoryId() == null || relationship.getFileId() == null) {
            throw BusinessExceptionBuilder.of(ErrorCode.PARAMS_ERROR).build();
        }
        // 先排查一个文章只能有一个分类关系，不能有多个分类信息
        List<CateDocRelationship> existingRelations = categoryRepository.findRelationshipsByDocId(relationship.getFileId());
        if (!CollectionUtils.isEmpty(existingRelations)) {
            throw BusinessExceptionBuilder.of(ErrorCode.PARAMS_ERROR).build();
        }

        // 先排查是否具有该链接关系，否则不予进行关联
        List<CateDocRelationship> result = categoryRepository.findRelationshipsByCategoryAndDoc(
            relationship.getCategoryId(), relationship.getFileId());
        if (!result.isEmpty()) {
            throw BusinessExceptionBuilder.of(ErrorCode.OPERATE_FAILED).build();
        }
        categoryRepository.saveRelationship(relationship);
    }

    private void addDocRelate(CateDocRelationship relationship) {
        if (relationship.getCategoryId() == null || relationship.getFileId() == null) {
            throw BusinessExceptionBuilder.of(ErrorCode.PARAMS_ERROR).build();
        }
        // 先排查一个文章只能有一个分类关系，不能有多个分类信息
    List<CateDocRelationship> relationships = categoryRepository.findRelationshipsByDocId(relationship.getFileId());
        if (!CollectionUtils.isEmpty(relationships)) {
            throw BusinessExceptionBuilder.of(ErrorCode.PARAMS_ERROR).build();
        }

        // 先排查是否具有该链接关系，否则不予进行关联
    List<CateDocRelationship> result = categoryRepository.findRelationshipsByCategoryAndDoc(
        relationship.getCategoryId(), relationship.getFileId());
        if (!result.isEmpty()) {
            throw BusinessExceptionBuilder.of(ErrorCode.PARAMS_ERROR).build();
        }
    categoryRepository.saveRelationship(relationship);
    }

    @Async
    @Override
    public void addRelationShipDefault(String categoryId, String docId) {
        if (categoryId == null) {
            return;
        }
        CateDocRelationship relationship = new CateDocRelationship();
        relationship.setCategoryId(categoryId);
        relationship.setCreateDate(new Date());
        relationship.setFileId(docId);
        relationship.setUpdateDate(new Date());
        addDocRelate(relationship);
    }

    @Override
    public void addRelationShipDefault(String categoryId, List<String> docIds) {
        for (String docId : docIds) {
            addRelationShipDefault(categoryId, docId);
        }
    }

    /**
     * 取消某个文件在分类下的关联关系
     *
     * @param relationship -> CateDocRelationship
     */
    @Override
    public void cancelCategoryRelationship(CateDocRelationship relationship) {
    categoryRepository.deleteRelationship(relationship);
    }

    /**
     * 根据category的id，查询相关连的文件id列表
     *
     * @param categoryDb -> Category
     * @return -> List<String>
     */
    @Override
    public List<String> queryDocListByCategory(Category categoryDb) {
    List<CateDocRelationship> result = categoryRepository.findRelationshipsByCategoryId(categoryDb.getId(), Sort.unsorted());
        if (result.isEmpty()) {
            return Lists.newArrayList();
        }
        return result.stream().map(CateDocRelationship::getFileId).collect(Collectors.toList());
    }

    /**
     * 根据分类的id查询分类信息
     *
     * @param id -> String
     * @return -> Category
     */
    @Override
    public Category queryById(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }
    return categoryRepository.findById(id).orElse(null);
    }

    /**
     * @return com.jiaruiblog.entity.Category
     * @author luojiarui
     * 根据文档的信息返回分类信息
     **/
    @Override
    public CategoryVO queryByDocId(String docId) {
    List<CateDocRelationship> relationships = categoryRepository.findRelationshipsByDocId(docId);
    if (relationships.isEmpty() || relationships.get(0).getCategoryId() == null) {
            throw BusinessExceptionBuilder.of(ErrorCode.OPERATE_FAILED).build();
        }
    Category category = categoryRepository.findById(relationships.get(0).getCategoryId()).orElse(new Category());
        CategoryVO categoryVO = new CategoryVO();
        categoryVO.setId(category.getId());
        categoryVO.setName(category.getName());
    categoryVO.setRelationShipId(relationships.get(0).getId());
        return categoryVO;
    }

    /**
     * 根据关键字模糊搜索相关的文档id
     *
     * @param keyWord 关键字
     * @return 文档的id信息
     */
    @Override
    public List<String> fuzzySearchDoc(String keyWord) {
        if (!StringUtils.hasText(keyWord)) {
            return Lists.newArrayList();
        }
    List<Category> categories = new ArrayList<>(); // categoryRepository.findByNameContainingIgnoreCase(keyWord);
        List<String> ids = categories.stream().map(Category::getId).collect(Collectors.toList());
    List<CateDocRelationship> relationships = new ArrayList<>();
    for (String id : ids) {
        relationships.addAll(categoryRepository.findRelationshipsByCategoryId(id, Sort.unsorted()));
    }

    return relationships.stream().map(CateDocRelationship::getFileId).collect(Collectors.toList());
}

    /**
     * @author luojiarui
     * 根据文档的id进行分类和文档的关系删除，这里文档的id是fileId
     **/
    @Override
    public void removeRelateByDocId(String docId) {
    categoryRepository.deleteRelationshipsByDocId(docId);
    }

    /**
     * @return java.util.List<com.jiaruiblog.entity.Category>
     * @author luojiarui
     * 热度随机产生22/6/26
     **/
    @Override
    public List<Category> getRandom() {
    return categoryRepository.findAll(Sort.by(Sort.Direction.DESC, UPDATE_DATE)).subList(0, 3);
}
    /**
     * @return java.util.List<com.jiaruiblog.entity.CateDocRelationship>
     * @author luojiarui
     * 根据总类查询关系
     **/
    @Override
    public List<CateDocRelationship> getRelateByCateId(String cateId) {
        long pageIndex = 0;
        int pageSize = 7;
        return categoryRepository.findRelationshipsByCategoryId(cateId, Sort.by(Sort.Direction.DESC, UPDATE_DATE))
            .stream()
            .skip((pageIndex - 1) * pageSize)
            .limit(pageSize)
            .collect(Collectors.toList());
    }

    /**
     * @return java.lang.Integer
     * @author luojiarui
     * 统计总数
     **/
    @Override
    public long countAllFile() {
        return categoryRepository.countAll();
    }

    /**
     * @return boolean
     * @author luojiarui
     * 某个分类和文档是否存在关系
     **/
    @Override
    public boolean relateExist(String categoryId, String fileId) {
        List<CateDocRelationship> result = categoryRepository.findRelationshipsByCategoryAndDoc(categoryId, fileId);
        return !CollectionUtils.isEmpty(result);
    }

    /**
     * @return com.jiaruiblog.util.BaseApiResult
     * @author luojiarui
     * 根据分类id， 标签id，搜索内容联合查询文档
     **/
    @Override
    public PageVO<FileDocumentDTO> getDocByTagAndCate(String cateId, String tagId, String keyword, Long pageNum, Long pageSize) {
    List<FileDocumentDTO> mappedResults = new ArrayList<>();
    int count = 0;

    // Implement logic using CategoryRepository instead of mongoTemplate
    // This is a placeholder - actual implementation will depend on your repository methods
        return PageVO.<FileDocumentDTO>builder()
            .pageSize(pageSize.intValue())
            .pageNum(pageNum.intValue())
            .total(count)
            .list(mappedResults)
                .build();
    }

@Override
public PageVO<FileDocumentDTO> getMyCollection(String cateId, String tagId, String keyword, Long pageNum, Long pageSize, String userId) {
    List<FileDocumentDTO> mappedResults = new ArrayList<>();
    int count = 0;

    // Implement logic using CategoryRepository instead of mongoTemplate
    // This is a placeholder - actual implementation will depend on your repository methods
    return PageVO.<FileDocumentDTO>builder()
            .pageSize(pageSize.intValue())
            .pageNum(pageNum.intValue())
            .total(count)
            .list(mappedResults)
            .build();
}

@Override
public PageVO<FileDocumentDTO> getMyUploaded(String cateId, String tagId, String keyword, Long pageNum, Long pageSize, String userId) {
    List<FileDocumentDTO> mappedResults = new ArrayList<>();
    int count = 0;

    // Implement logic using CategoryRepository instead of mongoTemplate
    // This is a placeholder - actual implementation will depend on your repository methods
    return PageVO.<FileDocumentDTO>builder()
            .total(count)
            .list(mappedResults)
            .pageNum(pageNum.intValue())
            .pageSize(pageSize.intValue())
            .build();
}
}
