package com.my.blog.controller;

import com.my.blog.common.Result;
import com.my.blog.dto.request.LoginDTO;
import com.my.blog.dto.request.RegisterDTO;
import com.my.blog.entity.User;
import com.my.blog.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "user management", description = "user register/login/query") // Swagger 文档标签
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    //http://localhost:8080/api/users/register
    @Operation(summary = "register", description = "register with username, password, email address")
    @PostMapping("/register")  //控制层进行参数校验，服务层进行判断参数是否已经存在
    public Result register(@RequestBody @Validated RegisterDTO registerDTO) {  //如果Controller层没有添加@Valid注解，所有DTO校验规则都会被绕过
        User user = userService.register(registerDTO);
        return Result.success("register success", user);
    }

    //http://localhost:8080/api/users/login
    @Operation(summary = "login", description = "login by username and password")
    @PostMapping("/login")
    public Result<String> login(@RequestBody @Validated LoginDTO loginDTO) {
        String token = userService.login(loginDTO);
        return Result.success("login success",token);
    }
}