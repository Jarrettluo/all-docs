package com.jiaruiblog.enums;

import com.jiaruiblog.utils.enumsCoverterUtils.BaseEnum;

public enum FileFormatEnum implements BaseEnum {

    PDF(1, "pdf"),
    XLS(2, "xls"),
    XLSX(3, "xlsx"),
    DOC(4, "doc"),
    DOCX(5, "docx"),
    PPT(6, "ppt"),
    PPTX(7, "pptx"),
    MD(8, "markdown"),
    PNG(9, "png"),
    JPEG(10, "JPEG");


    private Integer code;

    private String description;

    FileFormatEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }

    public String getDescription() {
        return this.description;
    }
}
