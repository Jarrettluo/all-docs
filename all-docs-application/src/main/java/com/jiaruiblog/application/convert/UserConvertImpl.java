package com.jiaruiblog.application.convert;

import com.jiaruiblog.domain.entity.User;
import com.jiaruiblog.domain.entity.vo.UserVO;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Jarrett Luo
 * @version 1.0
 */
public class UserConvertImpl implements UserConvert {

    @Override
    public UserVO convertToVO(User user) {
        if (user == null) {
            return new UserVO();
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> convertToVOList(List<User> users) {
        if (users == null || users.isEmpty()) {
            return List.of();
        }
        return users.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public User convertToEntity(UserVO userVO) {
        if (userVO == null) {
            return new User();
        }
        User user = new User();
        BeanUtils.copyProperties(userVO, user);
        return user;
    }
}