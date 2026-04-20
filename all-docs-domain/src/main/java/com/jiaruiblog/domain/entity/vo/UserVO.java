package com.jiaruiblog.domain.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jiaruiblog.common.enums.PermissionEnum;
import com.jiaruiblog.common.context.TimeZoneContext;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @ClassName UserVO
 * @Description 返回查询的用户结果
 * @author luojiarui
 * @Date 2023/2/18 00:14
 * @Version 1.0
 **/
@Data
public class UserVO {

    private String id;

    @NotBlank(message = "非空")
    private String username;

    private String phone;

    private String mail;

    private Boolean male = null;

    private String description;

    private String avatar;
    // 封禁状态
    private Boolean banning = false;

    private PermissionEnum permissionEnum;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date birthtime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastLogin;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateDate;

    // Method to adjust timezone
    public void adjustTimeZone() {
        String timezone = TimeZoneContext.getTimeZone();
        if (timezone != null) {
            TimeZone tz = TimeZone.getTimeZone(timezone);
            this.birthtime = adjustDateForTimezone(this.birthtime, tz);
            this.lastLogin = adjustDateForTimezone(this.lastLogin, tz);
            this.createDate = adjustDateForTimezone(this.createDate, tz);
            this.updateDate = adjustDateForTimezone(this.updateDate, tz);
        }
    }

    private Date adjustDateForTimezone(Date date, TimeZone tz) {
        if (date != null) {
            Calendar calendar = Calendar.getInstance(tz);
            calendar.setTime(date);
            return new Date(calendar.getTimeInMillis());
        }
        return date;
    }

}