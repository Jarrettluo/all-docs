package com.jiaruiblog.service;

import com.jiaruiblog.entity.Tag;
import com.jiaruiblog.utils.ApiResult;

/**
 * @Author Jarrett Luo
 * @Date 2022/6/7 11:39
 * @Version 1.0
 */
public interface TagService {

    ApiResult insert(Tag tag);

    ApiResult update(Tag tag);

    ApiResult remove(Tag tag);

    ApiResult queryById(Tag tag);

    ApiResult search(Tag tag);

    
}
