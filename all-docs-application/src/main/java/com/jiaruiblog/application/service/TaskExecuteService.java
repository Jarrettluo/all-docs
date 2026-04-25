package com.jiaruiblog.application.service;

import com.jiaruiblog.domain.entity.FileDocument;
import com.jiaruiblog.common.enums.FileFormatEnum;

import java.io.InputStream;

/**
 * @author Jarrett Luo
 * @Date 2022/6/7 11:38
 * @Version 1.0
 */
public interface TaskExecuteService {

    void execute(FileDocument document);

    String getExecutorType(FileFormatEnum docType);

    String uploadAndExecute(InputStream inputStream, String fileName, String contentType);
}