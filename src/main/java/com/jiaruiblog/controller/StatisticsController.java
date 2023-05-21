package com.jiaruiblog.controller;

import com.google.common.collect.Maps;
import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.entity.FileDocument;
import com.jiaruiblog.entity.Tag;
import com.jiaruiblog.entity.TagDocRelationship;
import com.jiaruiblog.entity.dto.SearchKeyDTO;
import com.jiaruiblog.entity.vo.DocumentVO;
import com.jiaruiblog.service.*;
import com.jiaruiblog.service.impl.FileServiceImpl;
import com.jiaruiblog.service.impl.RedisServiceImpl;
import com.jiaruiblog.util.BaseApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName StatisticsController
 * @Description 统计模块
 * @Author luojiarui
 * @Date 2022/6/26 2:24 下午
 * @Version 1.0
 **/
@Api(tags = "统计模块")
@RestController
@Slf4j
@CrossOrigin
@RequestMapping("/statistics")
public class StatisticsController {

    @Resource
    StatisticsService statisticsService;

    @Resource
    RedisService redisService;

    @Resource
    IFileService fileService;

    @Resource
    FileServiceImpl fileServiceImpl;

    @Resource
    TagService tagService;

    @Resource
    ElasticService elasticService;

    @ApiOperation(value = "查询热度榜", notes = "查询列表")
    @GetMapping(value = "/trend")
    public BaseApiResult trend() {
        return statisticsService.trend();
    }

    @ApiOperation(value = "查询统计数据", notes = "查询列表")
    @GetMapping(value = "/all")
    public BaseApiResult all() {
        return statisticsService.all();
    }

    /**
     * @return com.jiaruiblog.utils.ApiResult
     * @Author luojiarui
     * @Description 查询推荐的搜索记录
     * @Date 15:46 2022/9/11
     * @Param []
     **/
    @ApiOperation(value = "查询搜索结果", notes = "查询列表")
    @GetMapping("getSearchResult")
    public BaseApiResult getSearchResult(@RequestHeader HttpHeaders headers) {
        List<String> userSearchList = Lists.newArrayList();
        List<String> stringList = headers.get("id");
        if (!CollectionUtils.isEmpty(stringList)) {
            String userId = stringList.get(0);
            if (StringUtils.hasText(userId)) {
                userSearchList = redisService.getSearchHistoryByUserId(userId);
            }
        }

        List<String> hotSearchList = redisService.getHotList(null, RedisServiceImpl.SEARCH_KEY);
        Map<String, List<String>> result = Maps.newHashMap();
        result.put("userSearch", userSearchList);
        result.put("hotSearch", hotSearchList);
        return BaseApiResult.success(result);
    }

    @ApiOperation(value = "删除用户的搜索关键词", notes = "删除key")
    @PutMapping(value = "removeKey")
    public BaseApiResult removeKey(@RequestBody SearchKeyDTO searchKeyDTO) {
        redisService.delSearchHistoryByUserId(searchKeyDTO.getUserId(), searchKeyDTO.getSearchWord());
        return BaseApiResult.success();
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
     * @Author luojiarui
     * @Description 查看热榜
     * @Date 15:51 2022/9/11
     * @Param []
     **/
    @ApiOperation(value = "查询十条热门榜单", notes = "查询列表")
    @GetMapping("getHotTrend")
    public BaseApiResult getHotTrend() {
        List<String> docIdList = redisService.getHotList(null, RedisServiceImpl.DOC_KEY);

        if (CollectionUtils.isEmpty(docIdList)) {
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }


        List<FileDocument> fileDocumentList = Lists.newArrayList();
        for (String s : docIdList) {
            FileDocument fileDocument = fileService.queryById(s);
            if (fileDocument != null) {
                fileDocumentList.add(fileDocument);
            } else {
                redisService.delKey(s, RedisServiceImpl.DOC_KEY);
            }
        }
        // 从redis中删除无效id

        if (CollectionUtils.isEmpty(fileDocumentList)) {
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
        FileDocument topFileDocument = fileDocumentList.remove(0);
        DocumentVO documentVO = fileServiceImpl.convertDocument(null, topFileDocument);
        Map<String, Object> top1 = Maps.newHashMap();
        top1.put("name", topFileDocument.getName());
        top1.put("id", topFileDocument.getId());
        top1.put("commentNum", documentVO.getCommentNum());
        top1.put("collectNum", documentVO.getCollectNum());
        top1.put("likeNum", (int) Math.round(redisService.score(RedisServiceImpl.DOC_KEY, topFileDocument.getId())));
        top1.put("thumbId", topFileDocument.getThumbId());


        List<Object> others = new ArrayList<>();
        int count = 10;
        for (FileDocument fileDocument : fileDocumentList) {
            Map<String, Object> otherInfo = Maps.newHashMap();
            otherInfo.put("hit", count);
            otherInfo.put("name", fileDocument.getName());
            otherInfo.put("id", fileDocument.getId());
            count--;
            others.add(otherInfo);
        }

        Map<String, Object> result = Maps.newHashMap();
        result.put("top1", top1);
        result.put("others", others);

        return BaseApiResult.success(result);
    }


    /**
     * @return com.jiaruiblog.utils.ApiResult
     * @Author luojiarui
     * @Description 获取首页最近的数据
     * 展示1、最近新提交的12篇文章；2、获取最近新连接关系的文档；
     * @Date 21:58 2022/9/17
     * @Param []
     **/
    @ApiOperation(value = "查询最新数据", notes = "查询列表展示1、最近新提交的12篇文章；2、获取最近新连接关系的文档")
    @GetMapping("/recentDocs")
    public BaseApiResult getRecentDocs() {
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

        return BaseApiResult.success(result);
    }

    /**
     * @return java.util.List<java.util.Map < java.lang.String, java.lang.Object>>
     * @Author luojiarui
     * @Description 文档列表转向为map
     * @Date 22:47 2022/9/17
     * @Param [fileDocuments]
     **/
    private List<Map<String, Object>> doc2Map(List<FileDocument> fileDocuments) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(fileDocuments)) {
            return result;
        }

        for (FileDocument fileDocument : fileDocuments) {
            Map<String, Object> map = Maps.newHashMap();
            map.put("name", fileDocument.getName());
            map.put("id", fileDocument.getId());
            map.put("thumbId", fileDocument.getThumbId());
            result.add(map);
        }
        return result;
    }

    /**
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @Author luojiarui
     * @Description 生成返回的数据
     * @Date 23:07 2022/9/17
     * @Param [name, tagId, docList]
     **/
    private Map<String, Object> getTagMap(String name, String tagId, Object docList) {
        Map<String, Object> tagMap = Maps.newHashMap();
        if (name == null || tagId == null || docList == null) {
            return tagMap;
        }
        tagMap.put("name", name);
        tagMap.put("tagId", tagId);
        tagMap.put("docList", docList);
        return tagMap;
    }

    @GetMapping("monthStat")
    private BaseApiResult getMonthStat() {
        return statisticsService.getMonthStat();
    }

    @GetMapping("")
    private BaseApiResult getWordStat() throws IOException {
        return elasticService.getWordStat();
    }
}
