package com.jiaruiblog.convert;

import com.jiaruiblog.entity.User;
import com.jiaruiblog.entity.vo.UserVO;

import java.util.List;

public interface UserConvert {

    UserVO convertToVO(User user);

    List<UserVO> convertToVOList(List<User> users);

    User convertToEntity(UserVO userVO);
}