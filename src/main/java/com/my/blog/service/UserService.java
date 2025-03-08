package com.my.blog.service;

import com.my.blog.dto.request.LoginDTO;
import com.my.blog.dto.request.RegisterDTO;
import com.my.blog.dto.response.TokenPair;
import com.my.blog.entity.User;
import org.apache.ibatis.annotations.Param;

public interface UserService {

    User register(RegisterDTO registerDTO);

    TokenPair login(LoginDTO loginDTO);

    User selectByUsername(@Param("username") String username);


    boolean existsByEmail(@Param("email") String email);


    boolean existsByUsername(@Param("username") String username);

    // 添加 refreshToken 方法声明
    TokenPair refreshToken(String refreshToken);

}
