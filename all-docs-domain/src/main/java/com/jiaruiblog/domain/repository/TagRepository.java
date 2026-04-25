package com.jiaruiblog.domain.repository;

import com.jiaruiblog.domain.entity.Tag;

import java.util.List;

/**
 * @ClassName TagRepository
 * @Description 标签仓储接口
 * @author luojiarui
 * @Date 2022/6/4 10:31 上午
 * @Version 1.0
 **/
public interface TagRepository {

    /**
     * 保存标签
     * @param tag 标签实体
     * @return 保存后的标签
     */
    Tag save(Tag tag);

    /**
     * 根据ID查询标签
     * @param id 标签ID
     * @return 标签实体
     */
    Tag findById(String id);

    /**
     * 根据ID列表查询标签
     * @param idList ID列表
     * @return 标签列表
     */
    List<Tag> findByIds(List<String> idList);

    /**
     * 根据名称查询标签
     * @param name 标签名称
     * @return 标签列表
     */
    List<Tag> findByName(String name);

    /**
     * 根据名称列表查询标签
     * @param names 名称列表
     * @return 标签列表
     */
    List<Tag> findByNames(List<String> names);

    /**
     * 查询所有标签
     * @return 标签列表
     */
    List<Tag> findAll();

    /**
     * 统计标签总数
     * @return 数量
     */
    long count();

    /**
     * 删除标签
     * @param tag 标签实体
     */
    void delete(Tag tag);
}