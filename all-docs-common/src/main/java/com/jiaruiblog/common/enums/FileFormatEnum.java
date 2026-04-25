package com.jiaruiblog.common.enums;

import com.jiaruiblog.common.converter.BaseEnum;

/**
 * @author jiarui.luo
 */
public enum FileFormatEnum implements BaseEnum {

    PDF(1, "pdf"),
    XLS(2, "xls"),
    XLSX(3, "xlsx"),
    DOC(4, "doc"),
    DOCX(5, "docx"),
    PPT(6, "ppt"),
    PPTX(7, "pptx"),
    MD(8, "markdown"),
    PNG(9, "png", "png_", "image/png"),
    JPEG(10, "jpeg"),
    TEXT(11, "txt", "txt_", "text/plain"),
    HTML(12, "html"),
    JPG(13, "jpg");

    private final Integer code;
    private final String description;
    private final String filePrefix;
    private final String contentType;

    FileFormatEnum(int code, String description) {
        this(code, description, null, null);
    }

    FileFormatEnum(int code, String description, String filePrefix, String contentType) {
        this.code = code;
        this.description = description;
        this.filePrefix = filePrefix;
        this.contentType = contentType;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }

    public String getDescription() {
        return this.description;
    }

    public String getFilePrefix() {
        return this.filePrefix;
    }

    public String getContentType() {
        return this.contentType;
    }

    public static FileFormatEnum getDocType(String suffixName) {
        if (suffixName == null) {
            return null;
        }
        String lower = suffixName.toLowerCase(java.util.Locale.ROOT);
        switch (lower) {
            case ".pdf": return PDF;
            case ".doc": return DOC;
            case ".docx": return DOCX;
            case ".ppt": return PPT;
            case ".pptx": return PPTX;
            case ".xls": return XLS;
            case ".xlsx": return XLSX;
            case ".md": return MD;
            case ".html": case ".xhtml": case ".xht": case ".htm": return HTML;
            case ".txt": return TEXT;
            case ".jpeg": return JPEG;
            case ".jpg": return JPG;
            case ".png": return PNG;
            default: return null;
        }
    }
}