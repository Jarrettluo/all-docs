package com.jiaruiblog.domain.entity.dto;

import lombok.Data;

/**
 * @ClassName RemoveObjectDTO
 * @Description 删除对象请求数据传输对象
 * @author Jarrett Luo
 */
@Data
public class RemoveObjectDTO {

    /**
     * request body 请求对象中只具有单一参数id
     */
    private String id;

}