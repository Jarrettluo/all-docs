package com.jiaruiblog.application.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.jiaruiblog.application.service.ITokenService;
import com.jiaruiblog.domain.entity.po.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Token服务实现类
 * 使用JWT实现Token生成和验证
 */
@Service
public class TokenServiceImpl implements ITokenService {

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

    @Override
    public String createToken(User user) {
        Date expireDate = new Date(System.currentTimeMillis() + EXPIRATION * 1000);
        Map<String, Object> map = new HashMap<>(8);
        map.put("alg", "HS256");
        map.put("typ", "JWT");
        return JWT.create()
                .withHeader(map)
                .withClaim("id", user.getId())
                .withClaim("username", user.getUsername())
                .withExpiresAt(expireDate)
                .withIssuedAt(new Date())
                .sign(Algorithm.HMAC256(JwtSecretHolder.SECRET));
    }

    @Override
    public Map<String, Claim> verifyToken(String token) {
        DecodedJWT jwt;
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(JwtSecretHolder.SECRET)).build();
            jwt = verifier.verify(token);
        } catch (Exception e) {
            return Map.of();
        }
        return jwt.getClaims();
    }
}