package com.my.blog.service.Impl;

import com.my.blog.dto.request.UpdatePasswordDTO;
import com.my.blog.dto.response.TokenPair;
import com.my.blog.utils.RedisUtils;
import com.my.blog.dto.request.LoginDTO;
import com.my.blog.dto.request.RegisterDTO;
import com.my.blog.entity.User;
import com.my.blog.exception.CustomException;
import com.my.blog.exception.ErrorCode;
import com.my.blog.repository.UserRepository;
import com.my.blog.service.UserService;
import com.my.blog.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;
import java.util.Date;

@Transactional  //保持原子性
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    //final 与 Lombok 的 @RequiredArgsConstructor 完美配合，减少样板代码。
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtils jwtUtils;

    private final RedisUtils redisUtils; // 使用RedisUtils


    @Override
    public User register(RegisterDTO registerDTO) {

        //控制层进行参数校验，服务层进行判断参数是否已经存在
        if (userRepository.existsByUsername(registerDTO.getUsername())) {
            throw new CustomException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }
        if (userRepository.existsByEmail(registerDTO.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_REGISTERED);
        }


        String encodedPwd = passwordEncoder.encode(registerDTO.getPassword());


        // 使用Builder模式创建对象
        User user = User.builder()
                .username(registerDTO.getUsername())
                .password(encodedPwd)
                .email(registerDTO.getEmail())
                .role("USER")
                .build();

        userRepository.insert(user);

        //注册了才放进redis里
        // 使用RedisUtils设置缓存
        String redisKey = "user.register:" + user.getUsername(); // user: 作为前缀，加上用户名作为键名，形成完整的 Redis 键
        redisUtils.set(redisKey, user, 1, TimeUnit.HOURS);

        return user;

    }

    @Override
    public TokenPair login(LoginDTO loginDTO) {
        // 1. 用户验证逻辑
        User user = userRepository.queryByUsername(loginDTO.getUsername());
        if (user == null){
            throw  new CustomException(ErrorCode.USER_NOT_FOUND);
        }


        // 2. 密码验证
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INCORRECT_PASSWORD);
        }

        // 3. 生成双令牌
        String accessToken = jwtUtils.generateToken(user.getUsername(), user.getRole());
        String refreshToken = jwtUtils.generateRefreshToken(user.getUsername(), user.getRole());

        // 4. 存储refreshToken到Redis（使用用户名作为Key）
        redisUtils.set(
                user.getUsername(),
                refreshToken,
                jwtUtils.getRefreshExpiration() / 1000,
                TimeUnit.SECONDS
        );
        return new TokenPair(accessToken, refreshToken);
    }

    public TokenPair refreshToken(String refreshToken) {
        // 1. 基本格式验证
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 2. 验证refreshToken有效性
        if (!jwtUtils.isValidRefreshToken(refreshToken)) {
            throw new CustomException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        // 3. 解析令牌获取用户信息
        Claims claims = jwtUtils.parseToken(refreshToken);
        String username = claims.getSubject();
        String role = claims.get("role", String.class);

        // 4. 验证Redis存储的refreshToken
        String storedToken = (String) redisUtils.get(username);
        if (storedToken == null) {
            throw new CustomException(ErrorCode.REVOKED_REFRESH_TOKEN);
        }
        if (!storedToken.equals(refreshToken)) {
            throw new CustomException(ErrorCode.MISMATCHED_REFRESH_TOKEN);
        }

        // 5. 生成新令牌
        String newAccessToken = jwtUtils.generateToken(username, role);
        String newRefreshToken = jwtUtils.generateRefreshToken(username, role);

        // 6. 更新Redis存储（保持TTL不变）
        redisUtils.set(
                username,
                newRefreshToken,
                jwtUtils.getRefreshExpiration() / 1000,
                TimeUnit.SECONDS
        );

        return new TokenPair(newAccessToken, newRefreshToken);
    }

    @Override
    public void updatePassword(String username, UpdatePasswordDTO passwordDTO) {
        User user = userRepository.queryByUsername(username);
        if (user == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        
        // 验证旧密码
        if (!passwordEncoder.matches(passwordDTO.getOldPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INCORRECT_PASSWORD);
        }
        
        // 更新密码
        user.setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));
        user.setUpdatedAt(new Date());
        userRepository.updateById(user);
        
        // 更新缓存
        String redisKey = "user:" + username;
        redisUtils.set(redisKey, user, 1, TimeUnit.HOURS);
        
        // 使当前的所有令牌失效（可选）
        redisUtils.delete(username);
    }

    
    @Override
    public User selectByUsername(String username) {

        // 使用RedisUtils获取缓存
        String redisKey = "user:" + username;
        User user = (User) redisUtils.get(redisKey);

        if (user == null) {
            user = userRepository.queryByUsername(username);
            if (user != null) {
                redisUtils.set(redisKey, user, 1, TimeUnit.HOURS);
            }
        }

        return user;
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}
