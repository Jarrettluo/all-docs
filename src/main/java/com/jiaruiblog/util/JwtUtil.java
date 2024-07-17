package com.jiaruiblog.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.collect.Maps;
import com.jiaruiblog.entity.User;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName JwtUtil
 * @Description Jwt工具类，生成JWT和认证
 * @Author luojiarui
 * @Date 2022/6/19 9:20 下午
 * @Version 1.0
 **/
public class JwtUtil {

    private JwtUtil() {
        throw new IllegalStateException("jwtUtil error");
    }

    /**
     * 密钥
     */
    private static final String SECRET = "my_secret";

    /**
     * 过期时间：2天
     * 单位为秒
     **/
    private static final long EXPIRATION = 864000L;

    /**
     * 生成用户token,设置token超时时间
     */
    public static String createToken(User user) {
        //过期时间
        Date expireDate = new Date(System.currentTimeMillis() + EXPIRATION * 1000);
        Map<String, Object> map = new HashMap<>(8);
        map.put("alg", "HS256");
        map.put("typ", "JWT");
        // 添加头部
        // 可以将基本信息放到claims中
        // 超时设置,设置过期的日期
        return JWT.create()
                .withHeader(map)
                .withClaim("id", user.getId())
                .withClaim("username", user.getUsername())
                .withClaim("password", user.getPassword())
                .withExpiresAt(expireDate)
                //签发时间
                .withIssuedAt(new Date())
                //SECRET加密
                .sign(Algorithm.HMAC256(SECRET));

    }

    /**
     * 校验token并解析token
     */
    public static Map<String, Claim> verifyToken(String token) {
        DecodedJWT jwt;
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
            jwt = verifier.verify(token);
        } catch (Exception e) {
            //解码异常则抛出异常
            return Maps.newHashMap();
        }
        return jwt.getClaims();
    }

}
