package com.my.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // 允许特定的前端源，而不是使用通配符
        config.addAllowedOrigin("http://localhost:5173"); // 前端开发服务器地址
        // 如果有其他环境，可以添加多个
        // config.addAllowedOrigin("https://your-production-domain.com");

        // 允许跨域的HTTP方法
        config.addAllowedMethod("*");
        // 允许跨域的请求头
        config.addAllowedHeader("*");
        // 允许携带认证信息（cookies、HTTP认证及客户端SSL证明等）
        config.setAllowCredentials(true);
        // 预检请求的缓存时间（秒）
        config.setMaxAge(3600L);

        // 允许前端获取这些响应头
        config.addExposedHeader("Authorization");

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}