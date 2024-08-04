package com.jiaruiblog.service.impl;

import com.google.common.collect.Maps;
import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.entity.Comment;
import com.jiaruiblog.entity.User;
import com.jiaruiblog.entity.dto.BasePageDTO;
import com.jiaruiblog.entity.dto.CommentListDTO;
import com.jiaruiblog.entity.dto.CommentWithUserDTO;
import com.jiaruiblog.entity.vo.CommentWithUserVO;
import com.jiaruiblog.intercepter.SensitiveFilter;
import com.jiaruiblog.service.ICommentService;
import com.jiaruiblog.service.IUserService;
import com.jiaruiblog.util.BaseApiResult;
import com.mongodb.client.result.DeleteResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ConvertOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @ClassName CommentServiceImpl
 * @Description comment service impl
 * @Author luojiarui
 * @Date 2022/6/4 5:23 下午
 * @Version 1.0
 **/
@Slf4j
@Service
public class CommentServiceImpl implements ICommentService {


    private static final String COLLECTION_NAME = "commentCollection";

    private static final String DOC_ID = "docId";

    private static final String OBJECT_ID = "_id";

    @Resource
    MongoTemplate template;

    @Resource
    IUserService userService;

    @Override
    public BaseApiResult insert(Comment comment) {
        if( !StringUtils.hasText(comment.getUserId()) || !StringUtils.hasText(comment.getUserName())) {
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.PARAMS_IS_NOT_NULL);
        }
        try {
            // 敏感词过滤
            SensitiveFilter filter = SensitiveFilter.getInstance();
            String content = comment.getContent();
            content = filter.replaceSensitiveWord(content, 1,"*");
            comment.setContent(content);
        } catch (IOException e) {
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, e.getLocalizedMessage());
        }


        comment.setCreateDate(new Date());
        comment.setUpdateDate(new Date());
        template.save(comment, COLLECTION_NAME);
        return BaseApiResult.success(MessageConstant.SUCCESS);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public BaseApiResult update(Comment comment) {

        if( !StringUtils.hasText(comment.getUserId()) || !StringUtils.hasText(comment.getUserName())) {
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.PARAMS_IS_NOT_NULL);
        }

        Query query = new Query(Criteria.where("_id").is(comment.getId()));
        Comment commentDb = Optional.ofNullable(template.findById(comment.getId(), Comment.class, COLLECTION_NAME))
                .orElse(new Comment());
        if( !commentDb.getUserId().equals(comment.getUserId())) {
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }

        Update update  = new Update();
        update.set("content", comment.getContent());
        update.set("updateDate", new Date());
        try {
            template.updateFirst(query, update, User.class);
        } catch (Exception e) {
            log.error("更新评论信息{}==>出错==>{}", comment, e);
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
        return BaseApiResult.success(MessageConstant.SUCCESS);
    }

    @Override
    public BaseApiResult remove(Comment comment, String userId) {
        Query query = new Query(Criteria.where(OBJECT_ID).is(comment.getId()));
        Comment commentDb = Optional.ofNullable(template.findById(comment.getId(), Comment.class, COLLECTION_NAME))
                .orElse(new Comment());
        if( !commentDb.getUserId().equals(comment.getUserId())) {
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
        template.remove(query, Comment.class, COLLECTION_NAME);
        return BaseApiResult.success(MessageConstant.SUCCESS);
    }

    /**
     * @Author luojiarui
     * @Description 删除批量的评论列表
     * @Date 20:51 2023/2/12
     * @Param [commentIdList]
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    @Override
    public BaseApiResult removeBatch(List<String> commentIdList) {
        Query query = new Query(Criteria.where(OBJECT_ID).in(commentIdList));
        DeleteResult remove = template.remove(query, Comment.class, COLLECTION_NAME);
        if (remove.getDeletedCount() < commentIdList.size()) {
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
        return BaseApiResult.success(MessageConstant.SUCCESS);
    }

    /**
     * @Author luojiarui
     * @Description 根据文档的id查询相关的评论列表
     * @Date 11:57 2022/9/4
     * @Param [comment]
     * @return com.jiaruiblog.utils.ApiResult
     **/
    @Override
    public BaseApiResult queryById(CommentListDTO comment) {
        if (comment == null || comment.getDocId() == null) {
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.PARAMS_FORMAT_ERROR);
        }
        Query query = new Query(Criteria.where(DOC_ID).is(comment.getDocId()))
                .with(Sort.by(Sort.Direction.DESC, "createDate"));

        Long totalNum = template.count(query, Comment.class, COLLECTION_NAME);
        // 分页查询
        long skip = (long) comment.getPage() * comment.getRows();
        query.skip(skip);
        query.limit(comment.getRows());
        // 这里应该联合查询，根据评论的id查询到评论的用户，再根据用户查询头像信息
        List<Comment> comments = template.find(query, Comment.class, COLLECTION_NAME);
        // 通过comment的id查询用户的头像信息
        List<String> userId = comments.stream().map(Comment::getUserId).collect(Collectors.toList());
        Map<String, String> userAvatarMap = userService.queryUserAvatarBatch(userId);
        List<CommentWithUserVO> commentWithUserVOList = new ArrayList<>();
        for (Comment item : comments) {
            CommentWithUserVO commentWithUserVO = new CommentWithUserVO();
            BeanUtils.copyProperties(item, commentWithUserVO);
            commentWithUserVO.setUserAvatarId(userAvatarMap.get(item.getUserId()));
            commentWithUserVOList.add(commentWithUserVO);
        }

        Map<String, Object> result = Maps.newHashMap();
        result.put("totalNum", totalNum);
        result.put("comments", commentWithUserVOList);

        return BaseApiResult.success(result);
    }

    @Override
    public BaseApiResult search(Comment comment) {
        return BaseApiResult.success(MessageConstant.SUCCESS);
    }

    /**
     * @Author luojiarui
     * @Description //根据文档的id 查询评论的数量
     * @Date 10:47 下午 2022/6/22
     * @Param [docId]
     * @return java.lang.Long
     **/
    @Override
    public Long commentNum(String docId) {
        Query query = new Query().addCriteria(Criteria.where(DOC_ID).is(docId));
        return template.count(query, Comment.class, COLLECTION_NAME);
    }

    /**
     * 根据关键字模糊搜索相关的文档id
     * @param keyWord 关键字
     * @return 文档的id信息
     */
    @Override
    public List<String> fuzzySearchDoc(String keyWord) {
        if(keyWord == null || "".equalsIgnoreCase(keyWord)) {
            return Lists.newArrayList();
        }
        Pattern pattern = Pattern.compile("^.*"+keyWord+".*$", Pattern.CASE_INSENSITIVE);
        Query query = new Query();
        query.addCriteria(Criteria.where("content").regex(pattern));

        List<Comment> comments = template.find(query, Comment.class, COLLECTION_NAME);
        return comments.stream().map(Comment::getDocId).collect(Collectors.toList());

    }

    /**
     * @Author luojiarui
     * @Description 根据文档进行删除评论信息
     * @Date 11:14 上午 2022/6/25
     * @Param [docId]
     **/
    @Override
    public void removeByDocId(String docId) {
        Query query = new Query(Criteria.where(DOC_ID).is(docId));
        template.remove(query, Comment.class, COLLECTION_NAME);
    }

    /**
     * @Author luojiarui
     * @Description // 统计总数
     * @Date 4:40 下午 2022/6/26
     * @Param []
     * @return java.lang.Integer
     **/
    @Override
    public long countAllFile() {
        return template.getCollection(COLLECTION_NAME).estimatedDocumentCount();
    }

    /**
     * @Author luojiarui
     * @Description 分页查询评论信息
     * @Date 14:47 2022/12/10
     * @Param [page, userId]
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    @Override
    public BaseApiResult queryAllComments(BasePageDTO page, String userId, Boolean isAdmin) {

        log.info("查询的参数是：{}, {}", page, userId);
        Criteria criteria = new Criteria();
        if (Boolean.FALSE.equals(isAdmin)) {
            criteria = Criteria.where("userId").is(userId);
        }

        // 通过query进行查找
        Aggregation countAggregation = Aggregation.newAggregation(
                // 选择某些字段
                Aggregation.project("id", "userName", "createDate", "content", "userId", "docId")
                        .and(ConvertOperators.Convert.convertValue("$_id").to("string"))//将主键Id转换为objectId
                        .as("id")
                        .and(ConvertOperators.Convert.convertValue("$docId").to("objectId")).as("docId")
                ,//新字段名称,
                Aggregation.match(criteria)
        );

        Aggregation aggregation = Aggregation.newAggregation(
                // 选择某些字段
                Aggregation.project("id", "userName", "createDate", "content", "userId", "docId")
                        .and(ConvertOperators.Convert.convertValue("$_id").to("string"))//将主键Id转换为objectId
                        .as("id")
                        .and(ConvertOperators.Convert.convertValue("$docId").to("objectId")).as("docId")
                ,//新字段名称,

                Aggregation.lookup(FileServiceImpl.COLLECTION_NAME, "docId", "_id", "abc"),
                Aggregation.sort(Sort.Direction.DESC, "createDate"),
                Aggregation.match(criteria),
                Aggregation.skip((long) (page.getPage()-1) * page.getRows()),
                Aggregation.limit(page.getRows())

        );

        AggregationResults<CommentWithUserDTO> aggregate = template.aggregate(aggregation,
                COLLECTION_NAME, CommentWithUserDTO.class);
        List<CommentWithUserDTO> mappedResults = aggregate.getMappedResults();
        List<CommentWithUserVO> commentWithUserVOList = new ArrayList<>();
        mappedResults.forEach(item -> {
            if (!item.getAbc().isEmpty()) {
                CommentWithUserVO commentWithUserVO = new CommentWithUserVO();
                BeanUtils.copyProperties(item, commentWithUserVO);
                commentWithUserVO.setDocId(item.getAbc().get(0).getId());
                commentWithUserVO.setDocName(item.getAbc().get(0).getName());
                commentWithUserVOList.add(commentWithUserVO);
            }
        });

        int count = template.aggregate(countAggregation, COLLECTION_NAME, CommentWithUserDTO.class).getMappedResults().size();

        Map<String, Object> result = Maps.newHashMap();
        result.put("data", commentWithUserVOList);
        result.put("total", count);
        result.put("pageNum", page.getPage());
        result.put("pageSize", page.getRows());
        return BaseApiResult.success(result);
    }
}
