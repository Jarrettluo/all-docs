package com.jiaruiblog.service.impl;

import com.jiaruiblog.entity.CateDocRelationship;
import com.jiaruiblog.entity.Category;
import com.jiaruiblog.entity.dto.FileDocumentDTO;
import com.jiaruiblog.entity.vo.CateOrTagVO;
import com.jiaruiblog.entity.vo.CategoryVO;
import com.jiaruiblog.entity.vo.PageVO;
import com.jiaruiblog.exception.BusinessException;
import com.jiaruiblog.exception.BusinessExceptionBuilder;
import com.jiaruiblog.exception.ErrorCode;
import com.jiaruiblog.repository.CategoryRepository;
import com.jiaruiblog.service.CategoryService;
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
 * @Date 2022/6/7 11:39
 * @Version 1.0
 */
@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    // 常量定义
    private static final String CATEGORY_ID = "categoryId";  // 分类ID字段名
    private static final String UPDATE_DATE = "uploadDate";  // 更新日期字段名
    private static final String FILE_ID = "fileId";          // 文件ID字段名
    public static final String DOC_ID = "docId";             // 文档ID字段名

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
        // 检查分类名称是否存在
        if (categoryRepository.findByName(category.getName()).isEmpty()) {
            throw BusinessExceptionBuilder.of(ErrorCode.CATEGORY_NOT_FOUND).build();
        }

        // 更新分类信息
        Optional<Category> existing = categoryRepository.findById(category.getId());
        if (existing.isPresent()) {
            Category toUpdate = existing.get();
            toUpdate.setName(category.getName());
            toUpdate.setUpdateDate(category.getUpdateDate());
            categoryRepository.save(toUpdate);
        }
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
        // 删除分类
        categoryRepository.delete(category);
        // TODO: 需要先删除关联关系，否则会导致数据不一致
        // categoryRepository.deleteRelationshipsByCategoryId(category.getId());
    }

    /**
     * 搜索分类（待实现）
     *
     * @param category 分类实体对象（未使用）
     * @deprecated 该方法尚未实现具体功能
     */
    @Override
    @Deprecated
    public void search(Category category) {
        // TODO 待实现分类搜索功能
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
     * 添加文档与分类的关联关系
     *
     * @param relationship 关联关系实体
     * @param relationship -> CateDocRelationship
     * @throws BusinessException 当参数错误或关系已存在时抛出
     *                           CateDocRelationship relationship = new CateDocRelationship();
     *                           relationship.setCategoryId(categoryId);
     *                           relationship.setCreateDate(new Date());
     *                           relationship.setFileId(docId);
     *                           relationship.setUpdateDate(new Date());
     *                           addDocRelate(relationship);
     *                           }
     * @Override public void addRelationShipDefault(String categoryId, List<String> docIds) {
     * for (String docId : docIds) {
     * addRelationShipDefault(categoryId, docId);
     * }
     * }
     * <p>
     * /**
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

    @Override
    public void addRelationShipDefault(String categoryId, String docId) {

    }

    @Override
    public void addRelationShipDefault(String categoryId, List<String> docIds) {

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
