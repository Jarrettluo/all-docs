package com.jiaruiblog.domain.repository;

import com.jiaruiblog.domain.entity.Category;

import java.util.List;
import java.util.Optional;

/**
 * @ClassName CategoryRepository
 * @Description 分类仓储接口
 * @author luojiarui
 * @Date 2022/6/4 10:28 上午
 * @Version 1.0
 **/
public interface CategoryRepository {

    /**
     * 保存分类
     * @param category 分类实体
     */
    void save(Category category);

    /**
     * 根据ID查询分类
     * @param id 分类ID
     * @return 分类实体
     */
    Optional<Category> findById(String id);

    /**
     * 根据名称查询分类
     * @param name 分类名称
     * @return 分类列表
     */
    List<Category> findByName(String name);

    /**
     * 查询所有分类
     * @return 分类列表
     */
    List<Category> findAll();

    /**
     * 统计分类总数
     * @return 数量
     */
    long countAll();
}