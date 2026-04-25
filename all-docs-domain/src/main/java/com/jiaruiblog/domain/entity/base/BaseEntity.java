package com.jiaruiblog.domain.entity.base;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName BaseEntity
 * @Description 基础实体类，所有实体应继承此类
 * @author luojiarui
 * @Date 2022/6/4 9:37 上午
 * @Version 1.0
 **/
@Data
@MappedSuperclass
public abstract class BaseEntity {

    /**
     * 主键
     */
    @Id
    protected String id;

    /**
     * 创建时间
     */
    @Column(name = "create_date", updatable = false)
    protected Date createDate;

    /**
     * 更新时间
     */
    @Column(name = "update_date")
    protected Date updateDate;
}