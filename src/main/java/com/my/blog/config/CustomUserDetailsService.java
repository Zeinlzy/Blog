package com.my.blog.config;

import com.my.blog.entity.User;
import com.my.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections;


//该类的核心价值在于将数据库存储的用户凭证与Spring Security的安全框架进行适配，是系统认证体系的中枢模块。
@Service
public class CustomUserDetailsService implements UserDetailsService {
    /**
     * MyBatis-Plus用户数据访问接口（确保实现了selectByUsername方法）
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * 用户认证核心逻辑（由Spring Security自动调用）
     * @param username 用户提交的登录名（要求唯一）
     * @return 用户安全实体（包含账号、密码和权限）
     * @throws UsernameNotFoundException 当用户名不存在时抛出（触发登录失败事件）
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //1.查询用户：调用自定义的MyBatis-Plus查询方法
        User user = userRepository.queryByUsername(username); // 自定义查询方法
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在：" + username);
        }

        // 2.角色权限转换：Spring Security要求角色必须以ROLE_为前缀
        // 例如，字符串"admin"必须转换为"ROLE_ADMIN"
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase());
        
        // 3.构建安全实体：使用Spring Security的内置User类（自动验证密码有效性）
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),     //框架用此字段验证用户名
                user.getPassword(),     //数据库中存储的密码必须加密（推荐使用BCrypt）
                Collections.singletonList(authority) // 单个角色的集合
        );
    }
}