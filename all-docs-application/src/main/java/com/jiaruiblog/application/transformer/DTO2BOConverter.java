package com.jiaruiblog.application.transformer;

import com.jiaruiblog.common.enums.PermissionEnum;
import com.jiaruiblog.domain.entity.bo.UserBO;
import com.jiaruiblog.domain.entity.dto.RegistryUserDTO;
import com.jiaruiblog.domain.entity.dto.UserDTO;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * @ClassName DTO2BOConverter
 * @Description DTO（数据传输对象）到BO（业务对象）的转换器，用于将UserDTO转换为UserBO
 * @author luojiarui
 * @Date 2024/7/23 17:34
 * @Version 1.0
 **/
public class DTO2BOConverter {

    private DTO2BOConverter() {}

    public static UserBO userDTO2BO(UserDTO userDTO) {
        UserBO userBO = new UserBO();
        if (Objects.isNull(userDTO)) {
            return userBO;
        }
        userBO.setId(userDTO.getId());
        // 对传入的DTO密码进行加密处理
        if (StringUtils.isNoneBlank(userDTO.getPassword())) {
            RegistryUserDTO registryUserDTO = new RegistryUserDTO();
            registryUserDTO.setPassword(userDTO.getPassword());
            userBO.setPassword(registryUserDTO.getEncodePassword());
        }
        userBO.setPhone(userDTO.getPhone());
        userBO.setMail(userDTO.getMail());
        userBO.setNickname(userDTO.getNickname());
        userBO.setMale(userDTO.isMale());
        userBO.setBirthtime(userDTO.getBirthtime());
        userBO.setDescription(userDTO.getDescription());
        userBO.setRole(PermissionEnum.getRoleByName(userDTO.getRole()));
        return userBO;
    }

}