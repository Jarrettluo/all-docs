package com.jiaruiblog.infrastructure.config.mybatis;

import com.jiaruiblog.common.enums.DocStateEnum;

/**
 * DocStateEnum 的 MyBatis TypeHandler
 * 
 * @author Jarrett Luo
 * @version 1.0
 */
public class DocStateEnumTypeHandler extends EnumTypeHandler<DocStateEnum> {
    
    public DocStateEnumTypeHandler() {
        super(DocStateEnum.class);
    }
}