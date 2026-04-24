package com.jiaruiblog.application.task.executor;

import com.jiaruiblog.enums.FileFormatEnum;

/**
 * @author Jarrett Luo
 * @Date 2022/10/24 11:27
 * @Version 1.0
 */
public class TaskExecutorFactory {

    private TaskExecutorFactory() {}

    public static TaskExecutor getTaskExecutor(FileFormatEnum format) {
        if (format == null) return null;
        switch (format) {
            case PDF: return new PdfWordTaskExecutor();
            case DOCX:
            case XLSX:
            case DOC: return new DocxExecutor();
            case PPT: return new com.jiaruiblog.application.task.executor.slider.PptExecutor();
            case PPTX: return new com.jiaruiblog.application.task.executor.slider.PptxExecutor();
            case MD:
            case HTML:
            case TEXT: return new TxtExecutor();
            case JPG:
            case JPEG:
            case PNG: return new PicExecutor();
            default: return null;
        }
    }
}