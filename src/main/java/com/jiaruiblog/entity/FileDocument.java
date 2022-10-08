package com.jiaruiblog.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @author jiarui.luo
 */
@Data
@Document
public class FileDocument {

    /**
     * 主键
     */
    @Id
    private String id;

    /**
     * 文件名称
     */
    private String name;

    /**
     * 文件大小
     */
    private long size;

    /**
     * 上传时间
     */
    private Date uploadDate;

    /**
     * 文件MD5值
     */
    private String md5;

    /**
     * 文件内容
     */
    private byte[] content;

    /**
     * 文件类型
     */
    private String contentType;

    /**
     * 文件后缀名
     */
    private String suffix;

    /**
     * 文件描述
     */
    private String description;

    /**
     * 大文件管理GridFS的ID
     */
    private String gridfsId;

    /**
     * 预览图的GridFS的ID
     */
    private String thumbId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getGridfsId() {
        return gridfsId;
    }

    public void setGridfsId(String gridfsId) {
        this.gridfsId = gridfsId;
    }


    public FileDocument() {
    }

    public FileDocument(String id, String name, long size, Date uploadDate, String md5, byte[] content, String contentType, String suffix, String description, String gridfsId) {
        this.id = id;
        this.name = name;
        this.size = size;
        this.uploadDate = uploadDate;
        this.md5 = md5;
        this.content = content;
        this.contentType = contentType;
        this.suffix = suffix;
        this.description = description;
        this.gridfsId = gridfsId;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        FileDocument fileInfo = (FileDocument) object;
        return java.util.Objects.equals(size, fileInfo.size)
                && java.util.Objects.equals(name, fileInfo.name)
                && java.util.Objects.equals(description, fileInfo.description)
                && java.util.Objects.equals(uploadDate, fileInfo.uploadDate)
                && java.util.Objects.equals(md5, fileInfo.md5)
                && java.util.Objects.equals(id, fileInfo.id);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(name, description, size, uploadDate, md5, id);
    }

    @Override
    public String toString() {
        return "FileDocument{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", size=" + size +
                ", uploadDate=" + uploadDate +
                ", md5='" + md5 + '\'' +
                ", content=" + content +
                ", contentType='" + contentType + '\'' +
                ", suffix='" + suffix + '\'' +
                ", description='" + description + '\'' +
                ", gridfsId='" + gridfsId + '\'' +
                '}';
    }
}
