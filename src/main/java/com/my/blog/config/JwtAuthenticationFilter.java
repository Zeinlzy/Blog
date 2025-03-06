// JwtAuthenticationFilter.java  位于src/main/java/config包
package com.my.blog.config;

import com.my.blog.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;

/**
 * JWT Authentication Filter (triggered per request)
 * Core responsibilities: intercept requests, verify tokens, inject user permission contexts, and pass them to subsequent filter chains
 * Security specification: Inherit from OncePerRequestFilter to ensure that a single request is processed only once
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;    //JWT parsing tools (including key management)
    private final CustomUserDetailsService userDetailsService;  //User details query service

    public JwtAuthenticationFilter(JwtUtils jwtUtils, CustomUserDetailsService userDetailsService) {
        this.jwtUtils  = jwtUtils;
        this.userDetailsService  = userDetailsService;
    }

    /**
     * Filter core logic (handles all HTTP requests)
     * @param request HTTP request object (used to obtain the header)
     * @param response HTTP Response Object (Unmodified)
     * @param filterChain Filter chain (control flow transfer)
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // Phase 1: Extract tokens
        String authHeader = request.getHeader("Authorization");

        // Handle only Bearer Token format (example :Bearer eyJhbGciOi...)
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);  // Intercept the payload part (remove the prefix)

            try {
                // Phase 2: Parsing and Validation
                Claims claims = jwtUtils.parseToken(token);  //Decode and verify the signature

                // Check if the token has expired (expiration time is later than the current time)
                if (claims.getExpiration().after(new Date())) {

                    String username = claims.getSubject();  //Get the username from the sub field

                    // Phase 3: Permission Context Injection
                    // If the current request is not bound with authentication information (avoid repeated loading)
                    if (username != null && SecurityContextHolder.getContext().getAuthentication()  == null) {
                        // Load user permissions from database (trigger CustomUserDetailsService)
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                        // Build an authentication token (password is left blank because the JWT already contains credentials)
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null, // The credential field is usually null (has been token-verified)
                                        userDetails.getAuthorities()); //permission list

                        // Additional request details (like IP address, SessionID, etc.)
                        authentication.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request));

                        // Update the security context (the subsequent interface authentication directly reads the data here)
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (Exception e) {
                // Catch all exceptions (token tampering/expiration/parsing errors)
                SecurityContextHolder.clearContext();   //Force clear context (prevent residue)
                logger.error("JWT 验证失败: " + e.getMessage());
            }
        }

        // Phase 4: Passing the filter chain
        filterChain.doFilter(request,  response); //Continue with subsequent filters or controller
    }

}