package com.jiaruiblog.entity.dto;

import lombok.Data;

/**
 * @Author Jarrett Luo
 * @Date 2022/6/20 16:44
 * @Version 1.0
 */
@Data
public class RemoveObjectDTO {

    /**
     * request body 请求对象中只具有单一参数id
     */
    private String id;

}
