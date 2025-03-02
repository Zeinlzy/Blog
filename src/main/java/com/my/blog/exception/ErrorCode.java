package com.my.blog.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    PARAM_VALIDATION_FAILED(4000, "参数校验失败"),

    USERNAME_ALREADY_EXISTS(4001, "用户名已存在"),

    EMAIL_ALREADY_REGISTERED(4002, "邮箱已被注册"),

    USER_REGISTRATION_FAILED(4003, "用户注册失败"),

    INVALID_CREDENTIALS(4005, "用户名或密码错误"),

    AUTHENTICATION_FAILED(4006, "认证失败"),

    INVALID_CAPTCHA(4007, "验证码无效或已过期"),

    USER_NOT_FOUND(408, "用户不存在"),

    ARTICLE_NOT_FOUND(4009, "文章不存在"),

    NO_PERMISSION_TO_MODIFY(4010, "无权限修改该文章");


    private final int code; //枚举字段应为final，且不可被@data字段生成的setter方法修改
    private final String message;

}
