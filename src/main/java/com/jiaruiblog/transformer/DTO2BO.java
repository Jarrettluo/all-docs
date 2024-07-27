package com.jiaruiblog.transformer;

import com.jiaruiblog.auth.PermissionEnum;
import com.jiaruiblog.entity.bo.UserBO;
import com.jiaruiblog.entity.dto.UserDTO;

import java.util.Objects;

/**
 * @ClassName UserDTO2BO
 * @Description TODO
 * @Author luojiarui
 * @Date 2024/7/23 17:34
 * @Version 1.0
 **/
public class DTO2BO {

    private DTO2BO() {}

    public static UserBO userDTO2BO(UserDTO userDTO) {
        UserBO userBO = new UserBO();
        if (Objects.isNull(userDTO)) {
            return userBO;
        }
        userBO.setId(userDTO.getId());
        userBO.setPassword(userDTO.getPassword());
        userBO.setPhone(userDTO.getPhone());
        userBO.setMail(userDTO.getMail());
        userBO.setMale(userDTO.isMale());
        userBO.setBirthtime(userDTO.getBirthtime());
        userBO.setDescription(userDTO.getDescription());
        userBO.setRole(PermissionEnum.getRoleByName(userDTO.getRole()));
        return userBO;
    }

}
