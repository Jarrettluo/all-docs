package com.jiaruiblog.controller;

import com.google.common.collect.Maps;
import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.entity.FileDocument;
import com.jiaruiblog.entity.vo.DocumentVO;
import com.jiaruiblog.service.IFileService;
import com.jiaruiblog.service.RedisService;
import com.jiaruiblog.service.StatisticsService;
import com.jiaruiblog.service.impl.FileServiceImpl;
import com.jiaruiblog.service.impl.RedisServiceImpl;
import com.jiaruiblog.utils.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @Autowired
    StatisticsService statisticsService;

    @Autowired
    RedisService redisService;

    @Autowired
    IFileService fileService;

    @Autowired
    FileServiceImpl fileServiceImpl;

    @ApiOperation(value = "4.1 查询热度榜", notes = "查询列表")
    @GetMapping(value = "/trend")
    public ApiResult trend(){
        return statisticsService.trend();
    }

    @ApiOperation(value = "4.2 查询统计数据", notes = "查询列表")
    @GetMapping(value = "/all")
    public ApiResult all(){
        return statisticsService.all();
    }

    /**
     * @Author luojiarui
     * @Description 查询推荐的搜索记录
     * @Date 15:46 2022/9/11
     * @Param []
     * @return com.jiaruiblog.utils.ApiResult
     **/
    @GetMapping("getSearchResult")
    public ApiResult getSearchResult(HttpServletRequest request){
        String userId = (String) request.getAttribute("id");
        List<String> userSearchList = Lists.newArrayList();
        if ( userId != null && userId != "") {
            userSearchList = redisService.getSearchHistoryByUserId("");
        }
        List<String> hotSearchList = redisService.getHotList(null, RedisServiceImpl.SEARCH_KEY);
        Map<String, List<String>> result = Maps.newHashMap();
        result.put("userSearch", userSearchList);
        result.put("hotSearch", hotSearchList);
        return ApiResult.success(result);
    }

    /**
     * @Author luojiarui
     * @Description 查看热榜
     * @Date 15:51 2022/9/11
     * @Param []
     * @return com.jiaruiblog.utils.ApiResult
     **/
    @GetMapping("getHotTrend")
    public ApiResult getHotTrend() {
        List<String> docIdList = redisService.getHotList(null, RedisServiceImpl.DOC_KEY);
        if (CollectionUtils.isEmpty(docIdList)) {
            return ApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
        List<FileDocument> fileDocumentList = fileService.listAndFilterByPage(0, docIdList.size(), docIdList);
        if ( CollectionUtils.isEmpty(fileDocumentList)) {
            return ApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
        FileDocument topFileDocument = fileDocumentList.remove(0);
        DocumentVO documentVO = fileServiceImpl.convertDocument(null, topFileDocument);
        Map<String, Object> top1 = Maps.newHashMap();
        top1.put("name", topFileDocument.getName());
        top1.put("id", topFileDocument.getId());
        top1.put("commentNum", documentVO.getCommentNum());
        top1.put("collectNUm", documentVO.getCollectNum());
        top1.put("likeNum", redisService.score(topFileDocument.getId(), RedisServiceImpl.DOC_KEY));


        List<Object> others = new ArrayList<>();
        int count = 10;
        for (FileDocument fileDocument : fileDocumentList) {
            Map<String, Object> otherInfo = Maps.newHashMap();
            otherInfo.put("hit", count);
            otherInfo.put("name", fileDocument.getName());
            otherInfo.put("id", fileDocument.getId());
            count --;
            others.add(otherInfo);
        }

        Map<String, Object> result = Maps.newHashMap();
        result.put("top1", top1);
        result.put("others", others);

        return ApiResult.success(result);
    }
}
