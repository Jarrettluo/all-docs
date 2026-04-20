package com.jiaruiblog.application.service.impl;

import com.jiaruiblog.application.service.CategoryService;
import com.jiaruiblog.domain.entity.CateDocRelationship;
import com.jiaruiblog.domain.entity.Category;
import com.jiaruiblog.domain.entity.dto.FileDocumentDTO;
import com.jiaruiblog.domain.entity.vo.CateOrTagVO;
import com.jiaruiblog.domain.entity.vo.CategoryVO;
import com.jiaruiblog.domain.entity.vo.PageVO;
import com.jiaruiblog.common.exception.BusinessException;
import com.jiaruiblog.common.exception.BusinessExceptionBuilder;
import com.jiaruiblog.common.exception.ErrorCode;
import com.jiaruiblog.infrastructure.repository.CategoryRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 分类服务实现类
 * 提供分类相关的增删改查及关联关系管理功能
 *
 * @author Jarrett Luo
 */
@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    // 常量定义
    private static final String UPDATE_DATE = "uploadDate";  // 更新日期字段名

    @Resource
    CategoryRepository categoryRepository;  // 分类数据访问接口

    /**
     * 新增分类记录
     * 注意：需要处理并发插入的事务问题
     *
     * @param category 分类实体对象
     * @throws BusinessException 当分类名称已存在时抛出
     */
    @Override
    public void insert(Category category) {
        // 检查分类名称是否已存在
        if (!isNameExist(category.getName()).isEmpty()) {
            throw BusinessExceptionBuilder.of(ErrorCode.OPERATE_FAILED).build();
        }
        // 保存分类信息
        categoryRepository.save(category);
    }

    /**
     * 更新分类记录
     *
     * @param category 分类实体对象
     * @throws BusinessException 当分类不存在时抛出
     */
    @Override
    public void update(Category category) {
        // 检查分类名称是否为null
        if (category.getName() == null || category.getName().isEmpty()) {
            throw BusinessExceptionBuilder.of(ErrorCode.PARAM_ERROR).withMessage("分类名称不能为空").build();
        }
        // 检查分类是否存在
        Optional<Category> existing = categoryRepository.findById(category.getId());
        if (existing.isEmpty()) {
            throw BusinessExceptionBuilder.of(ErrorCode.CATEGORY_NOT_FOUND).build();
        }
        // 更新分类信息
        Category toUpdate = existing.get();
        toUpdate.setName(category.getName());
        toUpdate.setUpdateDate(category.getUpdateDate());
        categoryRepository.save(toUpdate);
    }

    /**
     * 保存或更新分类
     * 如果分类名称已存在则返回现有分类ID，否则创建新分类
     *
     * @param cateName 分类名称
     * @return 分类ID，如果名称为空则返回null
     */
    @Override
    public String saveOrUpdateCate(String cateName) {
        if (!StringUtils.hasText(cateName)) {
            return null;
        }

        // 检查分类是否已存在
        List<Category> nameExist = categoryRepository.findByName(cateName);
        if (nameExist.isEmpty()) {
            // 创建新分类
            Category category = new Category();
            category.setUpdateDate(new Date());
            category.setCreateDate(new Date());
            category.setName(cateName);
            categoryRepository.save(category);
            return category.getId();
        } else {
            // 返回现有分类ID
            return nameExist.stream().findFirst().map(Category::getId).orElse(null);
        }
    }

    /**
     * 检查分类名称是否存在
     *
     * @param name 分类名称
     * @return 包含该名称的分类列表，如果不存在则返回空列表
     */
    private List<Category> isNameExist(String name) {
        return categoryRepository.findByName(name);
    }

    /**
     * 删除分类记录
     * 注意：需要先删除该分类下的所有关联关系
     *
     * @param category 要删除的分类实体
     */
    @Override
    public void remove(Category category) {
        // 先删除关联关系，避免数据不一致
        List<CateDocRelationship> relationships = categoryRepository.findRelationshipsByCategoryId(category.getId(), Sort.unsorted());
        for (CateDocRelationship relationship : relationships) {
            categoryRepository.deleteRelationship(relationship);
        }
        // 删除分类
        categoryRepository.delete(category);
    }

    /**
     * 搜索分类
     *
     * @param category 分类实体对象，包含搜索条件
     */
    @Override
    public void search(Category category) {
        if (category == null || category.getName() == null || category.getName().isEmpty()) {
            return;
        }
        // 根据分类名称模糊搜索（如果Repository支持）
        List<Category> results = categoryRepository.findByName(category.getName());
        // 搜索结果可用于后续处理，当前实现仅验证查询能力
        log.debug("搜索分类 '{}'，找到 {} 条结果", category.getName(), results.size());
    }

    /**
     * 获取所有分类列表及关联文档数量
     *
     * @return 分类视图对象列表，包含分类基本信息和关联文档数量
     */
    @Override
    public List<CateOrTagVO> list() {
        // 按更新时间升序获取所有分类
        List<Category> categories = categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "updateDate"));

        return categories.stream().map(category -> {
            CateOrTagVO vo = new CateOrTagVO();
            vo.setId(category.getId());
            vo.setName(category.getName());
            vo.setCreateDate(category.getCreateDate());
            vo.setUpdateDate(category.getUpdateDate());
            // 查询并设置该分类下的文档数量
            vo.setNum(categoryRepository.findRelationshipsByCategoryId(category.getId(), Sort.unsorted()).size());
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public void addRelationShip(CateDocRelationship relationship) {

    }

    /**
     * 取消某个文件在分类下的关联关系
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
        return result.stream()
                .map(CateDocRelationship::getFileId)
                .collect(Collectors.toList());
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
     * @return com.jiaruiblog.domain.entity.Category
     * @author luojiarui
     * 根据文档的信息返回分类信息
     **/
    @Override
    public CategoryVO queryByDocId(String docId) {
        List<CateDocRelationship> relationships = categoryRepository.findRelationshipsByDocId(docId);
        if (relationships.isEmpty()) {
            return null;
        }
        CateDocRelationship relationship = relationships.get(0);
        if (relationship == null || relationship.getCategoryId() == null) {
            return null;
        }
        Category category = categoryRepository.findById(relationship.getCategoryId()).orElse(null);
        if (category == null) {
            return null;
        }
        CategoryVO categoryVO = new CategoryVO();
        categoryVO.setId(category.getId());
        categoryVO.setName(category.getName());
        categoryVO.setRelationShipId(relationship.getId());
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
        List<String> ids = categories.stream().map(Category::getId).toList();
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
     * @return java.util.List<com.jiaruiblog.domain.entity.Category>
     * @author luojiarui
     * 热度随机产生22/6/26
     **/
    @Override
    public List<Category> getRandom() {
        List<Category> allCategories = categoryRepository.findAll(Sort.by(Sort.Direction.DESC, UPDATE_DATE));
        if (allCategories.isEmpty()) {
            return Lists.newArrayList();
        }
        int size = Math.min(3, allCategories.size());
        return allCategories.subList(0, size);
    }

    @Override
    public void addRelationShipDefault(String categoryId, String docId) {

    }

    @Override
    public void addRelationShipDefault(String categoryId, List<String> docIds) {

    }

    /**
     * @return java.util.List<com.jiaruiblog.domain.entity.CateDocRelationship>
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
    public PageVO<FileDocumentDTO> getMyCollection(String cateId, String tagId,
                                                   String keyword, Long pageNum,
                                                   Long pageSize, String userId) {
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