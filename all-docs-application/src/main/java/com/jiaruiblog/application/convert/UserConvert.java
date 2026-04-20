package com.jiaruiblog.application.convert;

import com.jiaruiblog.domain.entity.User;
import com.jiaruiblog.domain.entity.vo.UserVO;

import java.util.List;

public interface UserConvert {

    UserVO convertToVO(User user);

    List<UserVO> convertToVOList(List<User> users);

    User convertToEntity(UserVO userVO);
}