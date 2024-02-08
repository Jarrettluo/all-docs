package com.jiaruiblog.enums;

import org.apache.commons.lang3.StringUtils;

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
    // pic类的文档
    JPG,
    JPEG,
    PNG,
    // unknown
    UNKNOWN;

    public static DocType getDocType(String suffixName) {
        if (StringUtils.isNoneBlank(suffixName)) {
            suffixName = StringUtils.toRootLowerCase(suffixName);
        }
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
            case ".xhtml":
            case ".xht":
            case ".htm":
                return HTML;
            case ".txt":
                return TXT;
            case ".jpeg":
                return JPEG;
            case ".jpg":
                return JPG;
            case ".png":
                return PNG;
            default:
                return UNKNOWN;
        }
    }
}
