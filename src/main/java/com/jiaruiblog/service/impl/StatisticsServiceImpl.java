package com.jiaruiblog.service.impl;

import com.jiaruiblog.entity.CateDocRelationship;
import com.jiaruiblog.entity.Category;
import com.jiaruiblog.entity.FileDocument;
import com.jiaruiblog.entity.vo.DocVO;
import com.jiaruiblog.entity.vo.StatsVO;
import com.jiaruiblog.entity.vo.TrendVO;
import com.jiaruiblog.service.StatisticsService;
import com.jiaruiblog.utils.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName StatisticsServiceImpl
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/6/26 2:28 下午
 * @Version 1.0
 **/
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    CategoryServiceImpl categoryServiceImpl;

    @Autowired
    FileServiceImpl fileServiceImpl;

    @Autowired
    TagServiceImpl tagServiceImpl;

    @Autowired
    CommentServiceImpl commentServiceImpl;


    /**
     * @Author luojiarui
     * @Description // 统计随机的三个分类
     * @Date 2:29 下午 2022/6/26
     * @Param []
     * @return com.jiaruiblog.utils.ApiResult
     **/
    @Override
    public ApiResult trend() {
        List<Category> categoryList = categoryServiceImpl.getRandom();
        List<TrendVO> trendVOS = new ArrayList<>(3);

        for (Category category : categoryList) {
            TrendVO trendVO = new TrendVO();
            trendVO.setId(category.getId());
            trendVO.setName(category.getName());
            List<CateDocRelationship> relationships = categoryServiceImpl.getRelateByCateId(category.getId());
            List<String> ids = relationships.stream().map(CateDocRelationship::getFileId).collect(Collectors.toList());
            List<FileDocument> documents = fileServiceImpl.listAndFilterByPage(0, 7, ids);
            List<DocVO> docVOS = new ArrayList<>();
            for (FileDocument document : documents) {
                DocVO docVO = new DocVO();
                docVO.setId(document.getId());
                docVO.setName(document.getName());
                docVOS.add(docVO);
            }
            trendVO.setDocList(docVOS);
            trendVOS.add(trendVO);
        }
        return ApiResult.success(trendVOS);
    }

    /**
     * @Author luojiarui
     * @Description // 统计数量
     * @Date 2:29 下午 2022/6/26
     * @Param []
     * @return com.jiaruiblog.utils.ApiResult
     **/
    @Override
    public ApiResult all() {
        StatsVO statsVO = new StatsVO();
        statsVO.setDocNum(fileServiceImpl.countAllFile());
        statsVO.setCommentNum(commentServiceImpl.countAllFile());
        statsVO.setCategoryNum(categoryServiceImpl.countAllFile());
        statsVO.setTagNum(tagServiceImpl.countAllFile());
        return ApiResult.success(statsVO);
    }

}
