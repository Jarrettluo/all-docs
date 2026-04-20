package com.jiaruiblog.application.service.impl;

import com.jiaruiblog.application.service.FileOperationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * @author luojiarui
 **/
@Slf4j
@Service
public class FileOperationServiceImpl implements FileOperationService {

    @Override
    public String parseToStr(MultipartFile file) {
        return "";
    }

    @Override
    public String parseToStr(String path) {
        return "";
    }

    @Override
    public String uploadFile(String name, InputStream inputStream) {
        return "";
    }

    @Override
    public String uploadFile(String name, File file) {
        return "";
    }

    @Override
    public String uploadFile(String name, InputStream inputStream, long size) {
        return "";
    }

    @Override
    public String uploadFile(String name, File file, long size) {
        return "";
    }

    @Override
    public InputStream getFile(String name) {
        return null;
    }

    @Override
    public InputStream getFile(String name, Integer expires) {
        return null;
    }

    @Override
    public void deleteFile(String name) {
    }

    @Override
    public String getFileUrl(String name) {
        return "";
    }

    @Override
    public String getFileUrl(String name, Integer expires) {
        return "";
    }

    @Override
    public List<String> listNames() {
        return List.of();
    }

    @Override
    public boolean isExist(String name) {
        return false;
    }
}