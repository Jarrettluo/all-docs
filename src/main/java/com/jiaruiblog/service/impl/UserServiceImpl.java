package com.jiaruiblog.service.impl;

import com.jiaruiblog.auth.PermissionEnum;
import com.jiaruiblog.config.SystemConfig;
import com.jiaruiblog.entity.User;
import com.jiaruiblog.entity.bo.UserBO;
import com.jiaruiblog.entity.dto.BasePageDTO;
import com.jiaruiblog.entity.dto.RegistryUserDTO;
import com.jiaruiblog.entity.dto.UserRoleDTO;
import com.jiaruiblog.entity.vo.UserVO;
import com.jiaruiblog.exception.BusinessException;
import com.jiaruiblog.exception.ErrorCode;
import com.jiaruiblog.repository.UserRepository;
import com.jiaruiblog.service.IFileService;
import com.jiaruiblog.service.IUserService;
import com.jiaruiblog.util.JwtUtil;
import com.mongodb.client.result.UpdateResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 * @author Jarrett Luo
 */
@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    private static final String COLLECTION_NAME = "user";
    private static final String OBJECT_ID = "_id";
    private static final String USER_BANNING = "banning";
    public static final String AVATAR = "avatar";
    public static final String USERNAME = "username";
    public static final String ROLE = "permissionEnum";
    public static final String UPDATE_TIME = "updateDate";

    @Resource
    MongoTemplate mongoTemplate;

    @Resource
    IFileService fileService;

    @Resource
    private SystemConfig systemConfig;

    @Autowired
    private UserRepository userRepository;  // 统一接口，根据配置自动注入对应实现

    /**
     * 初始化第一个用户，默认从配置中取到第一个管理员账号密码
     */
    @Override
    public void initFirstUser() {
        RegistryUserDTO userDTO = new RegistryUserDTO();
        userDTO.setUsername(systemConfig.getInitialUsername());
        userDTO.setPassword(systemConfig.getInitialPassword());
        Query query = new Query().addCriteria(Criteria.where(USERNAME).is(userDTO.getUsername()));
        List<User> users = mongoTemplate.find(query, User.class, COLLECTION_NAME);
        if (CollectionUtils.isEmpty(users)) {
            User user = new User();
            user.setPermissionEnum(PermissionEnum.ADMIN);
            user.setUsername(systemConfig.getInitialUsername());
            user.setPassword(userDTO.getEncodePassword());
            user.setCreateDate(new Date());
            user.setUpdateDate(new Date());
            mongoTemplate.save(user, COLLECTION_NAME);
            return;
        }
        if (Boolean.TRUE.equals(systemConfig.getCoverAdmin())) {
            Update update = new Update();
            update.set(ROLE, PermissionEnum.ADMIN);
            update.set("password", userDTO.getEncodePassword());
            update.set(UPDATE_TIME, new Date());
            mongoTemplate.updateFirst(query, update, User.class, COLLECTION_NAME);
        }
    }

    /**
     * 用户登录
     * @param userDTO 登录用户信息DTO
     * @return Map<String, String> 包含token、用户ID、头像、用户名、用户类型等信息的map
     */
    @Override
    public Map<String, String> login(RegistryUserDTO userDTO) {
        Query query = new Query(Criteria.where(USERNAME)
                .is(userDTO.getUsername()).and("password").is(userDTO.getEncodePassword()));
        User dbUser = mongoTemplate.findOne(query, User.class, COLLECTION_NAME);
        if (dbUser == null) {
            return new HashMap<>();
        }
        // 屏蔽用户禁止访问
        if (Boolean.TRUE.equals(dbUser.getBanning())) {
            return new HashMap<>();
        }

        String token = JwtUtil.createToken(dbUser);
        Map<String, String> result = new HashMap<>(8);
        result.put("token", token);
        result.put("userId", dbUser.getId());
        result.put(AVATAR, dbUser.getAvatar());
        result.put(USERNAME, dbUser.getUsername());
        result.put("type", dbUser.getPermissionEnum() != null ? dbUser.getPermissionEnum().toString() : null);

        // 登录以后记录登录时间
        Query query1 = new Query(Criteria.where("_id").is(dbUser.getId()));
        Update update = new Update();
        update.set("lastLogin", new Date());
        mongoTemplate.updateFirst(query1, update, User.class, COLLECTION_NAME);

        return result;
    }

    /**
     * 用户注册
     * @param userDTO 注册用户信息DTO
     */
    @Override
    public void registry(RegistryUserDTO userDTO) {
        User user = new User();
        user.setPermissionEnum(PermissionEnum.USER);
        Query query = new Query().addCriteria(Criteria.where(USERNAME).is(userDTO.getUsername()));
        List<User> users = mongoTemplate.find(query, User.class, COLLECTION_NAME);
        if (CollectionUtils.isEmpty(users)) {
            user.setUsername(userDTO.getUsername());
            user.setPassword(userDTO.getEncodePassword());
            user.setCreateDate(new Date());
            user.setUpdateDate(new Date());
            user.setLastLogin(new Date());
            mongoTemplate.save(user, COLLECTION_NAME);
        }
    }

    /**
     * 获取用户列表
     * @param page 分页参数DTO
     * @return Map<String, Object> 包含总数、当前页码、每页大小、用户列表的map
     */
    @Override
    public Map<String, Object> getUserList(BasePageDTO page) {
        long count = mongoTemplate.count(new Query(), User.class, COLLECTION_NAME);
        if (count < 1) {
            return new HashMap<>();
        }
        int pageNum = Optional.ofNullable(page.getPage()).orElse(1);
        int pageSize = Optional.ofNullable(page.getRows()).orElse(10);
        // 如果传入的参数超过了总数，返回第一页
        if ((long) (pageNum - 1) * pageSize > count) {
            pageNum = 1;
        }
        Query query = new Query();
        query.skip((long) (pageNum - 1) * pageSize);
        query.limit(pageSize);
        query.with(Sort.by(Sort.Direction.DESC, "createDate"));
        List<UserVO> users = mongoTemplate.find(query, UserVO.class, COLLECTION_NAME);
        Map<String, Object> result = new HashMap<>();
        result.put("total", count);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("result", users);
        return result;
    }

    /**
     * 修改用户角色
     * @param userRoleDTO 用户角色修改DTO
     */
    @Override
    public void changeUserRole(UserRoleDTO userRoleDTO) {
        User user = mongoTemplate.findById(userRoleDTO.getUserId(), User.class, COLLECTION_NAME);
        if (user == null || userRoleDTO.getRole().equals(user.getPermissionEnum())) {
            return ;
        }
        Query query = new Query().addCriteria(Criteria.where("_id").is(user.getId()));
        Update update = new Update();
        update.set(ROLE, userRoleDTO.getRole());
        update.set(UPDATE_TIME, new Date());
        mongoTemplate.updateFirst(query, update, User.class, COLLECTION_NAME);
    }

    /**
     * 封禁或解封用户
     * @param userId 用户ID
     */
    @Override
    public void blockUser(String userId) {
        User user = queryById(userId);
        if (user == null) {
            return ;
        }
        Query query = new Query();
        query.addCriteria(Criteria.where(OBJECT_ID).is(userId));
        Update update = new Update().set(USER_BANNING, !Optional.ofNullable(user.getBanning()).orElse(true));
        mongoTemplate.updateFirst(query, update, User.class, COLLECTION_NAME);
    }

    /**
     * 检查用户是否存在
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
     * @param user 用户业务对象
     * @return boolean 是否更新成功
     */
    @Override
    public boolean updateUserBySelf(UserBO user) {
        Query query = new Query(Criteria.where("_id").is(user.getId()));
        // 这里准备更新的user的密码已经经过了编码处理
        Update update = getUserUpdate(user);
        UpdateResult updateResult1 = mongoTemplate.updateFirst(query, update, User.class, COLLECTION_NAME);
        return updateResult1.getModifiedCount() > 0;
    }
    /**
     * 管理员更新用户信息
     * @param userBO 用户业务对象
     * @return boolean 是否更新成功
     */
    @Override
    public boolean updateUserByAdmin(UserBO userBO) {
        Query query = new Query().addCriteria(Criteria.where("_id").is(userBO.getId()));
        Update update = getUserUpdate(userBO);
        update.set(ROLE, Optional.ofNullable(userBO.getRole()).orElse(PermissionEnum.USER));
        UpdateResult updateResult1 = mongoTemplate.updateFirst(query, update, User.class, COLLECTION_NAME);
        return updateResult1.getModifiedCount() > 0;
    }

    /**
     * 根据用户ID查询用户信息
     * @param userId 用户ID
     * @return User 用户实体
     */
    @Override
    public User queryById(String userId) {
        return mongoTemplate.findById(userId, User.class, COLLECTION_NAME);
    }

    /**
     * 根据用户名查询用户信息
     * @param username 用户名
     * @return User 用户实体
     */
    @Override
    public User queryByUsername(String username) {
        Query query = new Query(Criteria.where("username").is(username));
        return mongoTemplate.findOne(query, User.class, COLLECTION_NAME);
    }

    /**
     * 检查用户是否具有指定权限
     * @param user 用户实体
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
     * @param userId 用户ID
     * @param file 头像文件
     */
    @Override
    public void uploadUserAvatar(String userId, MultipartFile file) {
        User user = mongoTemplate.findById(userId, User.class, COLLECTION_NAME);

        if (user == null) {
            return ;
        }
        String gridfsId;
        try {
            gridfsId = fileService.uploadFileToGridFs("userAvatar", file.getInputStream(), file.getContentType());
        } catch (IOException e) {
            log.error("上传dfs出错{}", e.getLocalizedMessage());
            return ;
        }
        List<String> avatar = user.getAvatarList();
        avatar.add(gridfsId);

        Query query = new Query().addCriteria(Criteria.where("_id").is(userId));
        Update update = new Update();
        update.set("avatarList", avatar);
        update.set(UPDATE_TIME, new Date());
        update.set(AVATAR, gridfsId);
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, COLLECTION_NAME);
        long matchedCount = updateResult.getMatchedCount();
        if (matchedCount < 1) {
            throw new BusinessException(ErrorCode.USER_DISABLED);
        }
    }

    /**
     * 删除某个用户的信息
     * @param userId 要删除的用户ID
     * @throws BusinessException 当删除操作失败时抛出异常
     */
    @Override
    public void removeUser(String userId) {
        User user = mongoTemplate.findById(userId, User.class, COLLECTION_NAME);
        if (user == null) {
            return ;
        }
        log.warn("[删除警告]正在删除用户：{}", user);
        fileService.deleteGridFs(user.getAvatarList().toArray(new String[0]));
        Query query = new Query().addCriteria(Criteria.where("_id").is(userId));
        mongoTemplate.findAllAndRemove(query, User.class, COLLECTION_NAME);
    }

    /**
     * 管理员根据用户的id批量删除用户
     * @param userIdList 要删除的用户ID列表
     * @param adminUserId 管理员用户ID
     */
    @Override
    public void deleteUserByIdBatch(List<String> userIdList, String adminUserId) {
        Query query = new Query().addCriteria(Criteria.where("_id").in(userIdList));
        List<User> userList = mongoTemplate.find(query, User.class, COLLECTION_NAME);
        if (CollectionUtils.isEmpty(userList) || userIdList.contains(adminUserId)) {
            return ;
        }
        List<String> allUserId = new ArrayList<>();
        for (User user : userList) {
            log.warn("[删除警告]正在删除用户：{}", user);
            allUserId.addAll(user.getAvatarList());
        }
        fileService.deleteGridFs(allUserId.toArray(new String[0]));
        mongoTemplate.findAllAndRemove(query, User.class, COLLECTION_NAME);
    }

    /**
     * 删除用户的头像
     * @param userId 用户ID
     */
    @Override
    public void removeUserAvatar(String userId) {
        User user = mongoTemplate.findById(userId, User.class, COLLECTION_NAME);
        if (user == null) {
            return;
        }
        fileService.deleteGridFs(user.getAvatar());
        Query query = new Query().addCriteria(Criteria.where("_id").is(userId));
        Update update = new Update();
        update.set(AVATAR, null);
        update.set(UPDATE_TIME, new Date());
        mongoTemplate.updateFirst(query, update, User.class, COLLECTION_NAME);
    }
    /**
     * 根据用户id批量查询用户的头像信息
     * @param userIdList 用户ID列表
     * @return Map<String, String> 用户ID和头像URL的映射
     */
    @Override
    public Map<String, String> queryUserAvatarBatch(List<String> userIdList) {
        if (CollectionUtils.isEmpty(userIdList) || userIdList.size() > 100) {
            return new HashMap<>();
        }
        Query query = new Query(Criteria.where("_id").in(userIdList));
        List<User> users = mongoTemplate.find(query, User.class, COLLECTION_NAME);
        return users.stream().filter(item -> item.getId() != null && item.getAvatar() != null)
                .collect(Collectors.toMap(User::getId, User::getAvatar, (v1, v2) -> v2));
    }

    /**
     * 管理员对用户进行密码重置，重置的密码是初始密码
     * @param userId 需要重置密码的用户ID
     * @param adminId 管理员用户ID
     */
    @Override
    public void resetUserPwd(String userId, String adminId) {
        User user = mongoTemplate.findById(adminId, User.class, COLLECTION_NAME);
        User resetUser = mongoTemplate.findById(userId, User.class, COLLECTION_NAME);
        // 如果管理者是空的，或者管理者权限不够，均不能对用户进行重置！
        if (user == null
                || user.getId() == null
                || !PermissionEnum.ADMIN.equals(user.getPermissionEnum())
                || resetUser == null
        ) {
            return;
        }

        RegistryUserDTO userDTO = new RegistryUserDTO();
        userDTO.setPassword(systemConfig.getInitialPassword());

        Query query = new Query().addCriteria(Criteria.where("_id").is(userId));
        Update update = new Update();
        update.set("password", userDTO.getEncodePassword());
        mongoTemplate.updateFirst(query, update, User.class, COLLECTION_NAME);
    }

    /**
     * 构建用户信息更新的Update对象
     * @param user 用户业务对象，包含需要更新的字段信息
     * @return Update 构建好的MongoDB更新对象
     */
    private Update getUserUpdate(UserBO user) {
        Update update = new Update();
        if (StringUtils.hasText(user.getPassword())) {
            update.set("password", user.getPassword());
        }
        update.set("phone", user.getPhone());
        update.set("mail", user.getMail());
        update.set("male", user.getMale());
        update.set("description", user.getDescription());
        update.set(UPDATE_TIME, new Date());
        update.set("birthtime", user.getBirthtime());
        return update;
    }
}
