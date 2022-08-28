package com.jiaruiblog.service;

import com.jiaruiblog.entity.FileDocument;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface ElasticService {

    List<FileDocument> search(String keyword) throws IOException;

    void uploadFileToEs(InputStream is, FileDocument fileDocument);

    void uploadFileToEsDocx(InputStream is, FileDocument fileDocument);

}
