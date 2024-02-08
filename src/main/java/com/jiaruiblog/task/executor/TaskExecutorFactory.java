package com.jiaruiblog.task.executor;

import com.jiaruiblog.enums.DocType;

import java.util.EnumMap;
import java.util.Map;

/**
 * @Author Jarrett Luo
 * @Date 2022/10/24 11:27
 * @Version 1.0
 */
public class TaskExecutorFactory {

    private TaskExecutorFactory() {

    }

    static Map<DocType, TaskExecutor> taskExecutorMap = new EnumMap<>(DocType.class);

    public static TaskExecutor getTaskExecutor(DocType docType) {
        TaskExecutor taskExecutor = taskExecutorMap.get(docType);
        if (null != taskExecutor) {
            return taskExecutor;
        }
        return createTaskExecutor(docType);
    }

    /**
     * 创建任务执行器
     * @param docType 文档类型
     * @return 任务执行器
     */
    private static TaskExecutor createTaskExecutor(DocType docType) {
        TaskExecutor taskExecutor = null;
        switch (docType) {
            case PDF:
                taskExecutor = new PdfWordTaskExecutor();
                break;
            case DOCX:
            case XLSX:
                taskExecutor = new DocxExecutor();
                break;
            case PPTX:
                taskExecutor = new PptxExecutor();
                break;
            case MD:
            case HTML:
            case TXT:
                taskExecutor = new TxtExecutor();
                break;
            case JPG:
            case JPEG:
            case PNG:
                taskExecutor = new PicExecutor();
                break;
            default:
                break;
        }

        if (null != taskExecutor) {
            taskExecutorMap.put(docType, taskExecutor);
        }
        return taskExecutor;
    }
}
