package com.my.blog.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;

/*
 * 这是一个基于jjwt库实现的JWT工具类，主要功能包括：
1. 生成访问令牌（Access Token）和刷新令牌（Refresh Token）
2. 解析验证JWT令牌
3. 实现令牌刷新机制
4. 通过Spring配置管理密钥和过期时间
 */
@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret; //Secret key, read from the configuration file

    @Value("${jwt.expiration}")
    private Long expiration;  //Token validity period (unit: milliseconds). For example, 3600000 represents 1 hour.

    @Value("${jwt.refreshExpiration}")
    private Long refreshExpiration; // 新增refreshToken过期时间

    /**生成访问令牌（用于常规认证）
     * @param username Username, used as the subject of the Token.
     * @param role User role, stored as a custom claim of the Token.
     * @return The signed JWT string.
     */
    public String generateToken(String username, String role) {

        //Convert the secret key string into a SecretKey object required by the HMAC-SHA512 algorithm.
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.builder()
                .subject(username) //Set the subject (usually the user's unique identifier).
                .claim("role", role)  //Add custom claims (to store role information).
                .issuedAt(new Date())  //Token issuance time
                .expiration(new Date(System.currentTimeMillis() + expiration)) //Expiration time
                .signWith(key, Jwts.SIG.HS512) //Sign using the HMAC-SHA512 algorithm.
                .compact();  //Generate a Token string
    }

    /**
     * Parse and verify the JWT.
     * @param token JWT string
     * @return Claims object, which contains all the claims in the Token (such as username, role, expiration time, etc.).
     * @throws io.jsonwebtoken.JwtException If the Token has expired or the signature is invalid.
     *
     *
     * Security verification process:
     *  1. Reconstruct the SecretKey object using the configured key.
     *  2. Verify the integrity of the token signature.
     *  3. Automatically check the expiration time (exp claim).
     *  4. Return a trusted set of claims.
     */
    public Claims parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes()); // Regenerate the key object.
        return Jwts.parser()
                .verifyWith(key)      // Set the verification key (automatically verify the signature).
                .build()  //Create an instance of the parser.
                .parseSignedClaims(token) // Parse and verify the signature (throw an exception if it fails).
                .getPayload(); //Extract the verified claim data.
    }

    //验证刷新令牌有效性
    public boolean isValidRefreshToken(String refreshToken) {
        try {
            Claims claims = parseToken(refreshToken);
            return claims.get("type").equals("refresh") &&
                    claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    //通过刷新令牌生成新访问令牌
    public String generateNewAccessToken(String refreshToken) {
        Claims claims = parseToken(refreshToken);
        return generateToken(claims.getSubject(), claims.get("role").toString());
    }

    // 生成刷新令牌（长期有效）
    public String generateRefreshToken(String username, String role) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .claim("type", "refresh") // 标识token类型
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(key, Jwts.SIG.HS512)
                .compact();
    }

    // 新增getter方法
    public Long getRefreshExpiration() {
        return refreshExpiration;
    }
}