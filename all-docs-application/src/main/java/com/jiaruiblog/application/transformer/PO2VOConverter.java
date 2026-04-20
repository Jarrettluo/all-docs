package com.jiaruiblog.application.transformer;

import cn.hutool.core.collection.CollectionUtil;
import com.jiaruiblog.domain.entity.DocLog;
import com.jiaruiblog.domain.entity.vo.DocLogVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName PO2VOConverter
 * @Description TODO
 * @author luojiarui
 * @Date 2024/8/17 10:41
 * @Version 1.0
 **/
public class PO2VOConverter {

    public static final String DELETE = "DELETE";
    public static final String DOWNLOAD = "DOWNLOAD";
    public static final String UPLOAD = "UPLOAD";
    public static final String PREVIEW = "VIEW";

    public static List<DocLogVO> docLogListConvert(List<DocLog> docLogList) {
        if (CollectionUtil.isEmpty(docLogList)) {
            return new ArrayList<>();
        }

        List<DocLogVO> docLogVOList = new ArrayList<>();
        for (DocLog docLog : docLogList) {
            docLogVOList.add(docLogConvert(docLog));
        }
        return docLogVOList;
    }

    public static DocLogVO docLogConvert(DocLog docLog) {
        if (Objects.isNull(docLog)) {
            return new DocLogVO();
        }
        DocLogVO docLogVO = new DocLogVO();

        docLogVO.setId(docLog.getId());
        docLogVO.setUserName(docLog.getUserName());
        docLogVO.setDocName(docLog.getDocName());
        docLogVO.setCreateDate(docLog.getCreateDate());

        String actionName = "未知动作";
        switch (docLog.getAction()) {
            case DELETE:
                actionName = "删除文档";
                break;
            case DOWNLOAD:
                actionName = "下载";
                break;
            case UPLOAD:
                actionName = "上传";
                break;
            case PREVIEW:
                actionName = "浏览";
                break;
            default:
                break;
        }
        docLogVO.setAction(actionName);
        return docLogVO;
    }
}