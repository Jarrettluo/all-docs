package com.jiaruiblog.common;

/**
 * @ClassName RegexConstant
 * @Description 正则表达式
 * @author luojiarui
 * @Date 2022/11/8 23:23
 * @Version 1.0
 **/
public class RegexConstant {

    private RegexConstant() {
        throw new IllegalStateException("RegexConstant class error!");
    }

    /**
     * 中英文下划线横向，1-64位
     */
    public static final String CH_ENG_WORD = "^[\\u4E00-\\u9FA5A-Za-z0-9_-]{1,64}$";

    /**
     * @author luojiarui
     * @Description 只能是数字，大小字母，下划线组成
     * @Date 22:17 2023/2/14
     * @Param
     **/
    // 数字字母下划线
    public static final String NUM_WORD_REG = "^[A-Za-z0-9_]+$";

    // 邮箱
    public static final String MAIL_REG = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";

    // 11位手机号 - 使用宽松验证，适应不断更新的号段
    public static final String PHONE_REG = "^1\\d{10}$";

    // 1-32个中英文下划线
    public static final String NICKNAME_REG = "^[\\u4E00-\\u9FA5A-Za-z0-9_-]{1,32}$";

}
