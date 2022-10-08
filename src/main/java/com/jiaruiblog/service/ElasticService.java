package com.jiaruiblog.service;

import com.jiaruiblog.entity.FileDocument;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author jiarui.luo
 */
public interface ElasticService {

    /**
     * search
     * @param keyword String
     * @return result
     * @throws IOException
     */
    List<FileDocument> search(String keyword) throws IOException;

    /**
     * uploadFileToEs
     * @param is InputStream
     * @param fileDocument FileDocument
     */
    void uploadFileToEs(InputStream is, FileDocument fileDocument);

    /**
     * uploadFileToEsDocx
     * @param is InputStream
     * @param fileDocument FileDocument
     */
    void uploadFileToEsDocx(InputStream is, FileDocument fileDocument);

}
