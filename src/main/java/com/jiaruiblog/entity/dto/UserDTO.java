package com.jiaruiblog.entity.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author luojiarui
 **/
@Data
public class UserDTO {

    private String id;

    private String password;

    private String phone;

    private String mail;

    private boolean male = false;

    private String description;

    private Date birthtime;

    private String role;

}
