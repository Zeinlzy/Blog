package com.my.blog.service.Impl;

import com.my.blog.utils.RedisUtils;
import com.my.blog.dto.request.LoginDTO;
import com.my.blog.dto.request.RegisterDTO;
import com.my.blog.entity.User;
import com.my.blog.exception.CustomException;
import com.my.blog.exception.ErrorCode;
import com.my.blog.repository.UserRepository;
import com.my.blog.service.UserService;
import com.my.blog.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Transactional  //保持原子性
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    //final 与 Lombok 的 @RequiredArgsConstructor 完美配合，减少样板代码。
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RedisUtils redisUtils; // 使用RedisUtils


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
    public String login(LoginDTO loginDTO) {

        // 1. 先检查Redis缓存,
        String redisKey = "user.login:" + loginDTO.getUsername();
        String cachedToken = (String) redisUtils.get(redisKey);

        if (cachedToken != null) {
            return cachedToken;
        }

        // 2. 验证用户是否存在
        User user = userRepository.selectByUsername(loginDTO.getUsername());
        if (user == null){
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        // 3. 验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(),  user.getPassword()))  {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

        // 4. 生成JWT
        String token = jwtUtils.generateToken(user.getUsername(),  user.getRole());

        // 5. 将token存入Redis，设置1小时过期时间
        redisUtils.set(redisKey, token, 1, TimeUnit.HOURS);

        return token;
    }

    @Override
    public User selectByUsername(String username) {

        // 使用RedisUtils获取缓存
        String redisKey = "user:" + username;
        User user = (User) redisUtils.get(redisKey);

        if (user == null) {
            user = userRepository.selectByUsername(username);
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
