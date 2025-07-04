package com.jiaruiblog.service.impl;

import com.jiaruiblog.entity.CollectDocRelationship;
import com.jiaruiblog.exception.BusinessException;
import com.jiaruiblog.exception.ErrorCode;
import com.jiaruiblog.repository.CollectRepository;
import com.jiaruiblog.service.CollectService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 收藏服务实现类
 * @author Jarrett Luo
 */
@Slf4j
@Service
public class CollectServiceImpl implements CollectService {

    @Resource
    CollectRepository collectRepository;

    /**
     * 添加文档收藏关系
     * @param collect 收藏关系实体
     */
    @Override
    public void insert(CollectDocRelationship collect) {
        Boolean aBoolean = insertRelationShip(collect);
        if (Boolean.FALSE.equals(aBoolean)) {
            throw new BusinessException(ErrorCode.OPERATE_FAILED);
        }
    }

    @Override
    public Boolean insertRelationShip(CollectDocRelationship collect) {
        CollectDocRelationship collectDb = getExistRelationship(collect);
        if (collectDb != null) {
            return false;
        }
        collectRepository.save(collect);
        return true;
    }

    /**
     * 移除文档收藏关系
     * @param collect 收藏关系实体
     */
    @Override
    public void remove(CollectDocRelationship collect) {
        collectRepository.findByDocIdAndUserId(collect.getDocId(), collect.getUserId())
                .ifPresent(collectRepository::remove);
    }

    /**
     * 查询已存在的收藏关系
     * @param collect 收藏关系实体
     * @return 已存在的收藏关系实体
     */
    private CollectDocRelationship getExistRelationship(CollectDocRelationship collect) {
        collect = Optional.ofNullable(collect).orElse(new CollectDocRelationship());
        return collectRepository.findByDocIdAndUserId(collect.getDocId(), collect.getUserId())
                .orElse(null);
    }

    /**
     * 查询文档收藏数量
     * @param docId 文档ID
     * @return 收藏数量
     */
    @Override
    public Long collectNum(String docId) {
        return collectRepository.countByDocId(docId);
    }

    /**
     * 根据文档ID移除所有相关收藏关系
     * @param docId 文档ID
     */
    @Override
    public void removeRelateByDocId(String docId) {
        List<CollectDocRelationship> relationships = collectRepository.findAllByDocId(docId);
        relationships.forEach(this::remove);
    }
}
