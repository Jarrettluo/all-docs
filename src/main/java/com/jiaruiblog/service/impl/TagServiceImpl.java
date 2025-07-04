package com.jiaruiblog.service.impl;

import com.jiaruiblog.convert.TagConvert;
import com.jiaruiblog.entity.FileDocument;
import com.jiaruiblog.entity.Tag;
import com.jiaruiblog.entity.TagDocRelationship;
import com.jiaruiblog.entity.vo.CateOrTagVO;
import com.jiaruiblog.entity.vo.TagVO;
import com.jiaruiblog.exception.BusinessException;
import com.jiaruiblog.exception.ErrorCode;
import com.jiaruiblog.repository.TagRepository;
import com.jiaruiblog.service.DocumentService;
import com.jiaruiblog.service.TagService;
import com.mongodb.client.result.DeleteResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ConvertOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Jarrett Luo
 * @Date 2022/6/7 11:40
 * @Version 1.0
 */
@Slf4j
@Lazy
@Service
public class TagServiceImpl implements TagService {

    private static final String COLLECTION_NAME = "tagCollection";

    public static final String RELATE_COLLECTION_NAME = "relateTagCollection";

    private static final String FILE_ID = "fileId";

    private static final String TAG_ID = "tagId";

    public static final String DOC_ID = "docId";

    private static final String OBJECT_ID = "_id";


    private DocumentService fileService;

    @Autowired
    TagRepository tagRepository;

    @Resource
    TagConvert tagConvert;

    // 通过属性注入，防止循环依赖
    @Autowired
    private void setFileService(DocumentService documentService) {
        this.fileService = documentService;
    }

    /***
     * <p>插入全新的标签数据</p>
     * @param tag 待插入的标签
     **/
    @Override
    public void insert(Tag tag) {
        // 必须经过查重啊
        if (isTagExist(tag.getName())) {
            throw new BusinessException(ErrorCode.TAG_EXISTS);
        }
        tagRepository.save(tag);
    }

    /***
     * <p>更新标签数据</p>
     * @param tag 准备更新的标签

     **/
    @Override
    public void update(Tag tag) {
        // 必须经过查重啊
        List<Tag> tags = queryTagByName(tag.getName());
        long count = tags.stream()
                .filter(item -> !Objects.equals(item.getId(), tag.getId()) && item.getName().equals(tag.getName()))
                .count();
        if (count > 0) {
            throw new BusinessException(ErrorCode.TAG_EXISTS);
        }
        tagRepository.update(tag);
    }

    @Override
    public List<String> saveOrUpdateBatch(List<String> tags) {
        if (CollectionUtils.isEmpty(tags)) {
            return new ArrayList<>();
        }
        tags = tags.stream().filter(Objects::nonNull).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(tags)) {
            return new ArrayList<>();
        }
        List<Tag> existedTags = queryTagListByNameList(tags.toArray(new String[0]));
        List<String> existedTagIdList = existedTags.stream().map(Tag::getId).collect(Collectors.toList());
        List<String> existedTagNameList = existedTags.stream().map(Tag::getName).toList();
        tags.removeAll(existedTagNameList);
        List<String> newTagNameList = tags;

