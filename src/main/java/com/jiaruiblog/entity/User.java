package com.jiaruiblog.entity;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.util.Date;


/**
 * @ClassName User
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/6/4 9:37 上午
 * @Version 1.0
 **/
@Document
@Data
public class User {

    @Id
    private Long id;

    @NotBlank(message = "非空")
    private String username;

    private String message;

    private String company;

    private String hobby;

    private String avatar;

    private Date createDate;

    private Date updateDate;

    @Override
    public String toString () {
        return JSONObject.toJSONString(this);
    }
}
