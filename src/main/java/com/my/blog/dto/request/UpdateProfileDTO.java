package com.my.blog.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileDTO {
    
    @Size(max = 50, message = "昵称不能超过50个字符")
    private String nickname;
    
    private String avatar;
    
    @Size(max = 200, message = "个人简介不能超过200个字符")
    private String bio;
}