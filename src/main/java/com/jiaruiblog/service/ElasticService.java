package com.jiaruiblog.service;

import com.jiaruiblog.entity.FileDocument;
import com.jiaruiblog.util.BaseApiResult;

import java.io.IOException;
import java.util.List;

/**
 * @author jiarui.luo
 */
public interface ElasticService {

    /**
     * search
     * @param keyword String
     * @return result
     * @throws IOException exception
     */
    List<FileDocument> search(String keyword) throws IOException;

    BaseApiResult getWordStat() throws IOException;

}
