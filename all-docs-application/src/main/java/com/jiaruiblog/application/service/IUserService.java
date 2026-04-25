package com.jiaruiblog.application.service;

import com.jiaruiblog.common.enums.PermissionEnum;
import com.jiaruiblog.domain.entity.po.User;
import com.jiaruiblog.domain.entity.bo.UserBO;
import com.jiaruiblog.domain.entity.dto.BasePageDTO;
import com.jiaruiblog.domain.entity.dto.RegistryUserDTO;
import com.jiaruiblog.domain.entity.dto.UserRoleDTO;
import com.jiaruiblog.domain.entity.vo.PageVO;
import com.jiaruiblog.domain.entity.vo.UserVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @author jiarui.luo
 */
public interface IUserService {

    void initFirstUser();

    Map<String, String> login(RegistryUserDTO userDTO);


    /**
     * 注册用户
     * @param userDTO 用户注册信息
     */
    void registry(RegistryUserDTO userDTO);

    /**
     * @author luojiarui
     * @Description 查询用户列表
     * @Date 21:36 2023/1/10
     * @Param [pageDTO]
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    PageVO<UserVO> getUserList(BasePageDTO pageDTO);

    void changeUserRole(UserRoleDTO userRoleDTO);

    /**
     * @author luojiarui
     * @Description 屏蔽掉某个用户
     * @Date 21:37 2023/1/10
     * @Param [userId]
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    void blockUser(String userId);

    User queryById(String userId);

    User queryByUsername(String username);

    boolean checkPermissionForUser(User user, PermissionEnum[] permissionEnums);

    /**
     * @author luojiarui
     * @Description 上传用户的头像信息
     * @Date 22:26 2023/1/12
     * @Param []
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    void uploadUserAvatar(String userId, MultipartFile file);

    /**
     * Deleting a user profile picture
     * @param userId user index
     * @return BaseApiResult
     */
    void removeUserAvatar(String userId);

    /**
     * 获取用户头像字节数组
     * @param objectKey MinIO中的对象key (包含路径前缀，如 avatars/{username}/{filename})
     * @return 头像字节数组
     */
    byte[] getAvatarBytes(String objectKey);

    /**
     * remove user entity
     * @param userId user index
     * @return BaseApiResult
     */
     void removeUser(String userId);

    /**
     * Remove user entities in batches
     * @param userIdList user index
     * @param adminUserId administrator index
     * @return BaseApiResult
     */
    void deleteUserByIdBatch(List<String> userIdList, String adminUserId);

    Map<String, String> queryUserAvatarBatch(List<String> userIdList);

    /**
     * @author luojiarui
     * @Description 重置密码
     * @Date 20:08 2023/5/3
     * @Param [userId, adminId] 被充值的用户id， 管理者的id
     * @return com.jiaruiblog.util.BaseApiResult
     **/
    void resetUserPwd(String userId, String adminId);


    boolean isExist(String userId);

    /**
     * @author luojiarui
     * @Description 用户自行对自己的信息进行修改
     * @Date 17:49 2024/7/23
     * @Param [userBO] 传入的参数已经是经过校验的了
     * @return boolean 更新个人信息结果
     **/
    boolean updateUserBySelf(UserBO userBO);

    /**
     * @author luojiarui
     * @Description 管理员对某个用户的信息进行修改
     * @Date 23:34 2024/7/26
     * @Param [userBO]
     * @return boolean
     **/
    boolean updateUserByAdmin(UserBO userBO);

}