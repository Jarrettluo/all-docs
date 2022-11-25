package com.jiaruiblog.controller;

import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.entity.User;
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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

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
        log.info("更新用户入参==={}", user.toString());
        Query query = new Query(Criteria.where("_id").is(user.getId()));
        Update update = new Update();
        update.set("hobby", user.getHobby());
        update.set("company", user.getCompany());
        UpdateResult updateResult = template.updateFirst(query, update, User.class, COLLECTION_NAME);
        log.info("更新的结果==={}", updateResult.toString());
        return BaseApiResult.success("更新成功！");
    }

    @ApiOperation(value = "根据id删除用户", notes = "根据id删除用户")
    @DeleteMapping(value = "/auth/deleteByID")
    public BaseApiResult deleteById(@RequestBody User user, HttpServletRequest request) {
        // 用户只能删除自己，不能删除其他人的信息
        String userId = (String) request.getAttribute("id");
        if (!userId.equals(user.getId())) {
            return BaseApiResult.error(1201, MessageConstant.OPERATE_FAILED);
        }
        DeleteResult remove = template.remove(user, COLLECTION_NAME);
        if(remove.getDeletedCount() > 0) {
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
            return BaseApiResult.success(result);
        }
        return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
    }

    @RequestMapping("/allUsers")
    public BaseApiResult allUsers() {
        List<User> users = template.findAll(User.class);
        return BaseApiResult.success(users);
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


}