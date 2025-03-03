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

/**
 * This code is the core implementation class of Spring Security user authentication, which mainly accomplishes the following functions:
 * 1.Authentication bridge: Connect the database (through the MyBatis-Plus interface) to the Spring Security framework to implement username query and permission encapsulation
 * 2.Role permission conversion: Convert role fields (such as admin) in the user table to the ROLE_prefix format required by Spring Security
 * 3.Exception handling: throw a standard security framework exception when the user does not exist (UsernameNotFoundException)
 * 4.Standardized output: build a UserDetails object that complies with Spring Security specifications, including three elements: account, password, and permissions.
 */
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
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole());
        
        // 3.Building a security entity: Using Spring Security's Built-in User Class (Automatically Verify Password Validity)
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),     //The framework validates the username with this field
                user.getPassword(),     //The password stored in the database must be encrypted (BCrypt is recommended)
                Collections.singletonList(authority) // A collection of single characters
        );
    }
}