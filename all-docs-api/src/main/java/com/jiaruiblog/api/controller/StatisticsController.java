package com.jiaruiblog.api.controller;

import com.jiaruiblog.application.service.*;
import com.jiaruiblog.application.service.impl.DocumentServiceImpl;
import com.jiaruiblog.application.service.impl.RedisServiceImpl;
import com.jiaruiblog.common.ApiResult;
import com.jiaruiblog.common.exception.BusinessException;
import com.jiaruiblog.common.exception.ErrorCode;
import com.jiaruiblog.domain.entity.FileDocument;
import com.jiaruiblog.domain.entity.Tag;
import com.jiaruiblog.domain.entity.TagDocRelationship;
import com.jiaruiblog.domain.entity.dto.SearchKeyDTO;
import com.jiaruiblog.domain.entity.vo.DocumentVO;
import com.jiaruiblog.domain.entity.vo.StatsVO;
import com.jiaruiblog.domain.entity.vo.TrendVO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author luojiarui
 * @ClassName StatisticsController
 * @Description 统计模块
 * @Date 2022/6/26 2:24 下午
 * @Version 1.0
 **/
@RestController
@Slf4j
@CrossOrigin
@RequestMapping("/api/v1/statistics")
public class StatisticsController {

    @Resource
    StatisticsService statisticsService;

    @Resource
    RedisService redisService;

    @Resource
    DocumentService fileService;

    @Resource
    DocumentServiceImpl documentServiceImpl;

    @Resource
    TagService tagService;

    @Resource
    ElasticService elasticService;

    @Operation(summary = "查询热度榜", description = "查询列表")
    @GetMapping(value = "/trend")
    public ApiResult<List<TrendVO>> trend() {
        return ApiResult.success(statisticsService.trend());
    }

    @Operation(summary = "查询统计数据", description = "查询列表")
    @GetMapping(value = "/all")
    public ApiResult<StatsVO> all() {
        return ApiResult.success(statisticsService.all());
    }

    /**
     * @return com.jiaruiblog.utils.ApiResult
     * @author luojiarui
     * 查询推荐的搜索记录
     **/
    @Operation(summary = "查询搜索结果", description = "查询列表")
    @GetMapping("getSearchResult")
    public ApiResult<Map<String, List<String>>> getSearchResult(@RequestHeader HttpHeaders headers) {
        List<String> userSearchList = Lists.newArrayList();
        List<String> stringList = headers.get("id");
        if (!CollectionUtils.isEmpty(stringList)) {
            String userId = stringList.get(0);
            if (StringUtils.hasText(userId)) {
                userSearchList = redisService.getSearchHistoryByUserId(userId);
            }
        }

        List<String> hotSearchList = redisService.getHotList(null, RedisServiceImpl.SEARCH_KEY);
        Map<String, List<String>> result = new HashMap<>();
        result.put("userSearch", userSearchList);
        result.put("hotSearch", hotSearchList);
        return ApiResult.success(result);
    }

    @Operation(summary = "删除用户的搜索关键词", description = "删除key")
    @PutMapping(value = "removeKey")
    public ApiResult<Long> removeKey(@RequestBody SearchKeyDTO searchKeyDTO) {
        Long result = redisService.delSearchHistoryByUserId(searchKeyDTO.getUserId(), searchKeyDTO.getSearchWord());
        return ApiResult.success(result);
    }

