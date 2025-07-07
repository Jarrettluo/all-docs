package com.jiaruiblog.service.impl;

import com.jiaruiblog.auth.PermissionEnum;
import com.jiaruiblog.config.SystemConfig;
import com.jiaruiblog.convert.UserConvert;
import com.jiaruiblog.entity.User;
import com.jiaruiblog.entity.bo.UserBO;
import com.jiaruiblog.entity.dto.BasePageDTO;
import com.jiaruiblog.entity.dto.RegistryUserDTO;
import com.jiaruiblog.entity.dto.UserRoleDTO;
import com.jiaruiblog.entity.vo.PageVO;
import com.jiaruiblog.entity.vo.UserVO;
import com.jiaruiblog.exception.BusinessException;
import com.jiaruiblog.exception.ErrorCode;
import com.jiaruiblog.repository.UserRepository;
import com.jiaruiblog.service.IUserService;
import com.jiaruiblog.service.DocumentService;
import com.jiaruiblog.util.JwtUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 *
 * @author Jarrett Luo
 */
@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    public static final String AVATAR = "avatar";
    public static final String USERNAME = "username";
    public static final String UPDATE_TIME = "updateDate";

    @Resource
    private SystemConfig systemConfig;

    @Resource
    UserRepository userRepository;  // 统一接口，根据配置自动注入对应实现

    @Autowired
    UserConvert userConvert;

    @Resource
    private DocumentService documentService;

    /**
     * 初始化第一个用户，默认从配置中取到第一个管理员账号密码
     */
    @Override
    public void initFirstUser() {
        RegistryUserDTO userDTO = new RegistryUserDTO();
        userDTO.setUsername(systemConfig.getInitialUsername());
        userDTO.setPassword(systemConfig.getInitialPassword());
        List<User> userList = userRepository.findByUsername(systemConfig.getInitialUsername());
        if (CollectionUtils.isEmpty(userList)) {
            User user = new User();
            user.setPermissionEnum(PermissionEnum.ADMIN);
            user.setUsername(systemConfig.getInitialUsername());
            user.setPassword(userDTO.getEncodePassword());
            user.setCreateDate(new Date());
            user.setUpdateDate(new Date());
            userRepository.insert(user);
            return;
        }
        if (Boolean.TRUE.equals(systemConfig.getCoverAdmin())) {
            User user = userList.get(0);
            user.setPassword(userDTO.getEncodePassword());
            user.setPermissionEnum(PermissionEnum.ADMIN);
            userRepository.update(user);
        }
    }

    /**
     * 用户登录
     *
     * @param userDTO 登录用户信息DTO
     * @return Map<String, String> 包含token、用户ID、头像、用户名、用户类型等信息的map
     */
    @Override
    public Map<String, String> login(RegistryUserDTO userDTO) {
        User dbUser = queryByUsername(userDTO.getUsername());

        if (!dbUser.getPassword().equals(userDTO.getEncodePassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }
        // 屏蔽用户禁止访问
        if (Boolean.TRUE.equals(dbUser.getBanning())) {
            throw new BusinessException(ErrorCode.USER_DISABLED);
        }

        String token = JwtUtil.createToken(dbUser);
        Map<String, String> result = new HashMap<>(8);
        result.put("token", token);
        result.put("userId", dbUser.getId());
        result.put(AVATAR, dbUser.getAvatar());
        result.put(USERNAME, dbUser.getUsername());
        result.put("type", dbUser.getPermissionEnum() != null ? dbUser.getPermissionEnum().toString() : null);

        // 登录以后记录登录时间
        userRepository.updateLoginTime(dbUser);
        return result;
    }

    /**
     * 用户注册
     *
     * @param userDTO 注册用户信息DTO
     */
    @Override
    public void registry(RegistryUserDTO userDTO) {
        User user = new User();
        user.setPermissionEnum(PermissionEnum.USER);
        List<User> byUsername = userRepository.findByUsername(userDTO.getUsername());
        if (!CollectionUtils.isEmpty(byUsername)) {
            throw new BusinessException(ErrorCode.USERNAME_EXISTS);
        }
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getEncodePassword());
        user.setMail(userDTO.getMail());
        user.setPhone(userDTO.getPhone());
        user.setNickname(userDTO.getNickname());
        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        user.setLastLogin(new Date());
        userRepository.insert(user);
    }

    /**
     * 获取用户列表
     *
     * @param page 分页参数DTO
     * @return Map<String, Object> 包含总数、当前页码、每页大小、用户列表的map
     */
    @Override
    public PageVO<UserVO> getUserList(BasePageDTO page) {
        long count = userRepository.count();
        if (count < 1) {
            throw new BusinessException(ErrorCode.DATA_IS_EMPTY);
        }
        int pageNum = Optional.ofNullable(page.getPage()).orElse(1);
        int pageSize = Optional.ofNullable(page.getRows()).orElse(10);
        // 如果传入的参数超过了总数，返回第一页
        if ((long) (pageNum - 1) * pageSize > count) {
            pageNum = 1;
        }
        List<User> userList = userRepository.findByPage(pageNum, pageSize,
                Sort.by(Sort.Direction.DESC, "createDate"));

        List<UserVO> userVOList = userConvert.convertToVOList(userList);

        return PageVO.<UserVO>builder()
                .list(userVOList)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .total(count)
                .build();
    }

    /**
     * 修改用户角色
     *
     * @param userRoleDTO 用户角色修改DTO
     */
    @Override
    public void changeUserRole(UserRoleDTO userRoleDTO) {
        User user = queryById(userRoleDTO.getUserId());
        if (user == null || userRoleDTO.getRole().equals(user.getPermissionEnum())) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        user.setPermissionEnum(userRoleDTO.getRole());
        userRepository.update(user);
    }

    /**
     * 封禁或解封用户
     *
     * @param userId 用户ID
     */
    @Override
    public void blockUser(String userId) {
        User user = queryById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        userRepository.blockUser(user);
    }

    /**
     * 检查用户是否存在
     *
     * @param userId 用户ID
     * @return boolean 用户是否存在
     */
    @Override
    public boolean isExist(String userId) {
        if (userId == null || userId.isEmpty()) {
            return false;
        }
        User user = queryById(userId);
        return user != null;
    }

    /**
     * 用户自行更新信息
     *
     * @param userBO 用户业务对象
     * @return boolean 是否更新成功
     */
    @Override
    public boolean updateUserBySelf(UserBO userBO) {
        if (userBO == null || userBO.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User dbUser = userRepository.findById(userBO.getId());
        if (dbUser == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        // 只允许用户更新自己的基础信息，不能改权限
        if (userBO.getPassword() != null) dbUser.setPassword(userBO.getPassword());
        if (userBO.getPhone() != null) dbUser.setPhone(userBO.getPhone());
        if (userBO.getMail() != null) dbUser.setMail(userBO.getMail());
        if (userBO.getMale() != null) dbUser.setMale(userBO.getMale());
        if (userBO.getDescription() != null) dbUser.setDescription(userBO.getDescription());
        if (userBO.getBirthtime() != null) dbUser.setBirthtime(userBO.getBirthtime());
        dbUser.setUpdateDate(new Date());
        userRepository.update(dbUser);
        return true;
    }

    /**
     * 管理员更新用户信息
     *
     * @param userBO 用户业务对象
     * @return boolean 是否更新成功
     */
    @Override
    public boolean updateUserByAdmin(UserBO userBO) {
        if (userBO == null || userBO.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User dbUser = userRepository.findById(userBO.getId());
        if (dbUser == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        // 管理员可更新所有字段
        if (userBO.getPassword() != null) dbUser.setPassword(userBO.getPassword());
        if (userBO.getPhone() != null) dbUser.setPhone(userBO.getPhone());
        if (userBO.getMail() != null) dbUser.setMail(userBO.getMail());
        if (userBO.getMale() != null) dbUser.setMale(userBO.getMale());
        if (userBO.getDescription() != null) dbUser.setDescription(userBO.getDescription());
        if (userBO.getBirthtime() != null) dbUser.setBirthtime(userBO.getBirthtime());
        if (userBO.getRole() != null) dbUser.setPermissionEnum(userBO.getRole());
        dbUser.setUpdateDate(new Date());
        userRepository.update(dbUser);
        return true;
    }

    /**
     * 根据用户ID查询用户信息
     *
     * @param userId 用户ID
     * @return User 用户实体
     */
    @Override
    public User queryById(String userId) {
        User byId = userRepository.findById(userId);
        if (Objects.isNull(byId)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return byId;
    }

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return User 用户实体
     */
    @Override
    public User queryByUsername(String username) {
        List<User> userListInDB = userRepository.findByUsername(username);
        if (CollectionUtils.isEmpty(userListInDB)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        if (userListInDB.size() > 1) {
            log.error("出现重复的文件名：{}", username);
            throw new BusinessException(ErrorCode.USER_DISABLED);
        }
        // 从数据库中查询到的用户取到一个用户
        return userListInDB.get(0);
    }

    /**
     * 检查用户是否具有指定权限
     *
     * @param user            用户实体
     * @param permissionEnums 权限枚举数组
     * @return boolean 是否具有权限
     */
    @Override
    public boolean checkPermissionForUser(User user, PermissionEnum[] permissionEnums) {
        Set<PermissionEnum> collect = Arrays.stream(permissionEnums).collect(Collectors.toSet());
        return collect.contains(user.getPermissionEnum());
    }

    /**
     * 上传用户头像
     *
     * @param userId 用户ID
     * @param file   头像文件
     */
    @Override
    public void uploadUserAvatar(String userId, MultipartFile file) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        try {
            String gridfsId = documentService.uploadFileToGridFs("userAvatar", file.getInputStream(), file.getContentType());
            if (user.getAvatarList() == null) user.setAvatarList(new ArrayList<>());
            user.getAvatarList().add(gridfsId);
            user.setAvatar(gridfsId);
            user.setUpdateDate(new Date());
            userRepository.update(user);
        } catch (Exception e) {
            log.error("上传头像失败", e);
            throw new BusinessException(ErrorCode.OPERATE_FAILED);
        }
    }

    /**
     * 删除某个用户的信息
     *
     * @param userId 要删除的用户ID
     * @throws BusinessException 当删除操作失败时抛出异常
     */
    @Override
    public void removeUser(String userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        if (user.getAvatarList() != null && !user.getAvatarList().isEmpty()) {
            documentService.deleteGridFs(user.getAvatarList().toArray(new String[0]));
        }
        userRepository.deleteById(userId);
    }

    /**
     * 管理员根据用户的id批量删除用户
     *
     * @param userIdList  要删除的用户ID列表
     * @param adminUserId 管理员用户ID
     */
    @Override
    public void deleteUserByIdBatch(List<String> userIdList, String adminUserId) {
        if (userIdList == null || userIdList.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        for (String userId : userIdList) {
            if (userId.equals(adminUserId)) continue; // 不允许删除自己
            removeUser(userId);
        }
    }

    /**
     * 删除用户的头像
     *
     * @param userId 用户ID
     */
    @Override
    public void removeUserAvatar(String userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        if (user.getAvatar() != null) {
            documentService.deleteGridFs(user.getAvatar());
            user.setAvatar(null);
            user.setUpdateDate(new Date());
            userRepository.update(user);
        }
    }

    /**
     * 根据用户id批量查询用户的头像信息
     *
     * @param userIdList 用户ID列表
     * @return Map<String, String> 用户ID和头像URL的映射
     */
    @Override
    public Map<String, String> queryUserAvatarBatch(List<String> userIdList) {
        if (userIdList == null || userIdList.isEmpty() || userIdList.size() > 100) {
            return new HashMap<>();
        }
        Map<String, String> result = new HashMap<>();
        for (String userId : userIdList) {
            User user = userRepository.findById(userId);
            if (user != null && user.getAvatar() != null) {
                result.put(userId, user.getAvatar());
            }
        }
        return result;
    }

    /**
     * 管理员对用户进行密码重置，重置的密码是初始密码
     *
     * @param userId  需要重置密码的用户ID
     * @param adminId 管理员用户ID
     */
    @Override
    public void resetUserPwd(String userId, String adminId) {
        User admin = userRepository.findById(adminId);
        // 待重置的用户
        User resetUser;
        if (Objects.equals(userId, adminId)) {
            resetUser = admin;
        } else {
            resetUser = userRepository.findById(userId);
        }

        // 如果管理者是空的，或者管理者权限不够，均不能对用户进行重置！
        if (admin == null
                || admin.getId() == null
                || !PermissionEnum.ADMIN.equals(admin.getPermissionEnum())
                || resetUser == null
        ) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED);
        }
        // 对重置的用户密码进行重新调整
        RegistryUserDTO userDTO = new RegistryUserDTO();
        userDTO.setPassword(systemConfig.getInitialPassword());

        resetUser.setPassword(userDTO.getEncodePassword());
        userRepository.update(resetUser);
    }

}
