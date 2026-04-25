package com.jiaruiblog.common.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Base64;

@Component
public class HmacUtil {

    @Value("${hmac.secret.key}")
    private String secretKey;

    public String generateHmac(String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hmacBytes = mac.doFinal(data.getBytes());

        // 使用Base64进行URL安全的编码，生成的 hmac 是 URL 安全的，不会包含 `+`, `/` 或 `=`
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hmacBytes);
    }

    public boolean validateHmac(String data, String hmac) throws Exception {
        if (data == null || hmac == null) {
            return false;
        }
        String generatedHmac = generateHmac(data);
        return MessageDigest.isEqual(generatedHmac.getBytes(), hmac.getBytes());
    }
}
