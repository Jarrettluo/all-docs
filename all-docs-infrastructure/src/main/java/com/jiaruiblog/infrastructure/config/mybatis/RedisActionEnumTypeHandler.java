package com.jiaruiblog.infrastructure.config.mybatis;

import com.jiaruiblog.common.enums.RedisActionEnum;

/**
 * RedisActionEnum 的 MyBatis TypeHandler
 *
 * @author Jarrett Luo
 * @version 1.0
 */
public class RedisActionEnumTypeHandler extends EnumTypeHandler<RedisActionEnum> {

    public RedisActionEnumTypeHandler() {
        super(RedisActionEnum.class);
    }
}