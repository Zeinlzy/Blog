package com.my.blog.controller;

import com.my.blog.common.Result;
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

@Tag(name = "用户管理", description = "用户注册、登录、查询接口") // Swagger 文档标签
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    //http://localhost:8080/api/users/register
    @Operation(summary = "注册", description = "通过用户名，密码，邮箱注册用户")
    @PostMapping("/register")  //控制层进行参数校验，服务层进行判断参数是否已经存在
    public Result register(@RequestBody @Validated RegisterDTO registerDTO) {  //如果Controller层没有添加@Valid注解，所有DTO校验规则都会被绕过
        User user = userService.register(registerDTO);
        return Result.success("注册成功", user);
    }
}