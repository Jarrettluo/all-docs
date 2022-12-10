package com.jiaruiblog.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * @ClassName DocLog
 * @Description TODO
 * @Author luojiarui
 * @Date 2022/12/10 10:58
 * @Version 1.0
 **/
@Data
public class DocLog {

    @Id
    private String id;

    private String userId;

    private String userName;


}
