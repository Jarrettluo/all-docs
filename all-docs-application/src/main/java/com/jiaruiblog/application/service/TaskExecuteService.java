package com.jiaruiblog.application.service;

import com.jiaruiblog.domain.entity.FileDocument;
import com.jiaruiblog.common.enums.DocType;

import java.io.InputStream;

/**
 * @author Jarrett Luo
 * @Date 2022/6/7 11:38
 * @Version 1.0
 */
public interface TaskExecuteService {

    /**
     * @author luojiarui
     * @Description 执行任务
     * @Date 16:48 2022/11/5
     * @Param [document]
     */
    void execute(FileDocument document);

    /**
     * @author luojiarui
     * @Description 根据文档类型获取执行器类型
     * @Date 16:48 2022/11/5
     * @Param [docType]
     * @return java.lang.String
     */
    String getExecutorType(DocType docType);

    /**
     * @author luojiarui
     * @Description 上传文件并执行任务
     * @Date 16:48 2022/11/5
     * @Param [inputStream, fileName, contentType]
     * @return java.lang.String
     */
    String uploadAndExecute(InputStream inputStream, String fileName, String contentType);
}