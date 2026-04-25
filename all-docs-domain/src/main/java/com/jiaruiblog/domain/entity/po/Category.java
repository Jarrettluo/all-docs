package com.jiaruiblog.domain.entity.po;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName Category
 * @Description Category
 * @author luojiarui
 * @Date 2022/6/4 10:28 上午
 * @Version 1.0
 **/
@Data
public class Category {

    protected String id;

    protected String name;

    protected Date createDate;

    protected Date updateDate;

    @Override
    public String toString () {
        return JSON.toJSONString(this);
    }

    public void update(Category category) {
        if (category == null) {
            return;
        }
        if (category.getName() != null) {
            this.name = category.getName();
        }
        if (category.getUpdateDate() != null) {
            this.updateDate = category.getUpdateDate();
        }
    }

}