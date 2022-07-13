package com.jiaruiblog.service;

import com.jiaruiblog.entity.FileDocument;

import java.io.IOException;
import java.io.InputStream;

public interface ElasticService {

    String search(String keyword) throws IOException;

    void uploadFileToEs(InputStream is, FileDocument fileDocument);
}
