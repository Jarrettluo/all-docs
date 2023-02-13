package com.jiaruiblog.service;

import com.jiaruiblog.entity.FileDocument;

/**
 * @Author Jarrett Luo
 * @Date 2022/10/20 18:03
 * @Version 1.0
 */
public interface TaskExecuteService {

    /**
     * 任务执行的入口
     * @param fileDocument 文档信息的实体
     */
    void execute(FileDocument fileDocument);

}
