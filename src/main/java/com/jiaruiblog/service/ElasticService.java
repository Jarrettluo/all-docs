package com.jiaruiblog.service;

import com.jiaruiblog.entity.FileDocument;

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
     */
    List<FileDocument> search(String keyword) throws IOException;

}
