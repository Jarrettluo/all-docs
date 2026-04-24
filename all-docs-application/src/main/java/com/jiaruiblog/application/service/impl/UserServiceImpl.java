package com.jiaruiblog.application.service.impl;

import com.jiaruiblog.application.service.IUserService;
import com.jiaruiblog.enums.PermissionEnum;
import com.jiaruiblog.common.exception.BusinessException;
import com.jiaruiblog.common.exception.ErrorCode;
import com.jiaruiblog.domain.entity.User;
import com.jiaruiblog.domain.entity.bo.UserBO;
import com.jiaruiblog.domain.entity.dto.BasePageDTO;
import com.jiaruiblog.domain.entity.dto.RegistryUserDTO;
import com.jiaruiblog.domain.entity.dto.UserRoleDTO;
import com.jiaruiblog.domain.entity.vo.PageVO;
import com.jiaruiblog.domain.entity.vo.UserVO;
import com.jiaruiblog.infrastructure.repository.UserRepository;
import com.jiaruiblog.infrastructure.storage.MinioStorageStrategy;
import com.jiaruiblog.util.HmacUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    private static final String DEFAULT_ADMIN_USERNAME = "admin";
    private static final String DEFAULT_ADMIN_PASSWORD = "admin123";

    @Resource
    UserRepository userRepository;

    @Resource
    MinioStorageStrategy minioStorageStrategy;

    @Resource
    HmacUtil hmacUtil;

    @Override
    public void initFirstUser() {
        log.info("初始化第一个管理员用户");
        List<User> existingUsers = userRepository.findByUsername(DEFAULT_ADMIN_USERNAME);
        if (existingUsers == null || existingUsers.isEmpty()) {
            User admin = new User();
            admin.setUsername(DEFAULT_ADMIN_USERNAME);
            admin.setPassword(encodePassword(DEFAULT_ADMIN_PASSWORD));
            admin.setPermissionEnum(PermissionEnum.ADMIN);
            admin.setCreateDate(new Date());
            admin.setUpdateDate(new Date());
            admin.setBanning(false);
            userRepository.save(admin);
            log.info("管理员用户创建成功：username={}", DEFAULT_ADMIN_USERNAME);
        } else {
            log.info("管理员用户已存在，无需创建");
        }
    }

    @Override
    public Map<String, String> login(RegistryUserDTO userDTO) {
        if (userDTO == null || !StringUtils.hasText(userDTO.getUsername()) || !StringUtils.hasText(userDTO.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "用户名或密码不能为空");
        }

        User user = queryByUsername(userDTO.getUsername());

        if (!verifyPassword(userDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        if (Boolean.TRUE.equals(user.getBanning())) {
            throw new BusinessException(ErrorCode.USER_DISABLED);
        }

        // 生成token
        String token;
        try {
            token = hmacUtil.generateHmac(user.getId());
        } catch (Exception e) {
            log.error("生成token失败", e);
            token = UUID.randomUUID().toString();
        }

        log.info("用户登录成功：username={}, userId={}", userDTO.getUsername(), user.getId());

        Map<String, String> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        if (user.getAvatar() != null) {
            result.put("avatar", user.getAvatar());
        }
        result.put("username", user.getUsername());
        if (user.getPermissionEnum() != null) {
            result.put("type", user.getPermissionEnum().toString());
        }

        // 更新登录时间
        user.setLastLogin(new Date());
        userRepository.update(user);

        return result;
    }

    @Override
    public void registry(RegistryUserDTO userDTO) {
        if (userDTO == null || !StringUtils.hasText(userDTO.getUsername()) || !StringUtils.hasText(userDTO.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "用户名或密码不能为空");
        }

        List<User> existingUsers = userRepository.findByUsername(userDTO.getUsername());
        if (existingUsers != null && !existingUsers.isEmpty()) {
            throw new BusinessException(ErrorCode.USERNAME_EXISTS);
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(encodePassword(userDTO.getPassword()));
        user.setPermissionEnum(PermissionEnum.USER);
        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        user.setBanning(false);

        userRepository.save(user);
        log.info("用户注册成功：username={}", userDTO.getUsername());
    }

    @Override
    public PageVO<UserVO> getUserList(BasePageDTO page) {
        int pageNum = 1;
        int pageSize = 10;
        if (page != null) {
            // BasePageDTO uses page/rows instead of pageNum/pageSize
            if (page.getPage() != null) {
                pageNum = page.getPage();
            }
            if (page.getRows() != null) {
                pageSize = page.getRows();
            }
        }

        long count = userRepository.count();
        if (count < 1) {
            return PageVO.<UserVO>builder()
                    .list(new ArrayList<>())
                    .pageNum(pageNum)
                    .pageSize(pageSize)
                    .total(0)
                    .build();
        }

        List<User> userList = userRepository.findByPage(pageNum, pageSize, Sort.by(Sort.Direction.DESC, "createDate"));

        List<UserVO> userVOList = userList.stream()
                .map(this::convertToUserVO)
                .collect(Collectors.toList());

        return PageVO.<UserVO>builder()
                .list(userVOList)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .total(count)
                .build();
    }

    @Override
    public void changeUserRole(UserRoleDTO userRoleDTO) {
        if (userRoleDTO == null || !StringUtils.hasText(userRoleDTO.getUserId()) || userRoleDTO.getRole() == null) {
            throw new BusinessException(ErrorCode.INVALID_PARAM);
        }

        User user = userRepository.findById(userRoleDTO.getUserId());
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        user.setPermissionEnum(userRoleDTO.getRole());
        user.setUpdateDate(new Date());
        userRepository.update(user);
        log.info("用户角色变更成功：userId={}, newRole={}", userRoleDTO.getUserId(), userRoleDTO.getRole());
    }

    @Override
    public void blockUser(String userId) {
        if (!StringUtils.hasText(userId)) {
            throw new BusinessException(ErrorCode.INVALID_PARAM);
        }

        User user = userRepository.findById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        userRepository.blockUser(user);
        log.info("用户已被封禁：userId={}", userId);
    }

    @Override
    public User queryById(String userId) {
        if (!StringUtils.hasText(userId)) {
            return null;
        }
        return userRepository.findById(userId);
    }

    @Override
    public User queryByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            return null;
        }
        List<User> users = userRepository.findByUsername(username);
        if (users == null || users.isEmpty()) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        if (users.size() > 1) {
            log.error("出现重复的用户名：{}", username);
            throw new BusinessException(ErrorCode.USER_DISABLED);
        }
        return users.get(0);
    }

    @Override
    public boolean checkPermissionForUser(User user, PermissionEnum[] permissionEnums) {
        if (user == null || permissionEnums == null || permissionEnums.length == 0) {
            return false;
        }
        return Arrays.stream(permissionEnums).collect(Collectors.toSet()).contains(user.getPermissionEnum());
    }

    @Override
    public void uploadUserAvatar(String userId, MultipartFile file) {
        if (!StringUtils.hasText(userId) || file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "用户ID或文件不能为空");
        }

        User user = userRepository.findById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        try {
            String avatarId = UUID.randomUUID().toString();
            String contentType = file.getContentType() != null ? file.getContentType() : "image/jpeg";

            String result = minioStorageStrategy.upload(file.getInputStream(), avatarId, contentType);
            if (result == null) {
                throw new BusinessException(ErrorCode.OPERATE_FAILED, "头像上传失败");
            }

            if (user.getAvatarList() == null) {
                user.setAvatarList(new ArrayList<>());
            }
            user.getAvatarList().add(avatarId);
            user.setAvatar(avatarId);
            user.setUpdateDate(new Date());
            userRepository.update(user);

            log.info("用户头像上传成功：userId={}, avatarId={}", userId, avatarId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("上传用户头像失败：userId={}", userId, e);
            throw new BusinessException(ErrorCode.OPERATE_FAILED, "头像上传失败");
        }
    }

    @Override
    public void removeUserAvatar(String userId) {
        if (!StringUtils.hasText(userId)) {
            return;
        }
        User user = userRepository.findById(userId);
        if (user == null) {
            return;
        }
        if (user.getAvatar() != null) {
            try {
                minioStorageStrategy.delete(user.getAvatar());
            } catch (Exception e) {
                log.error("删除用户头像文件失败：avatarId={}", user.getAvatar(), e);
            }
            user.setAvatar(null);
            user.setUpdateDate(new Date());
            userRepository.update(user);
        }
        log.info("用户头像删除成功：userId={}", userId);
    }

    @Override
    public void removeUser(String userId) {
        if (!StringUtils.hasText(userId)) {
            throw new BusinessException(ErrorCode.INVALID_PARAM);
        }
        userRepository.deleteById(userId);
        log.info("用户删除成功：userId={}", userId);
    }

    @Override
    public void deleteUserByIdBatch(List<String> userIdList, String adminUserId) {
        if (userIdList == null || userIdList.isEmpty()) {
            return;
        }
        for (String userId : userIdList) {
            if (!userId.equals(adminUserId)) {
                removeUser(userId);
            }
        }
        log.info("批量删除用户完成：count={}", userIdList.size());
    }

    @Override
    public Map<String, String> queryUserAvatarBatch(List<String> userIdList) {
        if (userIdList == null || userIdList.isEmpty()) {
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

    @Override
    public void resetUserPwd(String userId, String adminId) {
        if (!StringUtils.hasText(userId)) {
            throw new BusinessException(ErrorCode.INVALID_PARAM);
        }

        User resetUser = userRepository.findById(userId);
        if (resetUser == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 重置为默认密码
        resetUser.setPassword(encodePassword("123456"));
        resetUser.setUpdateDate(new Date());
        userRepository.update(resetUser);
        log.info("用户密码重置成功：userId={}, adminId={}", userId, adminId);
    }

    @Override
    public boolean isExist(String userId) {
        return queryById(userId) != null;
    }

    @Override
    public boolean updateUserBySelf(UserBO userBO) {
        if (userBO == null || !StringUtils.hasText(userBO.getId())) {
            return false;
        }

        User user = userRepository.findById(userBO.getId());
        if (user == null) {
            return false;
        }

        // 用户只能修改部分字段
        if (StringUtils.hasText(userBO.getUsername())) {
            user.setUsername(userBO.getUsername());
        }
        if (userBO.getPhone() != null) {
            user.setPhone(userBO.getPhone());
        }
        if (userBO.getMail() != null) {
            user.setMail(userBO.getMail());
        }
        if (userBO.getMale() != null) {
            user.setMale(userBO.getMale());
        }
        if (userBO.getDescription() != null) {
            user.setDescription(userBO.getDescription());
        }
        user.setUpdateDate(new Date());

        userRepository.update(user);
        log.info("用户自行更新信息成功：userId={}", userBO.getId());
        return true;
    }

    @Override
    public boolean updateUserByAdmin(UserBO userBO) {
        if (userBO == null || !StringUtils.hasText(userBO.getId())) {
            return false;
        }

        User user = userRepository.findById(userBO.getId());
        if (user == null) {
            return false;
        }

        // 管理员可以修改更多字段
        if (StringUtils.hasText(userBO.getUsername())) {
            user.setUsername(userBO.getUsername());
        }
        if (userBO.getPhone() != null) {
            user.setPhone(userBO.getPhone());
        }
        if (userBO.getMail() != null) {
            user.setMail(userBO.getMail());
        }
        if (userBO.getMale() != null) {
            user.setMale(userBO.getMale());
        }
        if (userBO.getDescription() != null) {
            user.setDescription(userBO.getDescription());
        }
        if (userBO.getRole() != null) {
            user.setPermissionEnum(userBO.getRole());
        }
        user.setUpdateDate(new Date());

        userRepository.update(user);
        log.info("管理员更新用户信息成功：userId={}", userBO.getId());
        return true;
    }

    /**
     * 将User转换为UserVO
     */
    private UserVO convertToUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setPermissionEnum(user.getPermissionEnum());
        vo.setCreateDate(user.getCreateDate());
        vo.setUpdateDate(user.getUpdateDate());
        vo.setBanning(user.getBanning());
        vo.setMail(user.getMail());
        vo.setDescription(user.getDescription());
        vo.setAvatar(user.getAvatar());
        return vo;
    }

    /**
     * 简单密码加密（实际应用中应使用BCrypt等更安全的方案）
     */
    private String encodePassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("密码加密失败", e);
        }
    }

    /**
     * 验证密码
     */
    private boolean verifyPassword(String rawPassword, String encodedPassword) {
        return encodePassword(rawPassword).equals(encodedPassword);
    }
}