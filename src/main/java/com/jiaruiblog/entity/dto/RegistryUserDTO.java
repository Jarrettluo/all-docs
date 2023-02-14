package com.jiaruiblog.entity.dto;

import com.jiaruiblog.common.MessageConstant;
import com.jiaruiblog.common.RegexConstant;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * @ClassName RegistryUserDTO
 * @Description TODO
 * @Author luojiarui
 * @Date 2023/2/14 22:12
 * @Version 1.0
 **/
@Data
public class RegistryUserDTO {

    /*
    SHA(Secure Hash Algorithm，安全散列算法），数字签名等密码学应用中重要的工具，
    被广泛地应用于电子商务等信息安全领域。虽然，SHA与MD5通过碰撞法都被破解了，
    但是SHA仍然是公认的安全加密算法，较之MD5更为安全*/
    public static final String KEY_SHA = "SHA";

    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    @Size(min = 3, max = 32, message = MessageConstant.PARAMS_LENGTH_REQUIRED)
    @Pattern(regexp = RegexConstant.NUM_WORD_REG, message = MessageConstant.PARAMS_FORMAT_ERROR)
    String username;

    @NotNull(message = MessageConstant.PARAMS_IS_NOT_NULL)
    @Size(min = 3, max = 32, message = MessageConstant.PARAMS_LENGTH_REQUIRED)
    @Pattern(regexp = RegexConstant.NUM_WORD_REG, message = MessageConstant.PARAMS_FORMAT_ERROR)
    String password;

    public String getEncodePassword() throws IllegalStateException{
        BigInteger sha;
        byte[] inputData = this.password.getBytes();
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(KEY_SHA);
            messageDigest.update(inputData);
            sha = new BigInteger(messageDigest.digest());
        } catch (Exception e) {
            throw new IllegalStateException();
        }
        return sha.toString(32);
    }
}
