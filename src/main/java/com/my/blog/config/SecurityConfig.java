package com.my.blog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // 启用方法级安全控制,必须开启此注解才能让@PreAuthorize生效
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter; // 注入已创建的Bean

    // 安全规则配置
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF（仅建议开发环境使用）
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/webjars/**",
                                "api/**",
                                "api/users/register",
                                "api/users/login"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                //将自定义的 JWT认证过滤器 插入到 Spring Security 过滤器链中，使其在默认的 用户名密码认证过滤器 之前执行。
                //即JWT认证（无状态）应先于表单登录（有状态）执行,若请求携带有效JWT，直接完成认证，跳过表单登录流程
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // 禁用表单登录和HTTP Basic
                .formLogin(form -> form.disable())  // 禁用默认表单登录
                .httpBasic(basic -> basic.disable());  // 建议禁用 HTTP Basic
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // 无状态会话



        return http.build();
    }

    // 密码编码器（必须配置）
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}