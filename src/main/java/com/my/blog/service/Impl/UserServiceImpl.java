package com.my.blog.service.Impl;

import com.my.blog.dto.request.LoginDTO;
import com.my.blog.dto.request.RegisterDTO;
import com.my.blog.entity.User;
import com.my.blog.exception.CustomException;
import com.my.blog.exception.ErrorCode;
import com.my.blog.repository.UserRepository;
import com.my.blog.service.UserService;
import com.my.blog.common.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional  //保持原子性
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    //final 与 Lombok 的 @RequiredArgsConstructor 完美配合，减少样板代码。
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;


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

        return user;

        // 使用setter模式创建对象
//        User user = new User();
//        user.setUsername(registerDTO.getUsername());
//        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
//        user.setEmail(registerDTO.getEmail());
//        user.setRole("USER"); // 设置默认角色
//        userRepository.insert(user);
//        return user;
    }


    @Override
    public String login(LoginDTO loginDTO) {
        // 1. 验证用户是否存在
        User user = userRepository.selectByUsername(loginDTO.getUsername());
        if (user == null){
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        // 2. 验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(),  user.getPassword()))  {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

        // 3. 生成JWT
        return jwtUtils.generateToken(user.getUsername(),  user.getRole());
    }

    @Override
    public User selectByUsername(String username) {
        return userRepository.selectByUsername(username);
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
