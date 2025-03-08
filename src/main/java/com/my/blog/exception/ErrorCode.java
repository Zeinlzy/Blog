package com.my.blog.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 */
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

    NO_PERMISSION_TO_MODIFY(4010, "无权限修改该文章"),
    CATEGORY_NOT_FOUND(4011, "分类不存在"),
    TAG_NOT_FOUND(4012, "标签不存在"),
    CATEGORY_ALREADY_EXISTS(4013,"分类已存在"),
    TAG_ALREADY_EXISTS(4014, "标签已存在"),
    AUTHOR_NOT_FOUND(4015,"作者不存在"),
    INVALID_REFRESH_TOKEN(4010, "无效的刷新令牌"),

    // Token相关错误
    EXPIRED_REFRESH_TOKEN(2002, "刷新令牌已过期"),
    REVOKED_REFRESH_TOKEN(2003, "刷新令牌已被撤销"),
    MISMATCHED_REFRESH_TOKEN(2004, "刷新令牌不匹配"),
    INCORRECT_PASSWORD(2005,"密码不正确"),
    ACCOUNT_DEACTIVATED(2006, "账号已被注销，请联系管理员");


    private final int code; //枚举字段应为final，且不可被@data字段生成的setter方法修改
    private final String message;

}
