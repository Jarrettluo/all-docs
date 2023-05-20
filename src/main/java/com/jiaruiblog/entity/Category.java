package com.jiaruiblog.entity;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * @ClassName Classification
 * @Description Category
 * @Author luojiarui
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

    @NotBlank(message = "")
    protected String name;

    protected Date createDate;

    protected Date updateDate;

    @Override
    public String toString () {
        return JSON.toJSONString(this);
    }

}
