package com.jiaruiblog.domain.entity;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

/**
 * @ClassName FileObj
 * @Description FileObj
 * @author luojiarui
 * @Date 2022/7/3 10:47 下午
 * @Version 1.0
 **/
@Slf4j
@Data
public class FileObj {

    /**
     * 用于存储文件id
     */
    @Id
    @Field(type = FieldType.Keyword)
    private String id;

    /**
     * 文件名
     */
    @Field(type = FieldType.Text, analyzer="ik_max_word")
    private String name;

    /**
     * 文件的type，pdf，word，or txt
     */
    @Field(type = FieldType.Keyword)
    private String type;

    /**
     * 文件转化成base64编码后所有的内容。
     */
    @Field(type = FieldType.Text, analyzer="ik_smart")
    private String content;


    public void readFile(String path) throws IOException {
        //读文件
        File file = new File(path);
        byte[] bytes = getContent(file);
        //将文件内容转化为base64编码
        this.content = Base64.getEncoder().encodeToString(bytes);
    }

    public void readFile(InputStream inputStream) throws IOException {
        byte[] bytes = getContent(inputStream);
        //将文件内容转化为base64编码
        this.content = Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * @deprecated 大文件不应使用byte[]加载到内存，应使用GridFS streaming
     */
    @Deprecated
    private byte[] getContent(File file) throws IOException {
        long fileLength = file.length();
        if (fileLength > Integer.MAX_VALUE) {
            throw new IllegalStateException("File too large to load into memory: " + file.getName());
        }
        byte[] bytesArray = new byte[(int) fileLength];
        try (FileInputStream fileInputStream = new FileInputStream(file)){
            if (fileInputStream.read(bytesArray) < 0) {
                return bytesArray;
            }
        } catch (IOException e) {
            log.error("Failed to read file: {}", file.getName(), e);
            throw e;
        }
        return bytesArray;
    }

    /**
     * @deprecated 大文件不应使用byte[]加载到内存，应使用GridFS streaming
     */
    @Deprecated
    private byte[] getContent(InputStream inputStream) throws IOException {
        try (BufferedInputStream bis = new BufferedInputStream(inputStream);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("Failed to read input stream", e);
            throw e;
        }
    }

}