package com.jiaruiblog.application.initial;

import com.jiaruiblog.application.service.IUserService;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


/**
 * @ClassName InitCommandLineRunner
 * @Description 系统启动时执行初始化操作，包括初始化第一个管理员用户
 * @author luojiarui
 * @Date 2023/2/20 22:27
 * @Version 1.0
 **/
@Component
public class InitCommandLineRunner implements CommandLineRunner {

    @Resource
    IUserService userService;

    /**
     * @author luojiarui
     *  系统启动时，执行初始化操作
     **/
    @Override
    public void run(String... args) {
        userService.initFirstUser();
    }
}