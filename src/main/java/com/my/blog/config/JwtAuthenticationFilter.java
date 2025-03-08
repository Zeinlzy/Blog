// JwtAuthenticationFilter.java  位于src/main/java/config包
package com.my.blog.config;

import com.my.blog.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;

/*这是Spring Security框架中处理JWT认证的核心过滤器，作为HTTP请求进入系统的第一道安全防线。主要承担三大职责：
1. 认证网关 拦截所有HTTP请求，识别并验证Authorization头中的JWT令牌，将令牌转换为可识别的用户身份凭证
2. 上下文注入器 通过`SecurityContextHolder` 将认证信息注入安全上下文，为后续接口权限验证（@PreAuthorize等）提供数据支撑
3. 安全隔离层 通过异常处理和上下文清理机制，确保非法/失效的JWT不会污染系统安全状态
*/

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;    // JWT解析工具（包含密钥管理）
    private final CustomUserDetailsService userDetailsService;  // 用户详情查询服务

    public JwtAuthenticationFilter(JwtUtils jwtUtils, CustomUserDetailsService userDetailsService) {
        this.jwtUtils  = jwtUtils;
        this.userDetailsService  = userDetailsService;
    }

    /**
     * 过滤器核心逻辑（处理所有HTTP请求）
     * @param request HTTP请求对象（用于获取请求头）
     * @param response HTTP响应对象（未修改）
     * @param filterChain 过滤器链（控制流程转移）
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        
        // 对于预检请求（OPTIONS），直接放行
        if (request.getMethod().equals("OPTIONS")) {
            filterChain.doFilter(request, response);
            return;
        }
        
                                        // 阶段1：提取令牌
        String authHeader = request.getHeader("Authorization");

        // 仅处理Bearer Token格式（例如：Bearer eyJhbGciOi...）
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);  // 截取有效负载部分（移除前缀）

            try {
                // 阶段2：解析和验证
                Claims claims = jwtUtils.parseToken(token);  // 解码并验证签名

                // 检查令牌是否过期（过期时间晚于当前时间）
                if (claims.getExpiration().after(new Date())) {

                    String username = claims.getSubject();  // 从sub字段获取用户名

                    // 阶段3：权限上下文注入
                    // 如果当前请求未绑定认证信息（避免重复加载）
                    if (username != null && SecurityContextHolder.getContext().getAuthentication()  == null) {
                        // 从数据库加载用户权限（触发CustomUserDetailsService）
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                        // 构建认证令牌（密码留空，因为JWT已包含凭证）
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null, // 凭证字段通常为空（已通过令牌验证）
                                        userDetails.getAuthorities()); // 权限列表

                        // 附加请求详情（如IP地址、会话ID等）
                        authentication.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request));

                        // 更新安全上下文（后续接口认证直接读取这里的数据）
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (Exception e) {
                // 捕获所有异常（令牌篡改/过期/解析错误）
                SecurityContextHolder.clearContext();   // 强制清除上下文（防止残留）
                logger.error("JWT 验证失败: " + e.getMessage());
            }
        }

        // 阶段4：传递过滤器链
        filterChain.doFilter(request,  response); // 继续后续过滤器或控制器
    }

}