        // 批量新建不存在的tag信息
        List<Tag> newTags = new ArrayList<>();
        for (String s : newTagNameList) {
            Tag tag = new Tag();
            tag.setUpdateDate(new Date());
            tag.setName(s);
            tag.setCreateDate(new Date());
            newTags.add(tag);
        }
        Collection<Tag> insertedTags = tagRepository.saveAll(newTags);
        List<String> newTagIdList = insertedTags.stream().map(Tag::getId).toList();
        existedTagIdList.addAll(newTagIdList);
        return existedTagIdList;
    }

    /**
     * @author luojiarui
     *  删除某个已经存在的tag信息
     **/
    @Override
    public void remove(Tag tag) {
        tagRepository.delete(tag);
    }

    /**
     * 根据文档的id查询tag数量
     *
     * @param tag 标签
     * @return BaseApiResult
     */
    @Override
    public TagVO queryById(Tag tag) {
        Tag tag1 = tagRepository.findById(tag.getId());
        return tagConvert.convertToVO(tag1);
    }

    /**
     * @return java.util.List<com.jiaruiblog.entity.Tag>
     * @author luojiarui
     * 根据tag的id查询全部的tag列表
     **/
    public List<Tag> queryByIds(List<String> tagIds) {
        return tagRepository.findByIds(tagIds);
    }

    @Override
    public List<TagVO> search(Tag tag) {
        return null;
    }

    /***
     * <p>查询全部的标签信息</p>
     *
     **/
    @Override
    public List<CateOrTagVO> list() {
        Aggregation aggregation = Aggregation.newAggregation(
                // 选择某些字段
                Aggregation.project("id", "name", "createDate", "updateDate")
                        .and(ConvertOperators.Convert.convertValue("$_id").to("string"))//将主键Id转换为objectId
                        .as("id"),//新字段名称,
                Aggregation.lookup(RELATE_COLLECTION_NAME, "id", "tagId", "abc"),
                Aggregation.project("id", "name", "createDate", "updateDate")
                        .and("abc")
                        .size()
                        .as("num"),
                Aggregation.sort(Sort.Direction.ASC, "updateDate")
        );

        AggregationResults<CateOrTagVO> result = tagRepository.aggregate(
                aggregation, COLLECTION_NAME, CateOrTagVO.class);
        List<CateOrTagVO> resultList = result.getMappedResults();
        return resultList;
    }

    /**
     * @return com.jiaruiblog.entity.Tag
     * @author luojiarui
     **/
    @Override
    public Tag queryByTagId(String id) {
        if (!StringUtils.hasText(id)) {
            throw new BusinessException(ErrorCode.TAG_NOT_FOUND);
        }
        return tagRepository.findById(id);
    }

    /**
     * @param relationship 关系对象
     */
    @Override
    public void addRelationShip(TagDocRelationship relationship) {
        if (relationship == null || !StringUtils.hasText(relationship.getTagId())) {
            return;
        }
        // 判断以下是否存在这个关系
        Query query = new Query(Criteria.where(TAG_ID).is(relationship.getTagId())
                .and(FILE_ID).is(relationship.getFileId()));
        if (tagRepository.relationshipExists(query)) {
            return;
        }
        tagRepository.saveRelationship(relationship);
    }

    @Override
    public void cancelTagRelationship(TagDocRelationship relationship) {
        tagRepository.deleteRelationships(relationship);
    }

    /**
     * 备用query 语句 // Query query1 = new Query().addCriteria(Criteria.where("_id").is(relationship.getTagId()));
     *
     * @return java.util.List<com.jiaruiblog.entity.vo.TagVO>
     * @author luojiarui
     * 根据文档的信息找到全部的tag信息
     **/
    @Override
    public List<TagVO> queryByDocId(String id) {
        List<TagVO> tagVOList = new ArrayList<>();
        List<TagDocRelationship> relationships = tagRepository.findRelationshipsByDocId(id);

        if (relationships.isEmpty()) {
            return tagVOList;
        }

        for (TagDocRelationship relationship : relationships) {
            Tag tag = Optional.ofNullable(tagRepository.findById(relationship.getTagId()))
                    .orElse(new Tag());
            TagVO tagVO = new TagVO();
            tagVO.setId(tag.getId());
            tagVO.setName(tag.getName());
            tagVO.setRelationshipId(relationship.getTagId());
            tagVOList.add(tagVO);
        }
        return tagVOList;
    }

    /**
     * @return java.util.Map<com.jiaruiblog.entity.Tag, java.util.List < com.jiaruiblog.entity.TagDocRelationship>>
     * @author luojiarui
     * 查询最近的tag分页
     **/
    public Map<Tag, List<TagDocRelationship>> getRecentTagRelationship(Integer tagNum) {
        Map<Tag, List<TagDocRelationship>> result = new HashMap<>();
        List<TagDocRelationship> files = getTagRelationshipByPage(0, tagNum, null);
        if (CollectionUtils.isEmpty(files)) {
            return result;
        }
        List<String> tagIds = files.stream().map(TagDocRelationship::getTagId).collect(Collectors.toList());
        List<Tag> tags = queryByIds(tagIds);
        for (Tag tag : tags) {
            result.put(tag, getTagRelationshipByPage(0, 12, tag.getId()));
        }
        return result;
    }

    /**
     * @return java.util.Map<com.jiaruiblog.entity.Tag, java.util.List < com.jiaruiblog.entity.TagDocRelationship>>
     * @author luojiarui
     * 默认查询两个最近的tag
     **/
    @Override
    public Map<Tag, List<TagDocRelationship>> getRecentTagRelationship() {
        return getRecentTagRelationship(2);
    }

    /**
     * @return boolean
     * @author luojiarui
     * 某个标签是否和文件存在关系
     **/
    @Override
    public boolean relateExist(String tagId, String fileId) {
        // 判断以下是否存在这个关系
        Query query = new Query(Criteria.where(TAG_ID).is(tagId)
                .and(FILE_ID).is(fileId));
        List<TagDocRelationship> result = mongoTemplate.find(query, TagDocRelationship.class, RELATE_COLLECTION_NAME);
        return !CollectionUtils.isEmpty(result);
    }

    /**
     * @return java.util.List<com.jiaruiblog.entity.TagDocRelationship>
     * @author luojiarui
     * 分页查询相关的关系列表
     * 1、tagId 为null 的时候不进行tag相关检索；2、page和size 进行分页
     **/
    public List<TagDocRelationship> getTagRelationshipByPage(int pageIndex, int pageSize, String tagId) {
        Query query = new Query().with(Sort.by(Sort.Direction.DESC, "createDate"));
        long skip = (long) (pageIndex) * pageSize;
        query.skip(skip);
        query.limit(pageSize);
        if (tagId != null) {
            query.addCriteria(Criteria.where(TAG_ID).is(tagId));
        }
        return Optional.of(mongoTemplate.find(query, TagDocRelationship.class, RELATE_COLLECTION_NAME)).orElse(Lists.newArrayList());
    }

    /**
     * @return java.util.List<java.lang.Long>
     * @author luojiarui
     * 根据tag的id 查询所有的相关的文档id列表
     **/
    @Override
    public List<String> queryDocIdListByTagId(String tagId) {
        Query query = new Query().addCriteria(Criteria.where(TAG_ID).is(tagId));
        List<TagDocRelationship> relationships = mongoTemplate.find(query, TagDocRelationship.class, RELATE_COLLECTION_NAME);
        return relationships.stream().map(TagDocRelationship::getFileId).collect(Collectors.toList());
    }

    /**
     * 根据关键字模糊搜索相关的文档id
     *
     * @param keyWord 关键字
     * @return 文档的id信息
     */
    @Override
    public List<String> fuzzySearchDoc(String keyWord) {
        if (keyWord == null || "".equalsIgnoreCase(keyWord)) {
            return Lists.newArrayList();
        }
        Pattern pattern = Pattern.compile("^.*" + keyWord + ".*$", Pattern.CASE_INSENSITIVE);
        Query query = new Query();
        query.addCriteria(Criteria.where("name").regex(pattern));
        List<Tag> categories = mongoTemplate.find(query, Tag.class, COLLECTION_NAME);
        if (CollectionUtils.isEmpty(categories)) {
            return Lists.newArrayList();
        }
        List<String> ids = categories.stream().map(Tag::getId).collect(Collectors.toList());
        Query query1 = new Query().addCriteria(Criteria.where(TAG_ID).in(ids));
        List<TagDocRelationship> relationships = mongoTemplate.find(query1, TagDocRelationship.class, RELATE_COLLECTION_NAME);

        return relationships.stream().map(TagDocRelationship::getFileId).collect(Collectors.toList());

    }

    /**
     * 判断某个tag名字是否已经存在？
     *
     * @param tagName 标签名字
     * @return 布尔
     */
    private boolean isTagExist(String tagName) {
        List<Tag> tags = queryTagByName(tagName);
        return !CollectionUtils.isEmpty(tags);
    }

    /**
     * 根据tag的名字检索tag信息
     *
     * @param name tag 名称
     * @return 查询回来的tag列表
     */
    private List<Tag> queryTagByName(String name) {
        if (!StringUtils.hasText(name)) {
            return Lists.newArrayList();
        }
        return tagRepository.findByName(name);
    }

    /**
     * @param name
     * @return java.util.List<com.jiaruiblog.entity.Tag>
     * @author luojiarui
     * @Description 通过列表查询全部的标签信息
     * @Date 15:56 2023/4/22
     * @Param [name]
     */
    public List<Tag> queryTagListByNameList(String... name) {
        List<String> nameList = Arrays.asList(name);
        if (CollectionUtils.isEmpty(nameList)) {
            return Lists.newArrayList();
        }
        return mongoTemplate.find(Query.query(Criteria.where("name").in(nameList)), Tag.class, COLLECTION_NAME);
    }


    /**
     * @author luojiarui
     * @Description // 根据文档的id解除掉标签和文档的关系
     * @Date 11:22 上午 2022/6/25
     * @Param [docId]
     **/
    @Override
    public void removeRelateByDocId(String docId) {
        Query query = new Query(Criteria.where(DOC_ID).is(docId));
        mongoTemplate.remove(query, TagDocRelationship.class, RELATE_COLLECTION_NAME);
    }

    /**
     * @return java.lang.Integer
     * @author luojiarui
     * @Description // 统计总数
     * @Date 4:40 下午 2022/6/26
     * @Param []
     **/
    @Override
    public long countAllFile() {
        return mongoTemplate.getCollection(COLLECTION_NAME).estimatedDocumentCount();
    }

    /**
     * @author luojiarui
     * @Description 保存文章的时候保存标签和文档的关系
     * @Date 12:15 2023/2/19
     * @Param [fileDocument]
     **/
    @Async
    @Override
    public void saveTagWhenSaveDoc(FileDocument fileDocument) {
        if (fileDocument == null || !StringUtils.hasText(fileDocument.getSuffix())) {
            return;
        }
        String suffix = fileDocument.getSuffix();
        String tagName = suffix.substring(suffix.lastIndexOf(".") + 1);

        if (tagName.length() == 0) {
            return;
        }

        List<Tag> tags = queryTagByName(tagName.toUpperCase(Locale.ROOT));
        Tag tag;

        if (CollectionUtils.isEmpty(tags)) {
            tag = new Tag();
            tag.setName(tagName.toUpperCase(Locale.ROOT));
            tag.setCreateDate(new Date());
            tag.setUpdateDate(new Date());
            tag = mongoTemplate.save(tag, COLLECTION_NAME);
        } else {
            tag = tags.get(0);
        }
        if (tag.getId() != null) {
            addRelationShip(getRelationInstance(tag.getId(), fileDocument.getId()));
        }
    }

    private TagDocRelationship getRelationInstance(String tagId, String docId) {
        TagDocRelationship tagDocRelationship = new TagDocRelationship();
        tagDocRelationship.setTagId(tagId);
        tagDocRelationship.setFileId(docId);
        tagDocRelationship.setCreateDate(new Date());
        tagDocRelationship.setUpdateDate(new Date());
        return tagDocRelationship;
    }

    /**
     * @author luojiarui
     * 批量保存数据
     **/
    @Override
    @Async
    public void addTagRelationShip(List<String> tags, List<String> docIds) {
        for (String docId : docIds) {
            for (String tag : tags) {
                if (tag == null) {
                    continue;
                }
                addRelationShip(getRelationInstance(tag, docId));
            }
        }
    }

    @Async
    @Override
    public void clearInvalidTagRelationship(String tagId) {
        Query query = new Query(Criteria.where(TAG_ID).is(tagId));
        List<TagDocRelationship> relationshipList = mongoTemplate.find(query, TagDocRelationship.class, RELATE_COLLECTION_NAME);
        // 对列表关系进行分批
        List<List<TagDocRelationship>> partition = ListUtils.partition(relationshipList, 50);

        List<String> invalidRelationship = new ArrayList<>();

        for (List<TagDocRelationship> relationships : partition) {
            List<String> docIdList = relationships.stream()
                    .map(TagDocRelationship::getFileId)
                    .collect(Collectors.toList());

            List<FileDocument> fileDocuments = fileService.queryByDocIds(docIdList.toArray(new String[0]));
            List<String> docIdListInDB = fileDocuments.stream()
                    .map(FileDocument::getId)
                    .collect(Collectors.toList());

            if (docIdList.size() != docIdListInDB.size()) {
                // 剩下在列表中的是无效的列表信息
                docIdList.removeAll(docIdListInDB);
                List<String> invalidList = relationships.stream()
                        .filter(item -> docIdList.contains(item.getFileId()))
                        .map(TagDocRelationship::getId)
                        .collect(Collectors.toList());
                invalidRelationship.addAll(invalidList);
            }
        }
        if (!invalidRelationship.isEmpty()) {
            Query deleteQuery = new Query(Criteria.where(OBJECT_ID).in(invalidRelationship));
            DeleteResult remove = mongoTemplate.remove(deleteQuery, TagDocRelationship.class, RELATE_COLLECTION_NAME);
            log.info("查询的无效id：" + tagId + "删除结果数量：" + remove.getDeletedCount());
        }
    }

}
