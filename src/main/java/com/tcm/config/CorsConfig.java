package com.tcm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("http://localhost:8080"); // 允许前端来源
        config.addAllowedOriginPattern("http://127.0.0.1:8080"); // 允许前端来源
        config.addAllowedOriginPattern("http://localhost:*"); // 允许localhost的所有端口
        config.addAllowedMethod("*"); // 允许所有方法
        config.addAllowedHeader("*"); // 允许所有头
        config.setAllowCredentials(true); // 允许携带凭据
        config.setMaxAge(3600L); // 预检请求缓存时间

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}