package com.jiaruiblog.domain.entity;

import com.alibaba.fastjson.JSON;
import com.jiaruiblog.common.MessageConstant;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @ClassName Classification
 * @Description Category
 * @author luojiarui
 * @Date 2022/6/4 10:28 上午
 * @Version 1.0
 **/
@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category {

    @Id
    protected String id;

    @NotBlank(message = MessageConstant.PARAMS_IS_NOT_NULL)
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