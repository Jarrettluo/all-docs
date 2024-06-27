package com.jiaruiblog.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.auth0.jwt.interfaces.Claim;
import com.jiaruiblog.common.ConfigConstant;
import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.config.SystemConfig;
import com.jiaruiblog.entity.User;
import com.jiaruiblog.entity.dto.*;
import com.jiaruiblog.service.IUserService;
import com.jiaruiblog.util.BaseApiResult;
import com.jiaruiblog.util.JwtUtil;
import com.mongodb.client.result.UpdateResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @ClassName UserController
 * @Description 关于用户的所有请求关系
 * @Author luojiarui
 * @Date 2022/6/4 9:38 上午
 * @Version 1.0
 **/
@Api(tags = "用户模块")
@RestController
@Slf4j
@CrossOrigin
@RequestMapping("/user")
public class UserController {

    private static final String COLLECTION_NAME = "user";

    private static final String REQUEST_USER_ID = "id";


    @Resource
    IUserService userService;

    @Resource
    private MongoTemplate template;

    @Resource
    SystemConfig systemConfig;


    @ApiOperation(value = "新增单个用户", notes = "新增单个用户")
    @PostMapping(value = "/insert")
    public BaseApiResult insertObj(@RequestBody @Valid RegistryUserDTO userDTO) {
        if (!StpUtil.hasPermission("user.insert")) {
            return BaseApiResult.error(MessageConstant.AUTH_ERROR_CODE, MessageConstant.NOT_PERMISSION);
        }
        if (Boolean.FALSE.equals(systemConfig.getUserRegistry())) {
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
        return userService.registry(userDTO);
    }

    @ApiOperation(value = "批量新增用户", notes = "批量新增用户")
    @PostMapping(value = "/batchInsert")
    public BaseApiResult batchInsert(@RequestBody List<User> users) {
        if (!StpUtil.hasPermission("user.insert")) {
            return BaseApiResult.error(MessageConstant.AUTH_ERROR_CODE, MessageConstant.NOT_PERMISSION);
        }
        log.info("批量新增用户入参=={}", users.toString());
        for (User item : users) {
            template.save(item, COLLECTION_NAME);
        }
        return BaseApiResult.success("批量新增成功");
    }

    @ApiOperation(value = "根据id查询", notes = "批量新增用户")
    @PostMapping(value = "/getById")
    public BaseApiResult getById(@RequestBody User user) {
        if (!StpUtil.hasPermission("user.query")) {
            return BaseApiResult.error(MessageConstant.AUTH_ERROR_CODE, MessageConstant.NOT_PERMISSION);
        }
        Query query = new Query(Criteria.where("_id").is(user.getId()));
        User one = template.findOne(query, User.class, COLLECTION_NAME);
        return BaseApiResult.success(one);
    }

    @ApiOperation(value = "根据用户名称查询", notes = "根据用户名称查询")
    @PostMapping(value = "/getByUsername")
    public BaseApiResult getByUsername(@RequestBody User user) {
        if (!StpUtil.hasPermission("user.query")) {
            return BaseApiResult.error(MessageConstant.AUTH_ERROR_CODE, MessageConstant.NOT_PERMISSION);
        }
        Query query = new Query(Criteria.where("username").is(user.getUsername()));
        User one = template.findOne(query, User.class, COLLECTION_NAME);
        return BaseApiResult.success(one);
    }

    @ApiOperation(value = "更新用户hobby和company", notes = "更新用户hobby和company")
    @PutMapping(value = "/updateUser")
    public BaseApiResult updateUser(@RequestBody User user) {
        if (!StpUtil.hasPermission("user.update")) {
            return BaseApiResult.error(MessageConstant.AUTH_ERROR_CODE, MessageConstant.NOT_PERMISSION);
        }
        Query query = new Query(Criteria.where("_id").is(user.getId()));
        Update update = new Update();
        if (StringUtils.hasText(user.getPassword())) {
            update.set("password", user.getPassword());
        }
        update.set("phone", user.getPhone());
        update.set("mail", user.getMail());
        update.set("male", user.getMale());
        update.set("description", user.getDescription());
        update.set("updateDate", new Date());
        update.set("birthtime", user.getBirthtime());
        UpdateResult updateResult1 = template.updateFirst(query, update, User.class, COLLECTION_NAME);
        if (updateResult1.getMatchedCount() > 0) {
            return BaseApiResult.success("更新成功！");
        }
        return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
    }

    /**
     * @return com.jiaruiblog.util.BaseApiResult
     * @Author luojiarui
     * @Description 删除用户的时候必须要删除其头像信息
     * @Date 22:40 2023/1/12
     * @Param [user, request]
     **/
    // @Permission(PermissionEnum.ADMIN)
    @ApiOperation(value = "根据id删除用户", notes = "根据id删除用户")
    @DeleteMapping(value = "/auth/deleteByID")
    public BaseApiResult deleteById(@RequestBody User removeUser, HttpServletRequest request) {
        if (!StpUtil.hasPermission("user.remove")) {
            return BaseApiResult.error(MessageConstant.AUTH_ERROR_CODE, MessageConstant.NOT_PERMISSION);
        }
        String userId = (String) request.getAttribute(REQUEST_USER_ID);
        // 不能删除自己的账号
        String removeUserId = removeUser.getId();
        if (userId == null || userId.equals(removeUserId)) {
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
        return userService.removeUser(removeUserId);
    }

    /**
     * @return com.jiaruiblog.util.BaseApiResult
     * @Author luojiarui
     * @Description 管理员批量删除， 注意删除用户的时候必须要删除其头像信息
     * @Date 22:40 2023/1/12
     * @Param [user, request]
     **/
    @ApiOperation(value = "根据id删除用户", notes = "根据id删除用户")
    // @Permission(value = PermissionEnum.ADMIN)
    @DeleteMapping(value = "/auth/deleteByIDBatch")
    public BaseApiResult deleteByIdBatch(@RequestBody BatchIdDTO batchIdDTO, HttpServletRequest request) {
        if (!StpUtil.hasPermission("user.remove")) {
            return BaseApiResult.error(MessageConstant.AUTH_ERROR_CODE, MessageConstant.NOT_PERMISSION);
        }
        // 用户只能删除自己，不能删除其他人的信息
        String adminUserId = (String) request.getAttribute(REQUEST_USER_ID);
        List<String> userIdList = Optional.ofNullable(batchIdDTO.getIds()).orElse(new ArrayList<>());
        if (userIdList.size() > ConfigConstant.MAX_DELETE_NUM) {
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
        return userService.deleteUserByIdBatch(userIdList, adminUserId);
    }


    /**
     * 模拟用户 登录
     */
    @ApiOperation(value = "用户登录")
    @PostMapping("/login")
    public BaseApiResult login(@RequestBody RegistryUserDTO user) {
        return userService.login(user);
    }

    /**
     * 模拟用户 登录
     */
    @ApiOperation(value = "用户登录")
    @GetMapping("/checkLoginState")
    public BaseApiResult checkLoginState(HttpServletRequest request, HttpServletResponse response) {
        // 缓存 2s; 避免前端频繁刷新
        response.setHeader("Cache-Control", "max-age=2, public");
        //获取 header里的token
        final String token = request.getHeader("authorization");
        if (!StringUtils.hasText(token)) {
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
        Map<String, Claim> userData = JwtUtil.verifyToken(token);
        if (CollectionUtils.isEmpty(userData)) {
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
        return BaseApiResult.success(MessageConstant.SUCCESS);
    }

    /**
     * @return com.jiaruiblog.util.BaseApiResult
     * @Author luojiarui
     * @Description 根据分页参数查询用户列表
     * @Date 21:21 2023/1/10
     * @Param []
     **/
    @ApiOperation(value = "管理员查询全部用户信息", notes = "只有管理员有权限进行用户列表查询")
    // @Permission(PermissionEnum.ADMIN)
    @GetMapping("/allUsers")
    public BaseApiResult allUsers(@ModelAttribute("pageDTO") BasePageDTO pageDTO) {
        if (!StpUtil.hasPermission("user.query")) {
            return BaseApiResult.error(MessageConstant.AUTH_ERROR_CODE, MessageConstant.NOT_PERMISSION);
        }
        return userService.getUserList(pageDTO);
    }

    @ApiOperation(value = "改变用户权限", notes = "管理员能够调整其他人的角色，不能调整自己的角色")
    // @Permission(PermissionEnum.ADMIN)
    @PutMapping("changeUserRole")
    public BaseApiResult changeUserRole(@RequestBody UserRoleDTO userRoleDTO, HttpServletRequest request) {
        if (!StpUtil.hasPermission("user.roles")) {
            return BaseApiResult.error(MessageConstant.AUTH_ERROR_CODE, MessageConstant.NOT_PERMISSION);
        }
        String adminUserId = (String) request.getAttribute(REQUEST_USER_ID);
        // 不能屏蔽自己的账号
        if (userRoleDTO.getUserId().equals(adminUserId)) {
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
        return userService.updateUserRoles(userRoleDTO.getUserId(), userRoleDTO.getRoleIds());
    }

    /**
     * @return com.jiaruiblog.util.BaseApiResult
     * @Author luojiarui
     * @Description 屏蔽用户，使用户不可登录；再次调用此接口则取消屏蔽
     * @Date 20:30 2023/2/12
     * @Param [userId]
     **/
    @ApiOperation(value = "管理员屏蔽用户", notes = "管理员不能屏蔽自己的账号")
    // @Permission(PermissionEnum.ADMIN)
    @GetMapping("blockUser")
    public BaseApiResult blockUser(@RequestParam("userId") String userId, HttpServletRequest request) {
        if (!StpUtil.hasPermission("user.block")) {
            return BaseApiResult.error(MessageConstant.AUTH_ERROR_CODE, MessageConstant.NOT_PERMISSION);
        }
        if (!StringUtils.hasText(userId)) {
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_IS_NOT_NULL);
        }
        String adminUserId = (String) request.getAttribute(REQUEST_USER_ID);
        // 不能屏蔽自己的账号
        if (userId.equals(adminUserId)) {
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
        return userService.blockUser(userId);
    }

    /**
     * 查询 用户信息，登录后携带JWT才能访问
     */
    @RequestMapping("/secure/getUserInfo")
    public String login(HttpServletRequest request) {
        Integer id = (Integer) request.getAttribute("id");
        String userName = request.getAttribute("username").toString();
        String password = request.getAttribute("password").toString();
        return "当前用户信息id=" + id + ",userName=" + userName + ",password=" + password;
    }

    static final Map<String, String> fieldRegx = new HashMap<>(8);

    static {
        // 1-64个数字字母下划线
        fieldRegx.put("password", "^[0-9a-z_]{1,64}$");
        fieldRegx.put("phone", "/^1(3\\d|4[5-9]|5[0-35-9]|6[567]|7[0-8]|8\\d|9[0-35-9])\\d{8}$/");
        fieldRegx.put("mail", "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");
        // 1-140个任意字符
        fieldRegx.put("description", "(.*){1,140}");
    }


    /**
     * @return com.jiaruiblog.util.BaseApiResult
     * @Author luojiarui
     * @Description 更新用户的基本信息
     * @Date 13:07 2022/12/18
     * @Param [userDTO]
     **/
    @PutMapping("updateUserInfo")
    public BaseApiResult updateUserInfo(@RequestBody UserDTO userDTO) {

        if (StringUtils.hasText(userDTO.getPassword())) {
            if (!patternMatch(userDTO.getPassword(), fieldRegx.get("password"))) {
                return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_FORMAT_ERROR);
            }
        }
        if (StringUtils.hasText(userDTO.getMail())) {
            if (!patternMatch(userDTO.getPassword(), fieldRegx.get("mail"))) {
                return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_FORMAT_ERROR);
            }
        }

        if (StringUtils.hasText(userDTO.getPhone())) {
            if (!patternMatch(userDTO.getPassword(), fieldRegx.get("phone"))) {
                return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_FORMAT_ERROR);
            }
        }
        if (StringUtils.hasText(userDTO.getDescription())) {
            if (!patternMatch(userDTO.getPassword(), fieldRegx.get("description"))) {
                return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_FORMAT_ERROR);
            }
        }

        return BaseApiResult.success();
    }


    @PostMapping("/auth/uploadUserAvatar")
    public BaseApiResult uploadUserAvatar(@RequestParam(value = "img") MultipartFile file, HttpServletRequest request) {
        String userId = (String) request.getAttribute("id");
        String type = file.getContentType();
        String[] availableTypes = new String[]{"image/png", "image/jpeg", "image/gif"};
        if (!Arrays.asList(availableTypes).contains(type)) {
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_FORMAT_ERROR);
        }
        return userService.uploadUserAvatar(userId, file);
    }

    @DeleteMapping("/auth/removeUserAvatar")
    public BaseApiResult removeUserAvatar(HttpServletRequest request) {
        return userService.removeUserAvatar((String) request.getAttribute("id"));
    }

    private boolean patternMatch(String s, String regex) {
        return Pattern.compile(regex).matcher(s).matches();
    }

    @ApiOperation(value = "重置用户密码", notes = "管理员对用户进行密码重置")
    @PostMapping("auth/resetUserPwd")
    public BaseApiResult resetUserPwd(@RequestBody String userId, HttpServletRequest request) {
        if (!StpUtil.hasPermission("user.password.reset")) {
            return BaseApiResult.error(MessageConstant.AUTH_ERROR_CODE, MessageConstant.NOT_PERMISSION);
        }
        String adminId = (String) request.getAttribute("id");
        return userService.resetUserPwd(userId, adminId);
    }

    @ApiOperation(value = "用户发起找回密码的请求，发送token给邮箱")
    @PostMapping("/generateResetToken")
    public BaseApiResult generateResetToken() {
        // 用户发送邮件信息
        // 查找相应的用户名；如果用户名不存在则报错
        // 用户名找到以后发送对应的重置邮箱给用户
        // 使用88 邮箱给邮件发消息
        return BaseApiResult.success();
    }

    @ApiOperation(value = "用户重置密码")
    @PostMapping("/resetPassword")
    public BaseApiResult resetPassword() {
        // 用户发送邮件/token/新密码
        // 首先验证token是否存在，不存在则禁止
        // 如果令牌有效，允许用户重置密码，并在成功后从 Redis 中删除令牌。
        // 其次根据邮箱找到对应的用户信息，修改用户密码
        // 用户自动登录
        return BaseApiResult.success();
    }

    @PostMapping("/getUserRoleInfo")
    @ApiOperation(value = "获取当前用户所有角色信息")
    public BaseApiResult getUserRoleInfo(String userId) {
        if (!StpUtil.hasPermission("user.role.query")) {
            return BaseApiResult.error(MessageConstant.AUTH_ERROR_CODE, MessageConstant.NOT_PERMISSION);
        }
        return BaseApiResult.success(userService.getUserRoles(userId));
    }

}