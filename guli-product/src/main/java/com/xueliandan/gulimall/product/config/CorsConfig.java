package com.xueliandan.gulimall.product.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author zxb 2025/5/30 20:28
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:8001")
                .allowedMethods("*")
                .allowedHeaders("*")     // 允许所有 header，包括 token
                .exposedHeaders("*")
                .allowCredentials(true); // 如果你用了 cookie/session 认证
    }
}