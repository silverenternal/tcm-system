package com.tcm;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestConfig {
    // 移除Rust服务的测试配置，因为我们现在只使用Java本地服务
}