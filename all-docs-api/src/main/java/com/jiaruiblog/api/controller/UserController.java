package com.jiaruiblog.api.controller;

import com.auth0.jwt.interfaces.Claim;
import com.jiaruiblog.api.auth.Permission;
import com.jiaruiblog.common.enums.PermissionEnum;
import com.jiaruiblog.common.ApiResult;
import com.jiaruiblog.common.ConfigConstant;
import com.jiaruiblog.infrastructure.config.SystemConfig;
import com.jiaruiblog.domain.entity.User;
import com.jiaruiblog.domain.entity.bo.UserBO;
import com.jiaruiblog.domain.entity.dto.*;
import com.jiaruiblog.domain.entity.vo.PageVO;
import com.jiaruiblog.domain.entity.vo.UserVO;
import com.jiaruiblog.common.exception.BusinessException;
import com.jiaruiblog.common.exception.ErrorCode;
import com.jiaruiblog.application.service.IUserService;
import com.jiaruiblog.application.transformer.DTO2BOConverter;
import com.jiaruiblog.api.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @ClassName UserController
 * @Description 关于用户的所有请求关系
 * @author luojiarui
 * @Date 2022/6/4 9:38 上午
 * @Version 1.0
 **/
@Tag(name = "用户模块")
@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    private static final String REQUEST_USER_ID = "id";

    static final Map<String, String> fieldRegx = new HashMap<>(8);

    static {
        // 1-64个数字字母下划线
        fieldRegx.put("password", "^[0-9a-z_]{1,64}$");
        fieldRegx.put("phone", "^1(3\\d|4[5-9]|5[0-35-9]|6[567]|7[0-8]|8\\d|9[0-35-9])\\d{8}$");
        fieldRegx.put("mail", "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");
        // 1-140个任意字符
        fieldRegx.put("description", "(.*){1,140}");
    }

    @Resource
    IUserService userService;

    @Resource
    SystemConfig systemConfig;


    @Operation(summary = "新增单个用户", description = "新增单个用户")
    @PostMapping(value = "/insert")
    public ApiResult<Object> insertObj(@RequestBody @Valid RegistryUserDTO userDTO) {
        // 判断是否开启用户注册
        if (Boolean.FALSE.equals(systemConfig.getUserRegistry())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        userService.registry(userDTO);
        return ApiResult.success();
    }

    @Operation(summary = "批量新增用户", description = "批量新增用户; 支持使用xls进行导入用户信息")
    @PostMapping(value = "/batchInsert")
    public ApiResult<Object> batchInsert(@RequestBody List<RegistryUserDTO> userDTOS) {
        for (RegistryUserDTO item : userDTOS) {
            userService.registry(item);
        }
        return ApiResult.success();
    }

    @Operation(summary = "根据id查询", description = "根据id查询用户信息")
    @PostMapping(value = "/getById")
    public ApiResult<Object> getById(@RequestBody UserDTO user) {
        User one = userService.queryById(user.getId());
        // 增加对无效用户的判断
        if (Objects.isNull(one)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return ApiResult.success(one);
    }

    @Operation(summary = "根据用户名称查询", description = "根据用户名称查询")
    @PostMapping(value = "/getByUsername")
    public ApiResult<Object> getByUsername(@RequestBody RegistryUserDTO user) {
        User one = userService.queryByUsername(user.getUsername());
        return ApiResult.success(one);
    }

    /**
     * @author luojiarui
     * @Description 仅限普通用户对自身的信息进行更新；不能更新其权限信息
     * @Date 23:25 2024/7/26
     * @Param [userDTO]
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    @Operation(summary = "更新用户hobby和company", description = "更新用户hobby和company")
    @PutMapping(value = "/updateUser")
    public ApiResult<Void> updateUser(@RequestBody UserDTO userDTO) {
        // 传入的参数数据不对，则返回参数不正确
        if (checkUserDTOParams(userDTO)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 检查修改参数信息
        UserBO userBO = DTO2BOConverter.userDTO2BO(userDTO);
        // 个人用户对自己的信息进行更改
        boolean result = userService.updateUserBySelf(userBO);
        if (result) {
            return ApiResult.success();
        }
        throw new BusinessException(ErrorCode.OPERATE_FAILED);
    }

    /**
     * @return com.jiaruiblog.util.BaseApiResult
     * @author luojiarui
     * @Description 删除用户的时候必须要删除其头像信息
     * @Date 22:40 2023/1/12
     * @Param [user, request]
     **/
    @Permission(PermissionEnum.ADMIN)
    @Operation(summary = "根据id删除用户", description = "根据id删除用户")
    @DeleteMapping(value = "/auth/deleteByID")
    public ApiResult<Object> deleteById(@RequestBody UserDTO removeUser, HttpServletRequest request) {
        String userId = (String) request.getAttribute(REQUEST_USER_ID);
        // 不能删除自己的账号
        String removeUserId = removeUser.getId();
        if (userId == null || userId.equals(removeUserId)) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED);
        }
        userService.removeUser(removeUserId);
        return ApiResult.success();
    }

    /**
     * @return com.jiaruiblog.util.BaseApiResult
     * @author luojiarui
     * @Description 管理员批量删除， 注意删除用户的时候必须要删除其头像信息
     * @Date 22:40 2023/1/12
     * @Param [user, request]
     **/
    @Operation(summary = "根据id删除用户", description = "根据id删除用户")
    @Permission(value = PermissionEnum.ADMIN)
    @DeleteMapping(value = "/auth/deleteByIDBatch")
    public ApiResult<Object> deleteByIdBatch(@RequestBody BatchIdDTO batchIdDTO, HttpServletRequest request) {
        // 用户只能删除自己，不能删除其他人的信息
        String adminUserId = (String) request.getAttribute(REQUEST_USER_ID);
        List<String> userIdList = Optional.ofNullable(batchIdDTO.getIds()).orElse(new ArrayList<>());
        if (userIdList.size() > ConfigConstant.MAX_DELETE_NUM) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        userService.deleteUserByIdBatch(userIdList, adminUserId);
        return ApiResult.success();
    }


    /**
     * 模拟用户 登录
     */
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public ApiResult<Object> login(@RequestBody RegistryUserDTO user) {
        return ApiResult.success(userService.login(user));
    }

    /**
     * 模拟用户 登录
     */
    @Operation(summary = "用户登录")
    @GetMapping("/checkLoginState")
    public ApiResult<Object> checkLoginState(HttpServletRequest request, HttpServletResponse response) {
        // 缓存 2s; 避免前端频繁刷新
        response.setHeader("Cache-Control", "max-age=2, public");
        //获取 header里的token
        final String token = request.getHeader("authorization");
        if (!StringUtils.hasText(token)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Map<String, Claim> userData = JwtUtil.verifyToken(token);
        if (CollectionUtils.isEmpty(userData)) {
            throw new BusinessException(ErrorCode.OPERATE_FAILED);
        }
        return ApiResult.success();
    }

    /**
     * @return com.jiaruiblog.util.BaseApiResult
     * @author luojiarui
     * @Description 根据分页参数查询用户列表
     * @Date 21:21 2023/1/10
     * @Param []
     **/
    @Operation(summary = "管理员查询全部用户信息", description = "只有管理员有权限进行用户列表查询")
    @Permission(PermissionEnum.ADMIN)
    @GetMapping("/allUsers")
    public ApiResult<PageVO<UserVO>> allUsers(@ModelAttribute("pageDTO") BasePageDTO pageDTO) {
        return ApiResult.success(userService.getUserList(pageDTO));
    }

    @Operation(summary = "改变用户权限", description = "管理员能够调整其他人的角色，不能调整自己的角色")
    @Permission(PermissionEnum.ADMIN)
    @PutMapping("changeUserRole")
    public ApiResult<Object> changeUserRole(@RequestBody UserRoleDTO userRoleDTO, HttpServletRequest request) {
        String adminUserId = (String) request.getAttribute(REQUEST_USER_ID);
        // 不能屏蔽自己的账号
        if (userRoleDTO.getUserId().equals(adminUserId)) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED);
        }
        userService.changeUserRole(userRoleDTO);
        return ApiResult.success();
    }

    /**
     * @return com.jiaruiblog.util.BaseApiResult
     * @author luojiarui
     * @Description 屏蔽用户，使用户不可登录；再次调用此接口则取消屏蔽
     * @Date 20:30 2023/2/12
     * @Param [userId]
     **/
    @Operation(summary = "管理员屏蔽用户", description = "管理员不能屏蔽自己的账号")
    @Permission(PermissionEnum.ADMIN)
    @GetMapping("blockUser")
    public ApiResult<Object> blockUser(@RequestParam("userId") String userId, HttpServletRequest request) {
        if (!StringUtils.hasText(userId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String adminUserId = (String) request.getAttribute(REQUEST_USER_ID);
        // 不能屏蔽自己的账号
        if (userId.equals(adminUserId)) {
            throw new BusinessException(ErrorCode.PERMISSION_DENIED);
        }
        userService.blockUser(userId);
        return ApiResult.success();
    }

    /**
     * 查询 用户信息，登录后携带JWT才能访问
     */
    @RequestMapping("/secure/getUserInfo")
    public String login(HttpServletRequest request) {
        String id = (String) request.getAttribute("id");
        String userName = request.getAttribute("username").toString();
        return "当前用户信息id=" + id + ",userName=" + userName;
    }

    /**
     * @return com.jiaruiblog.util.BaseApiResult
     * @author luojiarui
     * @Description 更新用户的基本信息，只有管理员具有修改权限
     * @Date 13:07 2022/12/18
     * @Param [userDTO]
     **/
    @Permission(PermissionEnum.ADMIN)
    @PutMapping("/auth/updateUserInfo")
    public ApiResult<String> updateUserInfo(@RequestBody UserDTO userDTO) {
        // 传入的参数数据不对，则返回参数不正确
        if (checkUserDTOParams(userDTO)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserBO userBO = DTO2BOConverter.userDTO2BO(userDTO);
        boolean b = userService.updateUserByAdmin(userBO);
        if (b) {
            return ApiResult.success();
        }
        throw new BusinessException(ErrorCode.OPERATE_FAILED);
    }

    @Operation(summary = "上传用户的头像", description = "上传当前登录用户的头像")
    @PostMapping("/auth/uploadUserAvatar")
    public ApiResult<Object> uploadUserAvatar(@RequestParam(value = "img") MultipartFile file, HttpServletRequest request) {
        String userId = (String) request.getAttribute("id");
        String type = file.getContentType();
        String[] availableTypes = new String[]{"image/png", "image/jpeg", "image/gif"};
        if (!Arrays.asList(availableTypes).contains(type)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        userService.uploadUserAvatar(userId, file);
        return ApiResult.success();
    }

    @Operation(summary = "删除用户头像", description = "删除当前登录用户的头像")
    @DeleteMapping("/auth/removeUserAvatar")
    public ApiResult<Object> removeUserAvatar(HttpServletRequest request) {
        userService.removeUserAvatar((String) request.getAttribute("id"));
        return ApiResult.success();
    }

    @Operation(summary = "重置用户密码", description = "管理员对用户进行密码重置")
    @PostMapping("auth/resetUserPwd")
    public ApiResult<Object> resetUserPwd(@RequestBody String userId, HttpServletRequest request) {
        String adminId = (String) request.getAttribute("id");
        userService.resetUserPwd(userId, adminId);
        return ApiResult.success();
    }

    @Operation(summary = "用户发起找回密码的请求，发送token给邮箱")
    @PostMapping("/generateResetToken")
    public ApiResult<Object> generateResetToken() {
        throw new UnsupportedOperationException("Password reset via email is not yet implemented");
    }

    @Operation(summary = "用户重置密码")
    @PostMapping("/resetPassword")
    public ApiResult<Object> resetPassword() {
        throw new UnsupportedOperationException("Password reset via email is not yet implemented");
    }

    private static boolean patternMatch(String s, String regex) {
        return !Pattern.compile(regex).matcher(s).matches();
    }

    /**
     * @author luojiarui
     * @Description 检查用户更新的信息符合要求
     * @Date 23:04 2024/7/23
     * @Param [userDTO]
     * @return boolean 符合要求返回true，不符合要求返回false
     **/
    public static boolean checkUserDTOParams(UserDTO userDTO) {
        if (StringUtils.hasText(userDTO.getPassword())
                && !patternMatch(userDTO.getPassword(), fieldRegx.get("password"))) {
            return false;
        }
        if (StringUtils.hasText(userDTO.getMail())
                && !patternMatch(userDTO.getMail(), fieldRegx.get("mail"))) {
            return false;
        }

        if (StringUtils.hasText(userDTO.getPhone())
                && !patternMatch(userDTO.getPhone(), fieldRegx.get("phone"))) {
            return false;
        }
        return !(StringUtils.hasText(userDTO.getDescription())
                && !patternMatch(userDTO.getDescription(), fieldRegx.get("description")));
    }

}