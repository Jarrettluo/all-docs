package com.jiaruiblog.service.impl;

import com.jiaruiblog.entity.FileObj;
import com.jiaruiblog.service.FileOperationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * @ClassName FileOperation
 * @Description FileOperationServiceImpl
 * @Author luojiarui
 * @Date 2022/7/13 7:57 下午
 * @Version 1.0
 **/
@Slf4j
@Service
public class FileOperationServiceImpl implements FileOperationService {

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


    /**
     * 这个方法将一个目录下所有的文件读入然后全部上传
     * @return
     */
    public List<FileObj> readFileByDir(String path) throws IOException {
        List<FileObj> fileObjs = new ArrayList<>();
        File file = new File(path);

        if (!file.isDirectory()) {
            return fileObjs;
        }

        File[] files = file.listFiles();
        for (File f: files) {
            FileObj fileObj = readFile(f.getAbsolutePath());
            fileObjs.add(fileObj);
        }

        return fileObjs;

    }

    private byte[] getContent(File file) throws IOException {

        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            log.error("file too big...");
            return new byte[0];
        }
        try (FileInputStream fi = new FileInputStream(file)) {
            byte[] buffer = new byte[(int) fileSize];
            int offset = 0;
            int numRead = 0;
            while (offset < buffer.length
                    && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {
                offset += numRead;
            }
            // 确保所有数据均被读取
            if (offset != buffer.length) {
                throw new IOException("Could not completely read file "
                        + file.getName());
            }
            return buffer;
        } catch (IOException e) {
            log.error("获取文件内容报错", e);
            return new byte[0];
        }

    }

}
