package com.jiaruiblog.service;

import com.jiaruiblog.utils.ApiResult;

import java.util.List;

/**
 * @Author Jarrett Luo
 * @Date 2022/6/9 16:06
 * @Version 1.0
 */
public interface FileDocumentService {

    /**
     * 根据文件信息的id查询文件信息
     * @param ids
     * @return
     */
    ApiResult queryByIds(List<Integer> ids);

}
