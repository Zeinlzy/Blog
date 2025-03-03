package com.my.blog.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;

/**
 * The JWT(JSON Web Token) tool class based on Spring Boot is mainly used to implement the following functions:
 * 1.Generate JWT: Generate a token with a digital signature (HMAC-SHA512 algorithm)
 *  based on the user name, role, key, and expiration time for user identity authentication and permission management.
 * 2.Parsing JWT: verifies the signature validity of the Token and extracts data such as user information (such as user name and role) and expiration time
 * 3.Integration with Spring Security: declare as Spring Bean through the @Component annotation,
 *  and inject key (jwt.secret) and expiration time (jwt.expiration) from the configuration file through @Value, which facilitates unified management of sensitive configurations.
 *
 *
 *  JWT Utility Class, used for generating and parsing JSON Web Tokens (JWT).
 *  Functions: User login authentication, Token signature verification, and encapsulation of permission and role information.
 */
@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret; //Secret key, read from the configuration file

    @Value("${jwt.expiration}")
    private Long expiration;  //Token validity period (unit: milliseconds). For example, 3600000 represents 1 hour.

    /**
     * @param username Username, used as the subject of the Token.
     * @param role User role, stored as a custom claim of the Token.
     * @return The signed JWT string.
     */
    public String generateToken(String username, String role) {

        //Convert the secret key string into a SecretKey object required by the HMAC-SHA512 algorithm.
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.builder()
                .subject(username) //Set the subject (usually the user's unique identifier).
                .claim("role", role)  //Add custom claims (to store role information).
                .issuedAt(new Date())  //Token issuance time
                .expiration(new Date(System.currentTimeMillis() + expiration)) //Expiration time
                .signWith(key, Jwts.SIG.HS512) //Sign using the HMAC-SHA512 algorithm.
                .compact();  //Generate a Token string
    }

    /**
     * Parse and verify the JWT.
     * @param token JWT string
     * @return Claims object, which contains all the claims in the Token (such as username, role, expiration time, etc.).
     * @throws io.jsonwebtoken.JwtException If the Token has expired or the signature is invalid.
     *
     *
     * Security verification process:
     *  1. Reconstruct the SecretKey object using the configured key.
     *  2. Verify the integrity of the token signature.
     *  3. Automatically check the expiration time (exp claim).
     *  4. Return a trusted set of claims.
     */
    public Claims parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes()); // Regenerate the key object.
        return Jwts.parser()
                .verifyWith(key)      // Set the verification key (automatically verify the signature).
                .build()  //Create an instance of the parser.
                .parseSignedClaims(token) // Parse and verify the signature (throw an exception if it fails).
                .getPayload(); //Extract the verified claim data.
    }
}