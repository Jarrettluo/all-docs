package com.jiaruiblog.application.service;

import com.jiaruiblog.domain.entity.po.Tag;
import com.jiaruiblog.domain.entity.po.TagDocRelationship;
import com.jiaruiblog.domain.entity.dto.FileDocumentDTO;
import com.jiaruiblog.domain.entity.vo.CateOrTagVO;
import com.jiaruiblog.domain.entity.vo.PageVO;

import java.util.List;

/**
 * @author Jarrett Luo
 * @Date 2022/6/7 11:38
 * @Version 1.0
 */
public interface TagService {

    /**
     * @author luojiarui
     * @Description 新增标签
     * @Date 15:47 2022/11/5
     * @Param [tag]
     */
    void insert(Tag tag);

    /**
     * @author luojiarui
     * @Description 更新标签
     * @Date 15:47 2022/11/5
     * @Param [tag]
     */
    void update(Tag tag);

    /**
     * @author luojiarui
     * @Description 删除标签
     * @Date 15:47 2022/11/5
     * @Param [tag]
     */
    void remove(Tag tag);

    /**
     * @author luojiarui
     * @Description 搜索标签
     * @Date 15:47 2022/11/5
     * @Param [tag]
     */
    void search(Tag tag);

    /**
     * @author luojiarui
     * @Description 获取所有标签
     * @Date 15:47 2022/11/5
     * @Param []
     * @return List<CateOrTagVO>
     */
    List<CateOrTagVO> list();

    /**
     * @author luojiarui
     * @Description 根据ID查询标签
     * @Date 15:47 2022/11/5
     * @Param [tagId]
     * @return Tag
     */
    Tag queryById(String tagId);

    /**
     * @author luojiarui
     * @Description 根据名称查询标签
     * @Date 15:47 2022/11/5
     * @Param [tagName]
     * @return Tag
     */
    Tag queryByName(String tagName);

    /**
     * @author luojiarui
     * @Description 根据文档ID查询标签列表
     * @Date 15:47 2022/11/5
     * @Param [docId]
     * @return List<Tag>
     */
    List<Tag> queryByDocId(String docId);

    /**
     * @author luojiarui
     * @Description 获取标签列表（用于下拉框）
     * @Date 15:47 2022/11/5
     * @Param []
     * @return List<Tag>
     */
    List<Tag> getTagList();

    /**
     * @author luojiarui
     * @Description 根据文档ID删除标签关联
     * @Date 15:47 2022/11/5
     * @Param [docId]
     */
    void removeRelateByDocId(String docId);

    /**
     * @author luojiarui
     * @Description 添加标签关联
     * @Date 15:47 2022/11/5
     * @Param [tag]
     */
    void addRelationShip(TagDocRelationship tag);

    /**
     * @author luojiarui
     * @Description 取消标签关联
     * @Date 15:47 2022/11/5
     * @Param [tag]
     */
    void cancelTagRelationship(TagDocRelationship tag);

    /**
     * @author luojiarui
     * @Description 模糊搜索标签
     * @Date 15:47 2022/11/5
     * @Param [keyWord]
     * @return List<String>
     */
    List<String> fuzzySearchDoc(String keyWord);

    /**
     * @author luojiarui
     * @Description 获取随机标签
     * @Date 15:47 2022/11/5
     * @Param []
     * @return List<Tag>
     */
    List<Tag> getRandom();

    /**
     * @author luojiarui
     * @Description 保存或更新标签
     * @Date 15:47 2022/11/5
     * @Param [tagName]
     * @return String
     */
    String saveOrUpdateTag(String tagName);

    /**
     * @author luojiarui
     * @Description 根据分类ID和标签ID联合查询文档
     * @Date 15:47 2022/11/5
     * @Param [cateId, tagId, keyword, pageNum, pageSize]
     * @return PageVO<FileDocumentDTO>
     */
    PageVO<FileDocumentDTO> getDocByTagAndCate(String cateId, String tagId, String keyword, Long pageNum, Long pageSize);

    /**
     * 获取最近的标签关联关系
     */
    java.util.Map<Tag, java.util.List<TagDocRelationship>> getRecentTagRelationship();
}