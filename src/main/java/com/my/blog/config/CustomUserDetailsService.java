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
     * MyBatis-Plus user data access interface (make sure the selectByUsername method is implemented)
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * User Authentication Core Logic (automatically invoked by Spring Security)
     * @param username User-submitted login name (unique required)
     * @return User security entity (including account, password, and permissions)
     * @throws UsernameNotFoundException Thrown when the username does not exist (triggers login failure event)
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //1.query user:Invoke the custom MyBatis-Plus query method
        User user = userRepository.selectByUsername(username); // User-defined query methods
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在：" + username);
        }

        // 2.Role permission conversion: Spring Security requires roles must be prefixed with ROLE_
        // For example, the string "admin" must be converted to "ROLE_ADMIN"
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole());
        
        // 3.Building a security entity: Using Spring Security's Built-in User Class (Automatically Verify Password Validity)
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),     //The framework validates the username with this field
                user.getPassword(),     //The password stored in the database must be encrypted (BCrypt is recommended)
                Collections.singletonList(authority) // A collection of single characters
        );
    }
}