package com.jiaruiblog.application.service.impl;

import com.jiaruiblog.application.service.IUserService;
import com.jiaruiblog.common.enums.PermissionEnum;
import com.jiaruiblog.domain.entity.User;
import com.jiaruiblog.domain.entity.bo.UserBO;
import com.jiaruiblog.domain.entity.dto.BasePageDTO;
import com.jiaruiblog.domain.entity.dto.RegistryUserDTO;
import com.jiaruiblog.domain.entity.dto.UserRoleDTO;
import com.jiaruiblog.domain.entity.vo.PageVO;
import com.jiaruiblog.domain.entity.vo.UserVO;
import com.jiaruiblog.infrastructure.repository.UserRepository;
import com.jiaruiblog.common.exception.BusinessException;
import com.jiaruiblog.common.exception.ErrorCode;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * 用户服务实现类
 *
 * @author Jarrett Luo
 */
@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    @Resource
    UserRepository userRepository;

    @Override
    public void initFirstUser() {
    }

    @Override
    public Map<String, String> login(RegistryUserDTO userDTO) {
        return Map.of();
    }

    @Override
    public void registry(RegistryUserDTO userDTO) {
    }

    @Override
    public PageVO<UserVO> getUserList(BasePageDTO page) {
        return PageVO.<UserVO>builder().build();
    }

    @Override
    public void changeUserRole(UserRoleDTO userRoleDTO) {
    }

    @Override
    public void blockUser(String userId) {
    }

    @Override
    public User queryById(String userId) {
        return null;
    }

    @Override
    public User queryByUsername(String username) {
        return null;
    }

    @Override
    public boolean checkPermissionForUser(User user, PermissionEnum[] permissionEnums) {
        return false;
    }

    @Override
    public void uploadUserAvatar(String userId, MultipartFile file) {
    }

    @Override
    public void removeUserAvatar(String userId) {
    }

    @Override
    public void removeUser(String userId) {
    }

    @Override
    public void deleteUserByIdBatch(List<String> userIdList, String adminUserId) {
    }

    @Override
    public Map<String, String> queryUserAvatarBatch(List<String> userIdList) {
        return Map.of();
    }

    @Override
    public void resetUserPwd(String userId, String adminId) {
    }

    @Override
    public boolean isExist(String userId) {
        return false;
    }

    @Override
    public boolean updateUserBySelf(UserBO userBO) {
        return false;
    }

    @Override
    public boolean updateUserByAdmin(UserBO userBO) {
        return false;
    }
}