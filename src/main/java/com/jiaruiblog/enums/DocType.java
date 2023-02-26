package com.jiaruiblog.enums;

/**
 * @Author Jarrett Luo
 * @Date 2022/10/24 11:39
 * @Version 1.0
 */
public enum DocType {

    // PDF文档
    PDF,
    // word文档
    DOCX,
    PPTX,
    XLSX,
    // txt类的文档
    HTML,
    MD,
    TXT,
    // unknown
    UNKNOWN;

    public static DocType getDocType(String suffixName) {
        switch (suffixName) {
            case ".pdf":
                return PDF;
            case ".docx":
                return DOCX;
            case ".pptx":
                return PPTX;
            case ".xlsx":
                return XLSX;
            case ".md":
                return MD;
            case ".html":
                return HTML;
            case ".txt":
                return TXT;
            default:
                return UNKNOWN;
        }
    }
}
