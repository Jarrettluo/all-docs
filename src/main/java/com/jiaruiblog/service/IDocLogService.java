package com.jiaruiblog.service;

import com.jiaruiblog.entity.FileDocument;
import com.jiaruiblog.entity.User;
import com.jiaruiblog.entity.dto.BasePageDTO;
import com.jiaruiblog.service.impl.DocLogServiceImpl;
import com.jiaruiblog.util.BaseApiResult;

import java.util.List;

/**
 * @ClassName IDocLogService
 * @Description 文档日志的信息
 * @Author luojiarui
 * @Date 2022/12/10 11:04
 * @Version 1.0
 **/
public interface IDocLogService {

    /**
     * add logs
     * @Author luojiarui
     * @Description add logs
     * @Date 22:54 2023/1/11
     * @Param [user, document, action]
     **/
    void addLog(User user, FileDocument document, DocLogServiceImpl.Action action);

    /**
     * query Doc Logs
     * @Author luojiarui
     * @Description 分页查询文档日志
     * @Date 20:57 2022/11/30
     * @Param page BasePageDTO
     * @Param user String
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    BaseApiResult queryDocLogs(BasePageDTO page, String userId);

    /**
     * delete doc logs in batches
     * @Author luojiarui
     * @Description 批量删除文档的日志
     * @Date 20:57 2022/11/30
     * @Param ids 文档的id列表
     * @Param userId user index
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    BaseApiResult deleteDocLogBatch(List<String> logIds, String userId);

}
