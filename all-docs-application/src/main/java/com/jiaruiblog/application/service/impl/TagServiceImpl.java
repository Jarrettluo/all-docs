package com.jiaruiblog.application.service.impl;

import com.jiaruiblog.application.service.TagService;
import com.jiaruiblog.common.exception.BusinessExceptionBuilder;
import com.jiaruiblog.common.exception.ErrorCode;
import com.jiaruiblog.domain.entity.po.CateDocRelationship;
import com.jiaruiblog.domain.entity.po.FileDocument;
import com.jiaruiblog.domain.entity.po.Tag;
import com.jiaruiblog.domain.entity.po.TagDocRelationship;
import com.jiaruiblog.domain.entity.dto.FileDocumentDTO;
import com.jiaruiblog.domain.entity.vo.CateOrTagVO;
import com.jiaruiblog.domain.entity.vo.PageVO;
import com.jiaruiblog.infrastructure.repository.CategoryRepository;
import com.jiaruiblog.infrastructure.repository.TagRepository;
import com.jiaruiblog.infrastructure.repository.DocumentRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Jarrett Luo
 * @Date 2022/6/7 11:40
 * @Version 1.0
 */
@Slf4j
@Service
public class TagServiceImpl implements TagService {

    @Resource
    TagRepository tagRepository;

    @Resource
    DocumentRepository documentRepository;

    @Resource
    CategoryRepository categoryRepository;

    @Override
    public void insert(Tag tag) {
        if (tag == null || !StringUtils.hasText(tag.getName())) {
            throw BusinessExceptionBuilder.of(ErrorCode.INVALID_PARAM).detail("标签名称不能为空").build();
        }
        tag.setCreateDate(new Date());
        tag.setUpdateDate(new Date());
        tagRepository.save(tag);
        log.info("标签创建成功：name={}", tag.getName());
    }

    @Override
    public void update(Tag tag) {
        if (tag == null || !StringUtils.hasText(tag.getId())) {
            throw BusinessExceptionBuilder.of(ErrorCode.INVALID_PARAM).build();
        }
        Tag existing = tagRepository.findById(tag.getId());
        if (existing == null) {
            throw BusinessExceptionBuilder.of(ErrorCode.TAG_NOT_FOUND).build();
        }
        if (StringUtils.hasText(tag.getName())) {
            existing.setName(tag.getName());
        }
        existing.setUpdateDate(new Date());
        tagRepository.update(existing);
        log.info("标签更新成功：id={}", tag.getId());
    }

    @Override
    public void remove(Tag tag) {
        if (tag == null || !StringUtils.hasText(tag.getId())) {
            throw BusinessExceptionBuilder.of(ErrorCode.INVALID_PARAM).build();
        }
        // 先删除关联关系
        tagRepository.deleteRelationshipsByDocId(tag.getId());
        // 删除标签
        tagRepository.delete(tag);
        log.info("标签删除成功：id={}", tag.getId());
    }

    @Override
    public void search(Tag tag) {
        if (tag == null || tag.getName() == null) {
            return;
        }
        // 根据标签名称模糊搜索
        List<Tag> results = tagRepository.findByNameRegex(tag.getName());
        log.debug("搜索标签 '{}'，找到 {} 条结果", tag.getName(), results.size());
    }

