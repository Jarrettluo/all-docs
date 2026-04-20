package com.jiaruiblog.common.enums;

import com.jiaruiblog.common.converter.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Jarrett Luo
 * @Date 2022/10/24 11:39
 * @Version 1.0
 */
@Getter
@AllArgsConstructor
public enum DocType implements BaseEnum {

    // PDF文档
    PDF(1, ".pdf"),
    // word文档
    DOCX(2, ".docx"),
    PPT(3, ".ppt"),
    PPTX(4, ".pptx"),
    XLSX(5, ".xlsx"),
    // txt类的文档
    HTML(6, ".html"),
    MD(7, ".md"),
    TXT(8, ".txt"),
    // pic类的文档
    JPG(9, ".jpg"),
    JPEG(10, ".jpeg"),
    PNG(11, ".png"),
    // unknown
    UNKNOWN(0, "");

    private final Integer code;
    private final String extension;

    public static DocType getDocType(String suffixName) {
        if (StringUtils.isNoneBlank(suffixName)) {
            suffixName = StringUtils.toRootLowerCase(suffixName);
        }
        switch (suffixName) {
            case ".pdf":
                return PDF;
            case ".docx":
                return DOCX;
            case ".ppt":
                return PPT;
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
