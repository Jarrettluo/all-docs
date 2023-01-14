package com.jiaruiblog.controller;

import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.entity.User;
import com.jiaruiblog.entity.dto.BasePageDTO;
import com.jiaruiblog.entity.dto.UserDTO;
import com.jiaruiblog.service.IUserService;
import com.jiaruiblog.util.BaseApiResult;
import com.jiaruiblog.util.JwtUtil;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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


    @Resource
    IUserService userService;


    @Autowired
    private MongoTemplate template;


    @ApiOperation(value = "新增单个用户", notes = "新增单个用户")
    @PostMapping(value = "/insert")
    public BaseApiResult insertObj(@RequestBody User user) {
        user.setCreateDate(new Date());
        template.save(user, COLLECTION_NAME);
        return BaseApiResult.success("新增成功");
    }

    @ApiOperation(value = "批量新增用户", notes = "批量新增用户")
    @PostMapping(value = "/batchInsert")
    public BaseApiResult batchInsert(@RequestBody List<User> users) {
        log.info("批量新增用户入参=={}", users.toString());
        for (User item : users) {
            template.save(item, COLLECTION_NAME);
        }
        return BaseApiResult.success("批量新增成功");
    }

    @ApiOperation(value = "根据id查询", notes = "批量新增用户")
    @PostMapping(value = "/getById")
    public BaseApiResult getById(@RequestBody User user) {
        Query query = new Query(Criteria.where("_id").is(user.getId()));
        User one = template.findOne(query, User.class, COLLECTION_NAME);
        return BaseApiResult.success(one);
    }

    @ApiOperation(value = "根据用户名称查询", notes = "根据用户名称查询")
    @PostMapping(value = "/getByUsername")
    public BaseApiResult getByUsername(@RequestBody User user) {
        Query query = new Query(Criteria.where("username").is(user.getUsername()));
        User one = template.findOne(query, User.class, COLLECTION_NAME);
        return BaseApiResult.success(one);
    }

    @ApiOperation(value = "更新用户hobby和company", notes = "更新用户hobby和company")
    @PutMapping(value = "/updateUser")
    public BaseApiResult updateUser(@RequestBody User user) {
        Query query = new Query(Criteria.where("_id").is(user.getId()));
        Update update = new Update();
        if (StringUtils.hasText(user.getPassword())) {
            update.set("password", user.getPassword());
        }
        update.set("phone", user.getPhone());
        update.set("mail", user.getMail());
        update.set("male", user.getMale());
        update.set("description", user.getDescription());
        UpdateResult updateResult1 = template.updateFirst(query, update, User.class, COLLECTION_NAME);
        if(updateResult1.getMatchedCount() > 0) {
            return BaseApiResult.success("更新成功！");
        }
        return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);


    }

    /**
     * @Author luojiarui
     * @Description 删除用户的时候必须要删除其头像信息
     * @Date 22:40 2023/1/12
     * @Param [user, request]
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    @ApiOperation(value = "根据id删除用户", notes = "根据id删除用户")
    @DeleteMapping(value = "/auth/deleteByID")
    public BaseApiResult deleteById(@RequestBody User user, HttpServletRequest request) {
        // 用户只能删除自己，不能删除其他人的信息
        String userId = (String) request.getAttribute("id");
        if (!userId.equals(user.getId())) {
            return BaseApiResult.error(1201, MessageConstant.OPERATE_FAILED);
        }
        DeleteResult remove = template.remove(user, COLLECTION_NAME);
        if (remove.getDeletedCount() > 0) {
            log.warn("[删除警告]正在删除用户：{}", user);
            return BaseApiResult.success("删除成功");
        } else {
            return BaseApiResult.error(1201, MessageConstant.OPERATE_FAILED);
        }
    }


    /**
     * 模拟用户 登录
     */
    @PostMapping("/login")
    public BaseApiResult login(@RequestBody User user) {
        Query query = new Query(Criteria.where("username").is(user.getUsername()));
        User dbUser = template.findOne(query, User.class, COLLECTION_NAME);
        if (dbUser != null && dbUser.getUsername().equals(user.getUsername())
                && dbUser.getPassword().equals(user.getPassword())) {
            String token = JwtUtil.createToken(dbUser);
            Map<String, String> result = new HashMap<>(8);
            result.put("token", token);
            result.put("userId", dbUser.getId());
            result.put("avatar", dbUser.getAvatar());
            result.put("username", dbUser.getUsername());
            result.put("type", dbUser.getPermissionEnum() != null ? dbUser.getPermissionEnum().toString() : null);
            return BaseApiResult.success(result);
        }
        return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
    }

    /**
     * @return com.jiaruiblog.util.BaseApiResult
     * @Author luojiarui
     * @Description 根据分页参数查询用户列表
     * @Date 21:21 2023/1/10
     * @Param []
     **/
    @GetMapping("/allUsers")
    public BaseApiResult allUsers(@ModelAttribute("pageDTO") BasePageDTO pageDTO) {
        return userService.getUserList(pageDTO);
    }

    @GetMapping("blockUser")
    public BaseApiResult blockUser(@RequestParam("userId") String userId) {
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

}