package com.jiaruiblog.application.service.impl;

import com.jiaruiblog.application.service.TagService;
import com.jiaruiblog.domain.entity.Tag;
import com.jiaruiblog.domain.entity.TagDocRelationship;
import com.jiaruiblog.domain.entity.dto.FileDocumentDTO;
import com.jiaruiblog.domain.entity.vo.CateOrTagVO;
import com.jiaruiblog.domain.entity.vo.PageVO;
import com.jiaruiblog.domain.entity.vo.TagVO;
import com.jiaruiblog.infrastructure.repository.TagRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

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

    @Override
    public void insert(Tag tag) {
        tagRepository.save(tag);
    }

    @Override
    public void update(Tag tag) {
        tagRepository.update(tag);
    }

    @Override
    public void remove(Tag tag) {
        tagRepository.delete(tag);
    }

    @Override
    public void search(Tag tag) {
    }

    @Override
    public List<CateOrTagVO> list() {
        return List.of();
    }

    @Override
    public Tag queryById(String tagId) {
        return null;
    }

    @Override
    public Tag queryByName(String tagName) {
        return null;
    }

    @Override
    public List<Tag> queryByDocId(String docId) {
        return List.of();
    }

    @Override
    public List<Tag> getTagList() {
        return List.of();
    }

    @Override
    public void removeRelateByDocId(String docId) {
    }

    @Override
    public void addRelationShip(TagDocRelationship tag) {
    }

    @Override
    public void cancelTagRelationship(TagDocRelationship tag) {
    }

    @Override
    public List<String> fuzzySearchDoc(String keyWord) {
        return List.of();
    }

    @Override
    public List<Tag> getRandom() {
        return List.of();
    }

    @Override
    public String saveOrUpdateTag(String tagName) {
        return "";
    }

    @Override
    public PageVO<FileDocumentDTO> getDocByTagAndCate(String cateId, String tagId, String keyword, Long pageNum, Long pageSize) {
        return PageVO.<FileDocumentDTO>builder().build();
    }

    @Override
    public java.util.Map<Tag, java.util.List<TagDocRelationship>> getRecentTagRelationship() {
        return Map.of();
    }
}