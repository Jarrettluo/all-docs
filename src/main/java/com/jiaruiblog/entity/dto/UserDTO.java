package com.jiaruiblog.entity.dto;

import lombok.Data;

/**
 * @ClassName UserDTO
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/12/18 12:56
 * @Version 1.0
 **/
@Data
public class UserDTO {


    private String password;

    private String phone;

    private String mail;

    private boolean male = false;

    private String description;

}
