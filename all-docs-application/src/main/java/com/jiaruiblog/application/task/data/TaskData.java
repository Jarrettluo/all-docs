package com.jiaruiblog.application.task.data;

import com.jiaruiblog.domain.entity.po.FileDocument;
import com.jiaruiblog.common.enums.FileFormatEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Jarrett Luo
 * @Date 2022/10/26 17:30
 * @Version 1.0
 */
@Setter
@Getter
public class TaskData {

    private FileDocument fileDocument;

    private String txtFilePath;

    private String thumbFilePath;

    private String previewFilePath;

    private FileFormatEnum docType;

}