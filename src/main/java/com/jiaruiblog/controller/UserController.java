package com.jiaruiblog.controller;

import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.entity.User;
import com.jiaruiblog.utils.ApiResult;
import com.jiaruiblog.utils.JwtUtil;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
@RequestMapping("/user")
public class UserController {

    static Map<Integer, User> userMap = new HashMap<>();

    private final static String COLLECTION_NAME = "user";

    static {
        //模拟数据库
        User user1 = new User("1L","张三","123456", "fsd", "xc", "c", "dfds", new Date(), new Date());
        userMap.put(1, user1);
        User user2 = new User("2L","李四","123123", "fsd", "xc", "c", "dfds", new Date(), new Date());
        userMap.put(2, user2);
    }

    @Autowired
    private MongoTemplate template;

    @ApiOperation(value = "新增单个用户", notes = "新增单个用户")
    @PostMapping(value = "/insert")
    public ApiResult insertObj(@RequestBody  User user){
        log.info("新增用户入参=={}",user.toString());
        user.setCreateDate(new Date());
        User save = template.save(user, COLLECTION_NAME);
        log.info("插入数据==={}",save.toString());
        return ApiResult.success("新增成功");
    }

    @ApiOperation(value = "批量新增用户", notes = "批量新增用户")
    @PostMapping(value = "/batchInsert")
    public ApiResult batchInsert(@RequestBody List<User> users){
        log.info("批量新增用户入参=={}",users.toString());
        for(User item : users){
            template.save(item, COLLECTION_NAME);
        }
        return ApiResult.success("批量新增成功");
    }

    @ApiOperation(value = "根据id查询", notes = "批量新增用户")
    @PostMapping(value = "/getById")
    public ApiResult getById(@RequestBody  User user){
        Query query = new Query(Criteria.where("_id").is(user.getId()));
        User one = template.findOne(query, User.class, COLLECTION_NAME);
        return ApiResult.success(one);
    }

    @ApiOperation(value = "根据用户名称查询", notes = "根据用户名称查询")
    @PostMapping(value = "/getByUsername")
    public ApiResult getByUsername(@RequestBody  User user){
        Query query = new Query(Criteria.where("username").is(user.getUserName()));
        User one = template.findOne(query, User.class, COLLECTION_NAME);
        return ApiResult.success(one);
    }

    @ApiOperation(value = "更新用户hobby和company", notes = "更新用户hobby和company")
    @PutMapping(value = "/updateUser")
    public ApiResult updateUser(@RequestBody  User user){
        log.info("更新用户入参==={}",user.toString());
        Query query = new Query(Criteria.where("_id").is(user.getId()));
        Update update  = new Update();
        update.set("hobby",user.getHobby());
        update.set("company",user.getCompany());
        UpdateResult updateResult = template.updateFirst(query, update, User.class, COLLECTION_NAME);
        log.info("更新的结果==={}",updateResult.toString());
        return ApiResult.success("更新成功！");
    }

    @ApiOperation(value = "根据id删除用户", notes = "根据id删除用户")
    @DeleteMapping(value = "/deleteByID")
    public ApiResult deleteByID(@RequestBody  User user){
        log.info("根据id删除用户请求==={}",user.toString());
        DeleteResult remove = template.remove(user, COLLECTION_NAME);
        log.info("删除的结果==={}",remove);
        return ApiResult.success("删除成功");
    }


    /**
     * 模拟用户 登录
     */
    @RequestMapping("/login")
    public ApiResult login(User user) {
        Query query = new Query(Criteria.where("username").is(user.getUserName()));
        User dbUser = template.findOne(query, User.class, COLLECTION_NAME);
        if (dbUser.getUserName().equals(user.getUserName()) && dbUser.getPassword().equals(user.getPassword())) {
            log.info("登录成功！生成token！");
            String token = JwtUtil.createToken(dbUser);
            return ApiResult.success(token);
        }
        return ApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
    }

    /**
     * 查询 用户信息，登录后携带JWT才能访问
     */
    @RequestMapping("/secure/getUserInfo")
    public String login(HttpServletRequest request) {
        Integer id = (Integer) request.getAttribute("id");
        String userName = request.getAttribute("userName").toString();
        String password= request.getAttribute("password").toString();
        return "当前用户信息id=" + id + ",userName=" + userName+ ",password=" + password;
    }


}