package com.my.blog.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT工具类，基于jjwt库实现令牌管理
 * 功能包含：生成/解析令牌、刷新令牌机制、密钥配置化管理
 */
@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;  // 签名密钥，从配置文件注入

    @Value("${jwt.expiration}")
    private Long expiration;  // 访问令牌有效期（毫秒）

    @Value("${jwt.refreshExpiration}")
    private Long refreshExpiration;  // 刷新令牌有效期（毫秒）

    /**
     * 生成访问令牌（包含用户角色信息）
     * @param username 用户唯一标识，作为令牌主题
     * @param role 用户角色，存储为自定义声明
     * @return 签名后的JWT字符串
     */
    public String generateToken(String username, String role) {
        // 将配置的密钥字符串转换为HMAC-SHA算法所需的SecretKey对象
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.builder()
                .subject(username)  // 设置令牌主题
                .claim("role", role)  // 添加角色声明
                .claim("type", "access")  // 明确标识为访问令牌
                .issuedAt(new Date())  // 签发时间
                .expiration(new Date(System.currentTimeMillis() + expiration))  // 动态计算过期时间
                .signWith(key, Jwts.SIG.HS512)  // 使用HS512算法签名
                .compact();  // 生成紧凑的URL安全字符串
    }

    /**
     * 生成长期有效的刷新令牌（包含类型标识）
     * @param username 用户唯一标识
     * @param role 用户角色
     * @return 签名后的刷新令牌字符串
     */
    public String generateRefreshToken(String username, String role) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .claim("type", "refresh")  // 明确令牌类型标识
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpiration))  // 使用独立的长有效期
                .signWith(key, Jwts.SIG.HS512)
                .compact();
    }

    /**
     * 解析并验证JWT令牌有效性
     * @param token JWT字符串
     * @return 包含声明信息的Claims对象
     * @throws io.jsonwebtoken.JwtException 当令牌过期或签名无效时抛出异常
     */
    public Claims parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.parser()
                .verifyWith(key)  // 设置验证密钥
                .build()
                .parseSignedClaims(token)  // 执行签名验证
                .getPayload();  // 获取已验证的声明数据
    }

    /**
     * 验证刷新令牌有效性（包含类型检查和有效期验证）
     * @param refreshToken 刷新令牌字符串
     * @return 是否有效的布尔结果
     */
    public boolean isValidRefreshToken(String refreshToken) {
        try {
            Claims claims = parseToken(refreshToken);
            // 校验令牌类型且未过期
            return "refresh".equals(claims.get("type")) && claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;  // 捕获解析异常视为无效令牌
        }
    }

    /**
     * 通过有效刷新令牌生成新的访问令牌
     * @param refreshToken 已验证的刷新令牌
     * @return 新生成的访问令牌
     */
    public String generateNewAccessToken(String refreshToken) {
        Claims claims = parseToken(refreshToken);
        // 复用用户主体信息和角色生成新令牌
        return generateToken(claims.getSubject(), claims.get("role").toString());
    }

    /**
     * 获取刷新令牌有效期配置（用于外部校验逻辑）
     * @return 以毫秒为单位的有效期值
     */
    public Long getRefreshExpiration() {
        return refreshExpiration;
    }

    public Long getExpiration() {
        return expiration;
    }
}