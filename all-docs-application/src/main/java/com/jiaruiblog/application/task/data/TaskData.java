package com.jiaruiblog.application.task.data;

import com.jiaruiblog.domain.entity.FileDocument;
import com.jiaruiblog.enums.FileFormatEnum;

/**
 * @author Jarrett Luo
 * @Date 2022/10/26 17:30
 * @Version 1.0
 */
public class TaskData {

    private FileDocument fileDocument;
    private String txtFilePath;
    private String thumbFilePath;
    private String previewFilePath;
    private FileFormatEnum docType;

    public FileDocument getFileDocument() { return fileDocument; }
    public void setFileDocument(FileDocument fileDocument) { this.fileDocument = fileDocument; }
    public String getTxtFilePath() { return txtFilePath; }
    public void setTxtFilePath(String txtFilePath) { this.txtFilePath = txtFilePath; }
    public String getThumbFilePath() { return thumbFilePath; }
    public void setThumbFilePath(String thumbFilePath) { this.thumbFilePath = thumbFilePath; }
    public String getPreviewFilePath() { return previewFilePath; }
    public void setPreviewFilePath(String previewFilePath) { this.previewFilePath = previewFilePath; }
    public FileFormatEnum getDocType() { return docType; }
    public void setDocType(FileFormatEnum docType) { this.docType = docType; }
}