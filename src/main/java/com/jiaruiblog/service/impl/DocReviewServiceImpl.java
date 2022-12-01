package com.jiaruiblog.service.impl;

import com.jiaruiblog.entity.BasePageDTO;
import com.jiaruiblog.entity.User;
import com.jiaruiblog.service.DocReviewService;
import com.jiaruiblog.util.BaseApiResult;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName DocReviewServiceImpl
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/11/30 21:02
 * @Version 1.0
 **/
@Service
public class DocReviewServiceImpl implements DocReviewService {

    @Override
    public BaseApiResult queryReviewsByPage(BasePageDTO page, User user) {
        return null;
    }

    @Override
    public BaseApiResult userRead(List<String> ids) {
        return null;
    }

    @Override
    public BaseApiResult refuse(String docId, String reason) {
        return null;
    }

    @Override
    public BaseApiResult refuseBatch(List<String> docIds) {
        return null;
    }

    @Override
    public BaseApiResult deleteReviewsBatch(List<String> docIds) {
        return null;
    }

    @Override
    public BaseApiResult queryReviewLog(BasePageDTO page, User user) {
        return null;
    }

    @Override
    public BaseApiResult queryDocLogs(BasePageDTO page, User user) {
        return null;
    }

    @Override
    public BaseApiResult deleteDocLogBatch(List<String> logIds) {
        return BaseApiResult.success();
    }
}