    @Override
    public List<CateOrTagVO> list() {
        List<Tag> tags = tagRepository.findAll(Sort.by(Sort.Direction.DESC, "updateDate"));
        if (tags == null) {
            return new ArrayList<>();
        }
        return tags.stream().map(tag -> {
            CateOrTagVO vo = new CateOrTagVO();
            vo.setId(tag.getId());
            vo.setName(tag.getName());
            // 获取该标签下的文档数量
            long count = tagRepository.countRelationshipsByTagId(tag.getId());
            vo.setNum((int) count);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public Tag queryById(String tagId) {
        if (!StringUtils.hasText(tagId)) {
            return null;
        }
        return tagRepository.findById(tagId);
    }

    @Override
    public Tag queryByName(String tagName) {
        if (!StringUtils.hasText(tagName)) {
            return null;
        }
        List<Tag> tags = tagRepository.findByName(tagName);
        return tags != null && !tags.isEmpty() ? tags.get(0) : null;
    }

    @Override
    public List<Tag> queryByDocId(String docId) {
        if (!StringUtils.hasText(docId)) {
            return new ArrayList<>();
        }
        List<TagDocRelationship> relationships = tagRepository.findRelationshipsByDocId(docId);
        if (relationships == null || relationships.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> tagIds = relationships.stream()
                .map(TagDocRelationship::getTagId)
                .collect(Collectors.toList());
        return tagRepository.findByIds(tagIds);
    }

    @Override
    public List<Tag> getTagList() {
        return tagRepository.findAll(Sort.by(Sort.Direction.DESC, "updateDate"));
    }

    @Override
    public void removeRelateByDocId(String docId) {
        if (!StringUtils.hasText(docId)) {
            return;
        }
        tagRepository.deleteRelationshipsByDocId(docId);
        log.info("删除文档关联的标签关系：docId={}", docId);
    }

    @Override
    public void addRelationShip(TagDocRelationship tagRelationship) {
        if (tagRelationship == null || !StringUtils.hasText(tagRelationship.getTagId())
                || !StringUtils.hasText(tagRelationship.getFileId())) {
            throw BusinessExceptionBuilder.of(ErrorCode.INVALID_PARAM).detail("标签关系不能为空").build();
        }
        tagRepository.saveRelationship(tagRelationship);
        log.info("标签关联创建成功：tagId={}, fileId={}", tagRelationship.getTagId(), tagRelationship.getFileId());
    }

    @Override
    public void cancelTagRelationship(TagDocRelationship tagRelationship) {
        if (tagRelationship == null) {
            return;
        }
        tagRepository.deleteRelationships(tagRelationship);
        log.info("标签关联删除成功");
    }

    @Override
    public List<String> fuzzySearchDoc(String keyWord) {
        if (!StringUtils.hasText(keyWord)) {
            return new ArrayList<>();
        }
        return tagRepository.findFileIdsByTagNameRegex(keyWord);
    }

    @Override
    public List<Tag> getRandom() {
        List<Tag> allTags = tagRepository.findAll(Sort.by(Sort.Direction.DESC, "updateDate"));
        if (allTags == null || allTags.isEmpty()) {
            return new ArrayList<>();
        }
        int size = Math.min(5, allTags.size());
        return allTags.subList(0, size);
    }

    @Override
    public String saveOrUpdateTag(String tagName) {
        if (!StringUtils.hasText(tagName)) {
            return null;
        }
        List<Tag> existingTags = tagRepository.findByName(tagName);
        if (existingTags != null && !existingTags.isEmpty()) {
            return existingTags.get(0).getId();
        }
        Tag tag = new Tag();
        tag.setName(tagName);
        tag.setCreateDate(new Date());
        tag.setUpdateDate(new Date());
        Tag savedTag = tagRepository.save(tag);
        log.info("创建新标签：name={}, id={}", tagName, savedTag.getId());
        return savedTag.getId();
    }

    @Override
    public PageVO<FileDocumentDTO> getDocByTagAndCate(String cateId, String tagId, String keyword, Long pageNum, Long pageSize) {
        List<String> docIds;

        // Step 1: Get doc IDs based on tag and category filters
        if (StringUtils.hasText(tagId)) {
            List<TagDocRelationship> tagRelationships = tagRepository.findRelationshipsByTagId(tagId);
            docIds = tagRelationships.stream().map(TagDocRelationship::getFileId).collect(Collectors.toList());
        } else if (StringUtils.hasText(cateId)) {
            List<CateDocRelationship> cateRelationships = categoryRepository.findRelationshipsByCategoryId(cateId, Sort.unsorted());
            docIds = cateRelationships.stream().map(CateDocRelationship::getFileId).collect(Collectors.toList());
        } else {
            docIds = new ArrayList<>();
        }

        if (docIds.isEmpty()) {
            return PageVO.<FileDocumentDTO>builder()
                    .pageSize(pageSize != null ? pageSize.intValue() : 10)
                    .pageNum(pageNum != null ? pageNum.intValue() : 0)
                    .total(0)
                    .list(new ArrayList<>())
                    .build();
        }

        // Step 2: Apply keyword filter if provided
        List<FileDocument> documents;
        int pageNumInt = pageNum != null ? pageNum.intValue() : 0;
        int pageSizeInt = pageSize != null ? pageSize.intValue() : 10;

        if (StringUtils.hasText(keyword)) {
            documents = documentRepository.findByPageWithFussySearch(pageNumInt, pageSizeInt, Sort.by(Sort.Direction.DESC, "uploadDate"), keyword);
        } else {
            documents = documentRepository.findByPage(pageNumInt, pageSizeInt, Sort.by(Sort.Direction.DESC, "uploadDate"));
        }

        // Step 3: Filter documents by the docIds from tag/category
        Set<String> docIdSet = Set.copyOf(docIds);
        List<FileDocument> filteredDocs = documents.stream()
                .filter(doc -> docIdSet.contains(doc.getId()))
                .collect(Collectors.toList());

        // Step 4: Convert to DTO
        List<FileDocumentDTO> mappedResults = filteredDocs.stream().map(doc -> {
            FileDocumentDTO dto = new FileDocumentDTO();
            BeanUtils.copyProperties(doc, dto);
            return dto;
        }).collect(Collectors.toList());

        return PageVO.<FileDocumentDTO>builder()
                .pageSize(pageSizeInt)
                .pageNum(pageNumInt)
                .total(mappedResults.size())
                .list(mappedResults)
                .build();
    }

    @Override
    public Map<Tag, List<TagDocRelationship>> getRecentTagRelationship() {
        List<TagDocRelationship> relationships = tagRepository.findRelationships();
        if (relationships == null || relationships.isEmpty()) {
            return new HashMap<>();
        }

        // 按标签分组
        Map<String, List<TagDocRelationship>> byTagId = relationships.stream()
                .collect(Collectors.groupingBy(TagDocRelationship::getTagId));

        Map<Tag, List<TagDocRelationship>> result = new HashMap<>();
        for (Map.Entry<String, List<TagDocRelationship>> entry : byTagId.entrySet()) {
            Tag tag = tagRepository.findById(entry.getKey());
            if (tag != null) {
                result.put(tag, entry.getValue());
            }
        }
        return result;
    }
}