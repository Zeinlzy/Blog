package com.my.blog.dto.response;

import lombok.Data;

@Data
public class TokenResponse {
    private String accessToken;

    public TokenResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}