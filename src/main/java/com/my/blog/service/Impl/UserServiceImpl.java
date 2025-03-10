package com.my.blog.service.Impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

import java.util.Map;
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

        //1.控制层进行参数合法性校验，服务层进行参数重复性校验
        if (userRepository.existsByUsername(registerDTO.getUsername())) {
            throw new CustomException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }
        if (userRepository.existsByEmail(registerDTO.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_REGISTERED);
        }

        // 2. 防重复提交锁（关键：原子性操作）
        String registerLockKey = "register:lock:" + registerDTO.getUsername();
        // 使用 setIfAbsent + 过期时间的原子操作（需确保 RedisUtils 支持）
        boolean lockAcquired = redisUtils.setIfAbsent(registerLockKey, "1", 5, TimeUnit.SECONDS);
        if (!lockAcquired) {
            throw new CustomException(ErrorCode.OPERATION_TOO_FAST);
        }


        String encodedPwd = passwordEncoder.encode(registerDTO.getPassword());


        // 使用Builder模式创建对象
        User user = User.builder()
                .username(registerDTO.getUsername())
                .password(encodedPwd)
                .email(registerDTO.getEmail())
                .role("USER")
                .enabled(true)
                .build();

        userRepository.insert(user);

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
        Object cachedData = redisUtils.get(redisKey);
        User user = null;

        // 处理缓存数据类型转换
        if (cachedData != null) {
            if (cachedData instanceof User) {
                // 如果缓存中的对象已经是User类型
                user = (User) cachedData;
            } else if (cachedData instanceof Map) {
                // 如果缓存中的对象是Map类型（如LinkedHashMap）
                try {
                    Map<String, Object> userMap = (Map<String, Object>) cachedData;
                    user = convertMapToUser(userMap);
                } catch (Exception e) {
                    // 转换失败，记录日志并从数据库重新获取
                    System.err.println("从缓存转换User对象失败: " + e.getMessage());
                    // 删除有问题的缓存
                    redisUtils.delete(redisKey);
                }
            }
        }

        if (user == null) {
            user = userRepository.queryByUsername(username);
            if (user != null) {
                redisUtils.set(redisKey, user, 1, TimeUnit.HOURS);
            }
        }

        return user;
    }

    // 辅助方法：将Map转换为User对象
    private User convertMapToUser(Map<String, Object> userMap) {
        User user = new User();

        // 设置基本属性
        if (userMap.containsKey("id")) {
            user.setId(Long.valueOf(userMap.get("id").toString()));
        }
        if (userMap.containsKey("username")) {
            user.setUsername((String) userMap.get("username"));
        }
        if (userMap.containsKey("password")) {
            user.setPassword((String) userMap.get("password"));
        }
        if (userMap.containsKey("email")) {
            user.setEmail((String) userMap.get("email"));
        }
        if (userMap.containsKey("role")) {
            user.setRole((String) userMap.get("role"));
        }
        if (userMap.containsKey("enabled")) {
            user.setEnabled((Boolean) userMap.get("enabled"));
        }

        // 处理日期类型
        if (userMap.containsKey("updatedAt")) {
            Object dateObj = userMap.get("updatedAt");
            if (dateObj instanceof Date) {
                user.setUpdatedAt((Date) dateObj);
            } else if (dateObj instanceof Long) {
                user.setUpdatedAt(new Date((Long) dateObj));
            } else if (dateObj instanceof String) {
                // 可以添加字符串日期解析逻辑
            }
        }

        if (userMap.containsKey("createdAt")) {
            Object dateObj = userMap.get("createdAt");
            if (dateObj instanceof Date) {
                user.setCreatedAt((Date) dateObj);
            } else if (dateObj instanceof Long) {
                user.setCreatedAt(new Date((Long) dateObj));
            } else if (dateObj instanceof String) {
                // 可以添加字符串日期解析逻辑
            }
        }

        return user;
    }

    @Override
    public void deactivateAccount(String username) {
        // 1. 查找用户
        User user = selectByUsername(username);
        if (user == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        // 2. 执行注销操作（这里使用软删除，将用户状态标记为已注销）
        user.setEnabled(false);
        user.setUpdatedAt(new Date());
        userRepository.updateById(user);

        // 3. 清除用户缓存
        String redisKey = "user:" + username;
        redisUtils.delete(redisKey);

        // 4. 使当前的所有令牌失效
        redisUtils.delete(username);
    }

    @Override
    public void reactivateAccount(String username) {
        User user = userRepository.queryByUsername(username);
        if (user == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        // 设置账号为启用状态
        user.setEnabled(true);
        user.setUpdatedAt(new Date());
        userRepository.updateById(user);

        // 更新缓存
        String redisKey = "user:" + username;
        redisUtils.set(redisKey, user, 1, TimeUnit.HOURS);
    }

    @Override
    public IPage<User> getAllUsers(int page, int size) {
        // 创建分页对象
        Page<User> pageParam = new Page<>(page, size);
        // 使用MyBatis-Plus的分页查询
        return userRepository.selectPage(pageParam, null);
    }

    @Override
    public void updateUserStatus(String username, boolean enabled) {
        // 检查用户是否存在
        User user = userRepository.queryByUsername(username);
        if (user == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        // 更新用户状态
        int result = userRepository.updateUserStatus(username, enabled);
        if (result <= 0) {
            throw new CustomException(ErrorCode.OPERATION_FAILED);
        }

        // 更新缓存
        String redisKey = "user:" + username;
        redisUtils.delete(redisKey);
    }

    @Override
    public void updateUserRole(String username, String role) {
        // 检查用户是否存在
        User user = userRepository.queryByUsername(username);
        if (user == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        // 验证角色是否有效（可以添加更多的角色验证逻辑）
        if (!"USER".equals(role) && !"ADMIN".equals(role)) {
            throw new CustomException(ErrorCode.INVALID_ROLE);
        }

        // 更新用户角色
        int result = userRepository.updateUserRole(username, role);
        if (result <= 0) {
            throw new CustomException(ErrorCode.OPERATION_FAILED);
        }

        // 更新缓存
        String redisKey = "user:" + username;
        redisUtils.delete(redisKey);
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
