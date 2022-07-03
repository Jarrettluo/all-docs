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
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/7/3 10:47 下午
 * @Version 1.0
 **/
@Data
@Document(indexName = "docwrite", createIndex = true)
public class FileObj {

    @Id
    @Field(type = FieldType.Keyword)
    String id; //用于存储文件id

    @Field(type = FieldType.Text, analyzer="ik_max_word")
    String name; //文件名

    @Field(type = FieldType.Keyword)
    String type; //文件的type，pdf，word，or txt

    @Field(type = FieldType.Text, analyzer="ik_smart")
    String content; //文件转化成base64编码后所有的内容。


    public FileObj readFile(String path) throws IOException {
        //读文件
        File file = new File(path);

        FileObj fileObj = new FileObj();
        fileObj.setName(file.getName());
        fileObj.setType(file.getName().substring(file.getName().lastIndexOf(".") + 1));

        byte[] bytes = getContent(file);

        //将文件内容转化为base64编码
        String base64 = Base64.getEncoder().encodeToString(bytes);
        fileObj.setContent(base64);

        return fileObj;
    }

    private byte[] getContent(File file) {
        FileInputStream fileInputStream = null;
        byte[] bytesArray = null;

        try {
            bytesArray = new byte[(int) file.length()];

            //read file into bytes[]
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytesArray);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return bytesArray;
    }

}
