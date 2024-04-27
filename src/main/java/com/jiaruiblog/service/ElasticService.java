package com.jiaruiblog.service;

import com.jiaruiblog.entity.FileDocument;
import com.jiaruiblog.entity.vo.PageVO;
import com.jiaruiblog.util.BaseApiResult;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    /**
     * search
     * @param keyword String
     * @param docIdSet 限制其
     * @return result
     * @throws IOException exception
     */
    Map<String, List<PageVO>> search(String keyword, Set<String> docIdSet) throws IOException;

    BaseApiResult getWordStat() throws IOException;

}
