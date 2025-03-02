package com.my.blog.exception;

import com.my.blog.common.Result;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //捕获自定义异常类
    @ExceptionHandler(CustomException.class)
    public Result<?> handleCustomException(CustomException e) {
        return Result.error(e.getCode(), e.getMessage());
    }

    //捕获参数校验异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidationException(MethodArgumentNotValidException ex) {
        String errorDetails = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                // 格式示例：用户名不能为空（username）
                .map(error -> String.format("%s （%s）", error.getDefaultMessage(),  error.getField()))
                .collect(Collectors.joining(" ；"));

        return Result.error(
                ErrorCode.PARAM_VALIDATION_FAILED.getCode(),
                ErrorCode.PARAM_VALIDATION_FAILED.getMessage()  + "：" + errorDetails
        );
    }


    //捕获其他运行时异常
    @ExceptionHandler(RuntimeException.class)
    public Result<?> handleRuntimeException(RuntimeException e) {
        return Result.error(400, e.getMessage());
    }
}