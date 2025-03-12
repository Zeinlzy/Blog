package com.my.blog.controller;

import com.my.blog.common.Result;
import com.my.blog.config.CustomUserDetailsService;
import com.my.blog.dto.request.LoginDTO;
import com.my.blog.dto.request.RefreshRequest;
import com.my.blog.dto.request.RegisterDTO;
import com.my.blog.dto.request.UpdatePasswordDTO;
import com.my.blog.dto.response.TokenPair;
import com.my.blog.dto.response.TokenResponse;
import com.my.blog.entity.User;
import com.my.blog.exception.CustomException;
import com.my.blog.exception.ErrorCode;
import com.my.blog.service.UserService;
import com.my.blog.utils.JwtUtils;
import com.my.blog.utils.RedisUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "user management", description = "提供用户相关功能") // Swagger 文档标签
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;


    @Operation(summary = "注册", description = "通过用户名，密码和邮箱注册")  //测试通过
    @PostMapping("/register")  //控制层进行参数合法性校验，服务层进行参数重复性校验
    public Result register(@RequestBody @Validated RegisterDTO registerDTO) {
        User user = userService.register(registerDTO);
        return Result.success("登录成功", user);
    }

    @Operation(summary = "登录", description = "通过用户名和密码登录")  //测试通过
    @PostMapping("/login")
    public Result<TokenPair> login(@RequestBody @Validated LoginDTO loginDTO) {

        // 登录前检查账号是否存在或者被封禁/注销
        User user = userService.selectByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new CustomException(ErrorCode.ACCOUNT_DEACTIVATED); // 用户不存在
        }
        if (!user.isEnabled()){
            throw new CustomException(ErrorCode.ACCOUNT_DEACTIVATED); // 账户被停用
        }

        TokenPair tokens = userService.login(loginDTO);
        return Result.success("登录成功", tokens);
    }


    // 添加修改密码功能
    @Operation(summary = "修改密码", description = "修改当前登录用户的密码")
    @PostMapping("/change-password")
    public Result changePassword(@RequestBody @Validated UpdatePasswordDTO passwordDTO) {  //测试通过
        // 获取当前登录用户
        String username = SecurityContextHolder.getContext()    // 获取安全上下文
                .getAuthentication()    // 获取认证信息
                .getName(); // 提取用户名
        userService.updatePassword(username, passwordDTO);
        return Result.success("密码修改成功", null);
    }

    //注销账号
    @Operation(summary = "注销账号", description = "注销当前登录用户的账号")  //测试通过
    @PostMapping("/deactivate")
    public Result deactivateAccount() {
        // 获取当前登录用户
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.deactivateAccount(username);
        return Result.success("账号已成功注销", null);
    }

    @Operation(summary = "刷新令牌", description = "使用refresh token获取新的access token")
    @PostMapping("/refresh-token")
    public Result<TokenPair> refreshToken(@RequestBody @Validated RefreshRequest request) {
        TokenPair tokens = userService.refreshToken(request.getRefreshToken());
        return Result.success("令牌刷新成功", tokens);
    }
    


}