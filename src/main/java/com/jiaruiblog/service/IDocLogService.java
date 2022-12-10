package com.jiaruiblog.service;

import com.jiaruiblog.entity.BasePageDTO;
import com.jiaruiblog.util.BaseApiResult;

import java.util.List;

/**
 * @ClassName IDocLogService
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/12/10 11:04
 * @Version 1.0
 **/
public interface IDocLogService {

    /**
     * @Author luojiarui
     * @Description 分页查询文档日志
     * @Date 20:57 2022/11/30
     * @Param [page, user]
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    BaseApiResult queryDocLogs(BasePageDTO page, String userId);

    /**
     * @Author luojiarui
     * @Description 批量删除文档的日志
     * @Date 20:57 2022/11/30
     * @Param [ids] 文档的id列表
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    BaseApiResult deleteDocLogBatch(List<String> logIds, String userId);

}
