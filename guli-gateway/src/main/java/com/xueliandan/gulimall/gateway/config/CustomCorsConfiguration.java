package com.xueliandan.gulimall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zxb 2025/6/8 15:37
 */
@Configuration
public class CustomCorsConfiguration {

    /**
     * 网关层统一跨域配置
     *
     * @return 跨域配置
     */
    @Bean
    public CorsWebFilter corsWebFilter() {
        UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
        Map<String, CorsConfiguration> corsConfigurations = new HashMap<>();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedOrigin("*");
        // 允许带上 cookie 信息进行跨域，否则会跨域会丢失 cookie 信息。
        corsConfiguration.setAllowCredentials(Boolean.TRUE);
        corsConfigurations.put("/**", corsConfiguration);

        corsConfigurationSource.setCorsConfigurations(corsConfigurations);
        return new CorsWebFilter(corsConfigurationSource);
    }

}
