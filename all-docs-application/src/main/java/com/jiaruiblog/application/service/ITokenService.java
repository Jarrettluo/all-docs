package com.jiaruiblog.application.service;

import com.jiaruiblog.domain.entity.po.User;
import com.auth0.jwt.interfaces.Claim;

import java.util.Map;

/**
 * Token服务接口
 * 负责生成和验证用户认证Token
 */
public interface ITokenService {

    /**
     * 根据用户信息生成Token
     */
    String createToken(User user);

    /**
     * 验证Token并返回解析后的用户数据
     */
    Map<String, Claim> verifyToken(String token);
}