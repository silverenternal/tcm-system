package com.tcm;

import org.springframework.boot.SpringApplication;

/**
 * 测试专用的启动类
 */
public class TestApplication {
    public static void main(String[] args) {
        // 以测试模式启动应用
        System.setProperty("spring.profiles.active", "test");
        SpringApplication app = new SpringApplication(TcmApplication.class);
        app.setWebApplicationType(org.springframework.boot.WebApplicationType.NONE);
        app.run(args);
    }
}