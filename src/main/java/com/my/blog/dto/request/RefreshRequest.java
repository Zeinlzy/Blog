package com.my.blog.dto.request;

import lombok.Data;

@Data
public class RefreshRequest {
    private String refreshToken;
}