    /**
     * 优化一下，按照指定的顺序进行提取，先无脑取回来，然后再进行排序
     * List<FileDocument> fileDocumentList = fileService.listAndFilterByPageNotSort(0, docIdList.size(), docIdList);
     * <p>
     * 存储无效的redis id
     * List<String> invalidDocs = Lists.newArrayList();
     * <p>
     * 批量从redis中删除
     * invalidDocs.add(s);
     *
     * @return com.jiaruiblog.utils.ApiResult
     * @author luojiarui
     **/
    @Operation(summary = "查询十条热门榜单", description = "查询列表")
    @GetMapping("getHotTrend")
    public ApiResult<Map<String, Object>> getHotTrend() {
        List<String> docIdList = redisService.getHotList(null, RedisServiceImpl.DOC_KEY);

        if (CollectionUtils.isEmpty(docIdList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }


        List<FileDocument> fileDocumentList = Lists.newArrayList();
        for (String s : docIdList) {
            FileDocument fileDocument = fileService.queryById(s);
            if (fileDocument != null) {
                fileDocumentList.add(fileDocument);
            } else {
                redisService.deleteKey(s);
            }
        }
        // 从redis中删除无效id
        if (CollectionUtils.isEmpty(fileDocumentList)) {
            throw new BusinessException(ErrorCode.DOCUMENT_NOT_FOUND);
        }
        FileDocument topFileDocument = fileDocumentList.remove(0);
        DocumentVO documentVO = documentServiceImpl.convertDocument(null, topFileDocument);
        Map<String, Object> top1 = new HashMap<>();
        top1.put("name", topFileDocument.getName());
        top1.put("id", topFileDocument.getId());
        top1.put("commentNum", documentVO.getCommentNum());
        top1.put("collectNum", documentVO.getCollectNum());
        top1.put("likeNum", (int) Math.round(redisService.score(RedisServiceImpl.DOC_KEY, topFileDocument.getId())));
        top1.put("thumbId", topFileDocument.getThumbId());


        List<Object> others = new ArrayList<>();
        int count = 10;
        for (FileDocument fileDocument : fileDocumentList) {
            Map<String, Object> otherInfo = new HashMap<>();
            otherInfo.put("hit", count);
            otherInfo.put("name", fileDocument.getName());
            otherInfo.put("id", fileDocument.getId());
            count--;
            others.add(otherInfo);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("top1", top1);
        result.put("others", others);

        return ApiResult.success(result);
    }


    /**
     * @return com.jiaruiblog.utils.ApiResult
     * @author luojiarui
     * 获取首页最近的数据
     * 展示1、最近新提交的12篇文章；2、获取最近新连接关系的文档；
     **/
    @Operation(summary = "查询最新数据", description = "查询列表展示1、最近新提交的12篇文章；2、获取最近新连接关系的文档")
    @GetMapping("/recentDocs")
    public ApiResult<List<Map<String, Object>>> getRecentDocs() {
        List<Map<String, Object>> result = Lists.newArrayList();

        List<FileDocument> recentFileDocuments = fileService.listFilesByPage(0, 12);
        List<Map<String, Object>> recentMap = doc2Map(recentFileDocuments);
        result.add(getTagMap("最近的文档", "tagId", recentMap));

        Map<Tag, List<TagDocRelationship>> tagDocMap = tagService.getRecentTagRelationship();

        for (Map.Entry<Tag, List<TagDocRelationship>> tagListEntry : tagDocMap.entrySet()) {
            Tag tag = tagListEntry.getKey();
            List<String> docIdList = tagListEntry.getValue()
                    .stream().map(TagDocRelationship::getFileId).collect(Collectors.toList());
            List<FileDocument> tagFileDocument = fileService.listAndFilterByPage(0, 12, docIdList);
            List<Map<String, Object>> map = doc2Map(tagFileDocument);
            result.add(getTagMap(tag.getName(), tag.getId(), map));
        }

        return ApiResult.success(result);
    }

    /**
     * @return java.util.List<java.util.Map < java.lang.String, java.lang.Object>>
     * @author luojiarui
     * 文档列表转向为map
     **/
    private List<Map<String, Object>> doc2Map(List<FileDocument> fileDocuments) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(fileDocuments)) {
            return result;
        }

        for (FileDocument fileDocument : fileDocuments) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", fileDocument.getName());
            map.put("id", fileDocument.getId());
            map.put("thumbId", fileDocument.getThumbId());
            result.add(map);
        }
        return result;
    }

    /**
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author luojiarui
     * 生成返回的数据
     **/
    private Map<String, Object> getTagMap(String name, String tagId, Object docList) {
        Map<String, Object> tagMap = new HashMap<>();
        if (name == null || tagId == null || docList == null) {
            return tagMap;
        }
        tagMap.put("name", name);
        tagMap.put("tagId", tagId);
        tagMap.put("docList", docList);
        return tagMap;
    }

    @GetMapping("monthStat")
    public ApiResult<Object> getMonthStat() {
        return ApiResult.success(statisticsService.getMonthStat());
    }

    @GetMapping("")
    public ApiResult<Object> getWordStat() {
        return ApiResult.success(elasticService.getWordStat());
    }
}