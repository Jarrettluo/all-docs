package com.jiaruiblog.domain.entity.dto;

import lombok.Data;

/**
 * @author Jarrett Luo
 */
@Data
public class RemoveObjectDTO {

    /**
     * request body 请求对象中只具有单一参数id
     */
    private String id;

}