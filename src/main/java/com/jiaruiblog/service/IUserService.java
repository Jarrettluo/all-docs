package com.jiaruiblog.service;

import com.jiaruiblog.auth.PermissionEnum;
import com.jiaruiblog.entity.User;
import com.jiaruiblog.entity.dto.BasePageDTO;
import com.jiaruiblog.entity.dto.RegistryUserDTO;
import com.jiaruiblog.entity.dto.UserRoleDTO;
import com.jiaruiblog.util.BaseApiResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author jiarui.luo
 */
public interface IUserService {

    void initFirstUser();

    BaseApiResult login(RegistryUserDTO userDTO);

    BaseApiResult registry(RegistryUserDTO userDTO);

    /**
     * @Author luojiarui
     * @Description 查询用户列表
     * @Date 21:36 2023/1/10
     * @Param [pageDTO]
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    BaseApiResult getUserList(BasePageDTO pageDTO);

    BaseApiResult changeUserRole(UserRoleDTO userRoleDTO);

    /**
     * @Author luojiarui
     * @Description 屏蔽掉某个用户
     * @Date 21:37 2023/1/10
     * @Param [userId]
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    BaseApiResult blockUser(String userId);

    User queryById(String userId);

    boolean checkPermissionForUser(User user, PermissionEnum[] permissionEnums);

    /**
     * @Author luojiarui
     * @Description 上传用户的头像信息
     * @Date 22:26 2023/1/12
     * @Param []
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    BaseApiResult uploadUserAvatar(String userId, MultipartFile file);

    /**
     * Deleting a user profile picture
     * @param userId user index
     * @return BaseApiResult
     */
    BaseApiResult removeUserAvatar(String userId);

    /**
     * remove user entity
     * @param userId user index
     * @return BaseApiResult
     */
    BaseApiResult removeUser(String userId);

    /**
     * Remove user entities in batches
     * @param userIdList user index
     * @param adminUserId administrator index
     * @return BaseApiResult
     */
    BaseApiResult deleteUserByIdBatch(List<String> userIdList, String adminUserId);

}
