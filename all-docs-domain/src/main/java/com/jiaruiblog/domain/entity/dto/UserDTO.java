package com.jiaruiblog.domain.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName UserDTO
 * @Description 用户DTO
 * @author luojiarui
 **/
@Data
public class UserDTO {

    private String id;

    @JsonIgnore
    private String password;

    private String phone;

    private String mail;

    private boolean male = false;

    private String description;

    private Date birthtime;

    private String role;

}