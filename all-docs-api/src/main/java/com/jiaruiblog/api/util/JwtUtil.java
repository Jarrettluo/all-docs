package com.jiaruiblog.api.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.jiaruiblog.domain.entity.po.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName JwtUtil
 * @Description Jwt工具类，生成JWT和认证
 * @author luojiarui
 * @Date 2022/6/19 9:20 下午
 * @Version 1.0
 **/
@Component
public class JwtUtil {

    private JwtUtil() {
        // Spring bean constructor
    }

    /**
     * 密钥持有类 - 解决静态字段无法注入的问题
     */
    private static class JwtSecretHolder {
        private static String SECRET;
    }

    /**
     * 过期时间：2天
     * 单位为秒
     **/
    private static final long EXPIRATION = 864000L;

    @Value("${jwt.secret}")
    public void setSecret(String secret) {
        JwtSecretHolder.SECRET = secret;
    }

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
                .withExpiresAt(expireDate)
                //签发时间
                .withIssuedAt(new Date())
                //SECRET加密
                .sign(Algorithm.HMAC256(JwtSecretHolder.SECRET));

    }

    /**
     * 校验token并解析token
     */
    public static Map<String, Claim> verifyToken(String token) {
        DecodedJWT jwt;
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(JwtSecretHolder.SECRET)).build();
            jwt = verifier.verify(token);
        } catch (Exception e) {
            //解码异常则抛出异常
            return Map.of();
        }
        return jwt.getClaims();
    }

}