package com.jiaruiblog.service.impl;

import com.jiaruiblog.auth.PermissionEnum;
import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.config.SystemConfig;
import com.jiaruiblog.entity.User;
import com.jiaruiblog.entity.bo.UserBO;
import com.jiaruiblog.entity.dto.BasePageDTO;
import com.jiaruiblog.entity.dto.RegistryUserDTO;
import com.jiaruiblog.entity.dto.UserRoleDTO;
import com.jiaruiblog.entity.vo.UserVO;
import com.jiaruiblog.service.IFileService;
import com.jiaruiblog.service.IUserService;
import com.jiaruiblog.util.BaseApiResult;
import com.jiaruiblog.util.JwtUtil;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author Jarrett Luo
 * @Date 2022/6/24 13:48
 * @Version 1.0
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

    /*
     * @Author luojiarui
     * @Description 初始化第一个用户，默认从配置中取到第一个管理员账号密码
     * @Date 17:30 2024/7/23
     * @Param []
     * @return void
     **/
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

    @Override
    public BaseApiResult login(RegistryUserDTO userDTO) {
        Query query = new Query(Criteria.where(USERNAME)
                .is(userDTO.getUsername()).and("password").is(userDTO.getEncodePassword()));
        User dbUser = mongoTemplate.findOne(query, User.class, COLLECTION_NAME);
        if (dbUser == null) {
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
        // 屏蔽用户禁止访问
        if (Boolean.TRUE.equals(dbUser.getBanning())) {
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.USER_HAS_BANNED);
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

        return BaseApiResult.success(result);

    }

    @Override
    public BaseApiResult registry(RegistryUserDTO userDTO) {
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
            return BaseApiResult.success(MessageConstant.SUCCESS);
        }
        return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.DATA_HAS_EXIST);
    }

    @Override
    public BaseApiResult getUserList(BasePageDTO page) {
        long count = mongoTemplate.count(new Query(), User.class, COLLECTION_NAME);
        if (count < 1) {
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.DATA_IS_NULL);
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
        return BaseApiResult.success(result);
    }

    @Override
    public BaseApiResult changeUserRole(UserRoleDTO userRoleDTO) {
        User user = mongoTemplate.findById(userRoleDTO.getUserId(), User.class, COLLECTION_NAME);
        if (user == null || userRoleDTO.getRole().equals(user.getPermissionEnum())) {
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
        Query query = new Query().addCriteria(Criteria.where("_id").is(user.getId()));
        Update update = new Update();
        update.set(ROLE, userRoleDTO.getRole());
        update.set(UPDATE_TIME, new Date());
        mongoTemplate.updateFirst(query, update, User.class, COLLECTION_NAME);
        return BaseApiResult.success(MessageConstant.SUCCESS);
    }

    @Override
    public BaseApiResult blockUser(String userId) {
        User user = queryById(userId);
        if (user == null) {
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
        Query query = new Query();
        query.addCriteria(Criteria.where(OBJECT_ID).is(userId));
        Update update = new Update().set(USER_BANNING, !Optional.ofNullable(user.getBanning()).orElse(true));
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, User.class, COLLECTION_NAME);
        if (updateResult.getModifiedCount() > 0) {
            return BaseApiResult.success(MessageConstant.SUCCESS);
        }
        return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
    }

    /**
     * 根据用户的主键id查询用户信息
     *
     * @param userId 用户信息
     * @return 返回布尔
     */
    @Override
    public boolean isExist(String userId) {
        if (userId == null || "".equals(userId)) {
            return false;
        }
        User user = queryById(userId);
        return user != null;
    }

    @Override
    public boolean updateUserBySelf(UserBO user) {
        Query query = new Query(Criteria.where("_id").is(user.getId()));
        Update update = getUserUpdate(user);
        UpdateResult updateResult1 = mongoTemplate.updateFirst(query, update, User.class, COLLECTION_NAME);
        return updateResult1.getModifiedCount() > 0;
    }

    @Override
    public boolean updateUserByAdmin(UserBO userBO) {
        Query query = new Query().addCriteria(Criteria.where("_id").is(userBO.getId()));
        Update update = getUserUpdate(userBO);
        update.set(ROLE, Optional.ofNullable(userBO.getRole()).orElse(PermissionEnum.USER));
        UpdateResult updateResult1 = mongoTemplate.updateFirst(query, update, User.class, COLLECTION_NAME);
        return updateResult1.getModifiedCount() > 0;
    }

    /**
     * 检索已经存在的user
     *
     * @param userId String userId
     * @return User
     */
    @Override
    public User queryById(String userId) {
        return mongoTemplate.findById(userId, User.class, COLLECTION_NAME);
    }

    @Override
    public User queryByUsername(String username) {
        Query query = new Query(Criteria.where("username").is(username));
        User one = mongoTemplate.findOne(query, User.class, COLLECTION_NAME);
        return one;
    }

    /**
     * @return boolean
     * @Author luojiarui
     * @Description 检查某个用户是否具有某种权限
     * @Date 21:28 2022/12/7
     * @Param [user, permissionEnum]
     **/
    @Override
    public boolean checkPermissionForUser(User user, PermissionEnum[] permissionEnums) {
        Set<PermissionEnum> collect = Arrays.stream(permissionEnums).collect(Collectors.toSet());
        return collect.contains(user.getPermissionEnum());
    }

    /**
     * @return com.jiaruiblog.util.BaseApiResult
     * @Author luojiarui
     * @Description 上传头像到文件的avatar中，保存了多个用户的信息
     * @Date 22:40 2023/1/12
     * @Param [userId, file]
     **/
    @Override
    public BaseApiResult uploadUserAvatar(String userId, MultipartFile file) {
        User user = mongoTemplate.findById(userId, User.class, COLLECTION_NAME);

        if (user == null) {
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_FORMAT_ERROR);
        }
        String gridfsId;
        try {
            gridfsId = fileService.uploadFileToGridFs("userAvatar", file.getInputStream(), file.getContentType());
        } catch (IOException e) {
            log.error("上传dfs出错{}", e.getLocalizedMessage());
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
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
        if (matchedCount > 0) {
            return BaseApiResult.success(MessageConstant.SUCCESS);
        }
        return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
    }

    /**
     * @return com.jiaruiblog.util.BaseApiResult
     * @Author luojiarui
     * @Description 删除某个用户的信息
     * @Date 23:00 2023/1/12
     * @Param [userId]
     **/
    @Override
    public BaseApiResult removeUser(String userId) {
        User user = mongoTemplate.findById(userId, User.class, COLLECTION_NAME);
        if (user == null) {
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_FORMAT_ERROR);
        }
        log.warn("[删除警告]正在删除用户：{}", user);
        fileService.deleteGridFs(user.getAvatarList().toArray(new String[0]));
        Query query = new Query().addCriteria(Criteria.where("_id").is(userId));
        mongoTemplate.findAllAndRemove(query, User.class, COLLECTION_NAME);
        return BaseApiResult.success(MessageConstant.SUCCESS);
    }

    /**
     * @return com.jiaruiblog.util.BaseApiResult
     * @Author luojiarui
     * @Description 管理员根据用户的id批量删除用户
     * @Date 20:28 2023/2/12
     * @Param [userIdList, adminUserId]
     **/
    @Override
    public BaseApiResult deleteUserByIdBatch(List<String> userIdList, String adminUserId) {
        Query query = new Query().addCriteria(Criteria.where("_id").in(userIdList));
        List<User> userList = mongoTemplate.find(query, User.class, COLLECTION_NAME);
        if (CollectionUtils.isEmpty(userList) || userIdList.contains(adminUserId)) {
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_FORMAT_ERROR);
        }
        List<String> allUserId = new ArrayList<>();
        for (User user : userList) {
            log.warn("[删除警告]正在删除用户：{}", user);
            allUserId.addAll(user.getAvatarList());
        }
        fileService.deleteGridFs(allUserId.toArray(new String[0]));
        mongoTemplate.findAllAndRemove(query, User.class, COLLECTION_NAME);
        return BaseApiResult.success(MessageConstant.SUCCESS);
    }

    @Override
    public BaseApiResult removeUserAvatar(String userId) {
        User user = mongoTemplate.findById(userId, User.class, COLLECTION_NAME);
        if (user == null) {
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_FORMAT_ERROR);
        }
        fileService.deleteGridFs(user.getAvatar());
        Query query = new Query().addCriteria(Criteria.where("_id").is(userId));
        Update update = new Update();
        update.set(AVATAR, null);
        update.set(UPDATE_TIME, new Date());
        mongoTemplate.updateFirst(query, update, User.class, COLLECTION_NAME);
        return BaseApiResult.success(MessageConstant.SUCCESS);
    }

    /**
     * @return java.util.List<java.lang.String>
     * @Author luojiarui
     * @Description 根据用户id批量查询用户的头像信息
     * @Date 22:41 2023/3/30
     * @Param [userIdList]
     **/
    @Override
    public Map<String, String> queryUserAvatarBatch(List<String> userIdList) {
        if (CollectionUtils.isEmpty(userIdList) || userIdList.size() > 100) {
            return new HashMap();
        }
        Query query = new Query(Criteria.where("_id").in(userIdList));
        List<User> users = mongoTemplate.find(query, User.class, COLLECTION_NAME);
        return users.stream().filter(item -> item.getId() != null && item.getAvatar() != null)
                .collect(Collectors.toMap(User::getId, User::getAvatar, (v1, v2) -> v2));
    }


    @Override
    public BaseApiResult resetUserPwd(String userId, String adminId) {
        User user = mongoTemplate.findById(adminId, User.class, COLLECTION_NAME);
        User resetUser = mongoTemplate.findById(userId, User.class, COLLECTION_NAME);
        // 如果管理者是空的，或者管理者权限不够，均不能对用户进行重置！
        if (user == null || user.getId() == null || user.getId().equals(userId)
                || !PermissionEnum.ADMIN.equals(user.getPermissionEnum())
                || resetUser == null
        ) {
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_FORMAT_ERROR);
        }

        RegistryUserDTO userDTO = new RegistryUserDTO();
        userDTO.setPassword(systemConfig.getInitialPassword());

        Query query = new Query().addCriteria(Criteria.where("_id").is(userId));
        Update update = new Update();
        update.set("password", userDTO.getEncodePassword());
        mongoTemplate.updateFirst(query, update, User.class, COLLECTION_NAME);

        return BaseApiResult.success(MessageConstant.SUCCESS);
    }

    /**
     * @Author luojiarui
     * @Description 用户自行更新或者管理员更新用户信息的时候操作
     * @Date 23:47 2024/7/26
     * @Param [user]
     * @return org.springframework.data.mongodb.core.query.Update
     **/
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
