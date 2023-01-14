package com.jiaruiblog.service.impl;

import com.jiaruiblog.auth.PermissionEnum;
import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.entity.User;
import com.jiaruiblog.entity.dto.BasePageDTO;
import com.jiaruiblog.service.IFileService;
import com.jiaruiblog.service.IUserService;
import com.jiaruiblog.util.BaseApiResult;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * @Author Jarrett Luo
 * @Date 2022/6/24 13:48
 * @Version 1.0
 */
@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    private static final String COLLECTION_NAME = "user";

    @Resource
    MongoTemplate mongoTemplate;

    @Resource
    IFileService fileService;


    @Override
    public BaseApiResult getUserList(BasePageDTO page) {
        long count = mongoTemplate.count(new Query(), User.class, COLLECTION_NAME);
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
        List<User> users = mongoTemplate.find(query, User.class, COLLECTION_NAME);
        Map<String, Object> result = new HashMap<>();
        result.put("total", count);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("result", users);

        return BaseApiResult.success(result);
    }

    @Override
    public BaseApiResult blockUser(String userId) {

        if (!isExist(userId)) {
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(userId));
        Update update = new Update().set("banning", true);
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, User.class, COLLECTION_NAME);
        if (updateResult.getModifiedCount() > 0) {
            return BaseApiResult.success(MessageConstant.SUCCESS);
        }
        return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
    }

    /**
     * 根据用户的主键id查询用户信息
     *
     * @param userId
     * @return
     */
    public boolean isExist(String userId) {
        if (userId == null || "".equals(userId)) {
            return false;
        }
        User user = queryById(userId);
        return user != null;
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

    /**
     * @return boolean
     * @Author luojiarui
     * @Description 检查某个用户是否具有某种权限
     * @Date 21:28 2022/12/7
     * @Param [user, permissionEnum]
     **/
    @Override
    public boolean checkPermissionForUser(User user, PermissionEnum[] permissionEnums) {
        return true;
    }

    /**
     * @Author luojiarui
     * @Description 上传头像到文件的avatar中，保存了多个用户的信息
     * @Date 22:40 2023/1/12
     * @Param [userId, file]
     * @return com.jiaruiblog.util.BaseApiResult
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
        update.set("updateDate", new Date());
        update.set("avatar", gridfsId);
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, COLLECTION_NAME);
        long matchedCount = updateResult.getMatchedCount();
        if (matchedCount > 0) {
            return BaseApiResult.success(MessageConstant.SUCCESS);
        }
        return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
    }

    /**
     * @Author luojiarui
     * @Description 删除某个用户的信息
     * @Date 23:00 2023/1/12
     * @Param [userId]
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    public BaseApiResult removeUser(String userId) {
        User user = mongoTemplate.findById(userId, User.class, COLLECTION_NAME);
        if (user == null) {
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.PARAMS_FORMAT_ERROR);
        }
        fileService.deleteGridFs((String[]) user.getAvatarList().toArray());
        Query query = new Query().addCriteria(Criteria.where("_id").is(userId));
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
        update.set("avatar", null);
        update.set("updateDate", new Date());
        mongoTemplate.updateFirst(query, update, User.class, COLLECTION_NAME);
        return BaseApiResult.success(MessageConstant.SUCCESS);
    }
}
