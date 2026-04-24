package com.jiaruiblog.application.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * @author Jarrett Luo
 * @Date 2022/6/7 11:39
 * @Version 1.0
 */
public interface FileOperationService {

    /**
     * @author luojiarui
     * @Description 解析文件并返回内容
     * @Date 15:24 2022/11/5
     * @Param [file]
     * @return TextExtractResult
     */
    TextExtractResult parseToStr(MultipartFile file);

    /**
     * @author luojiarui
     * @Description 解析文件并返回内容
     * @Date 15:24 2022/11/5
     * @Param [path]
     * @return TextExtractResult
     */
    TextExtractResult parseToStr(String path);

    /**
     * @author luojiarui
     * @Description 将文件上传到minio中
     * @Date 15:24 2022/11/5
     * @Param [name, inputStream]
     * @return String
     */
    String uploadFile(String name, InputStream inputStream);

    /**
     * @author luojiarui
     * @Description 将文件上传到minio中
     * @Date 15:24 2022/11/5
     * @Param [name, file]
     * @return String
     */
    String uploadFile(String name, File file);

    /**
     * @author luojiarui
     * @Description 将文件上传到minio中
     * @Date 15:24 2022/11/5
     * @Param [name, inputStream, size]
     * @return String
     */
    String uploadFile(String name, InputStream inputStream, long size);

    /**
     * @author luojiarui
     * @Description 将文件上传到minio中
     * @Date 15:24 2022/11/5
     * @Param [name, file, size]
     * @return String
     */
    String uploadFile(String name, File file, long size);

    /**
     * @author luojiarui
     * @Description 根据文件名获取文件流
     * @Date 15:24 2022/11/5
     * @Param [name]
     * @return InputStream
     */
    InputStream getFile(String name);

    /**
     * @author luojiarui
     * @Description 根据文件名获取文件流
     * @Date 15:24 2022/11/5
     * @Param [name, expires]
     * @return InputStream
     */
    InputStream getFile(String name, Integer expires);

    /**
     * @author luojiarui
     * @Description 根据文件名删除文件
     * @Date 15:24 2022/11/5
     * @Param [name]
     */
    void deleteFile(String name);

    /**
     * @author luojiarui
     * @Description 根据文件名获取文件链接
     * @Date 15:24 2022/11/5
     * @Param [name]
     * @return String
     */
    String getFileUrl(String name);

    /**
     * @author luojiarui
     * @Description 根据文件名获取文件链接
     * @Date 15:24 2022/11/5
     * @Param [name, expires]
     * @return String
     */
    String getFileUrl(String name, Integer expires);

    /**
     * @author luojiarui
     * @Description 获取所有文件列表
     * @Date 15:24 2022/11/5
     * @return List<String>
     */
    List<String> listNames();

    /**
     * @author luojiarui
     * @Description 判断文件是否存在
     * @Date 15:24 2022/11/5
     * @Param [name]
     * @return boolean
     */
    boolean isExist(String name);

}