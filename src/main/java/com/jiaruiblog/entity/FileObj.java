package com.jiaruiblog.entity;

import lombok.Data;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * @ClassName FileObj
 * @Description FileObj
 * @Author luojiarui
 * @Date 2022/7/3 10:47 下午
 * @Version 1.0
 **/
@Data
@Document(indexName = "docwrite")
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


    public void readFile(String path){
        //读文件
        File file = new File(path);
        byte[] bytes = getContent(file);
        //将文件内容转化为base64编码
        this.content = Base64.getEncoder().encodeToString(bytes);
    }

    private byte[] getContent(File file) {
        byte[] bytesArray = new byte[(int) file.length()];
        try (FileInputStream fileInputStream = new FileInputStream(file)){
            if (fileInputStream.read(bytesArray) < 0) {
                return bytesArray;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytesArray;
    }

}
