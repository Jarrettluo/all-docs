package com.jiaruiblog.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class HmacUtil {

    @Value("${hmac.secret.key}")
    private String secretKey;

    public String generateHmac(String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hmacData = mac.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(hmacData);
    }

    public boolean validateHmac(String data, String hmac) throws Exception {
        String generatedHmac = generateHmac(data);
        return generatedHmac.equals(hmac);
    }
}
