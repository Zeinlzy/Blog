package com.my.blog.service;

import com.my.blog.dto.request.LoginDTO;
import com.my.blog.dto.request.RegisterDTO;
import com.my.blog.entity.User;
import org.apache.ibatis.annotations.Param;

public interface UserService {

    User register(RegisterDTO registerDTO);

    String login(LoginDTO loginDTO);

    User selectByUsername(@Param("username") String username);


    boolean existsByEmail(@Param("email") String email);


    boolean existsByUsername(@Param("username") String username);


}
