package com.jiaruiblog.convert;

import com.jiaruiblog.entity.User;
import com.jiaruiblog.entity.vo.UserVO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserConvertImpl implements UserConvert {

    @Override
    public UserVO convertToVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        // Manual property copying
        userVO.setId(user.getId());
        userVO.setUsername(user.getUsername());
        userVO.setPhone(user.getPhone());
        userVO.setMail(user.getMail());
        userVO.setMale(user.getMale());
        userVO.setDescription(user.getDescription());
        userVO.setAvatar(user.getAvatar());
        userVO.setBanning(user.getBanning());
        userVO.setPermissionEnum(user.getPermissionEnum());
        userVO.setBirthtime(user.getBirthtime());
        userVO.setLastLogin(user.getLastLogin());
        userVO.setCreateDate(user.getCreateDate());
        userVO.setUpdateDate(user.getUpdateDate());

        userVO.adjustTimeZone();
        return userVO;
    }

    @Override
    public List<UserVO> convertToVOList(List<User> users) {
        return users.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public User convertToEntity(UserVO userVO) {
        if (userVO == null) {
            return null;
        }
        User user = new User();
        // Manual property copying
        user.setId(userVO.getId());
        user.setUsername(userVO.getUsername());
        user.setPhone(userVO.getPhone());
        user.setMail(userVO.getMail());
        user.setMale(userVO.getMale());
        user.setDescription(userVO.getDescription());
        user.setAvatar(userVO.getAvatar());
        user.setBanning(userVO.getBanning());
        user.setPermissionEnum(userVO.getPermissionEnum());
        user.setBirthtime(userVO.getBirthtime());
        user.setLastLogin(userVO.getLastLogin());
        user.setCreateDate(userVO.getCreateDate());
        user.setUpdateDate(userVO.getUpdateDate());

        return user;
    }
}