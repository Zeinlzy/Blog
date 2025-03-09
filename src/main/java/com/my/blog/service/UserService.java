package com.my.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.my.blog.dto.request.LoginDTO;
import com.my.blog.dto.request.RegisterDTO;
import com.my.blog.dto.request.UpdatePasswordDTO;
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

    void updatePassword(String username, UpdatePasswordDTO passwordDTO);

    /**
     * 注销用户账号
     * @param username 用户名
     */
    void deactivateAccount(String username);

    // 添加重新激活账号方法
    void reactivateAccount(String username);

    /**
     * 分页获取所有用户信息
     * @param page 页码
     * @param size 每页大小
     * @return 用户分页数据
     */
    IPage<User> getAllUsers(int page, int size);

    /**
     * 更新用户状态（启用/禁用）
     * @param username 用户名
     * @param enabled 状态
     */
    void updateUserStatus(String username, boolean enabled);

    /**
     * 更新用户角色
     * @param username 用户名
     * @param role 新角色
     */
    void updateUserRole(String username, String role);
}
