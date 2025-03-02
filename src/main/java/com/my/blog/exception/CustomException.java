package com.my.blog.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

//自定义异常类

@Getter
public class CustomException extends RuntimeException {
    private final int code;
    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }
}