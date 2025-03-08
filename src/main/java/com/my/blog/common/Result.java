package com.my.blog.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 统一API响应结果包装类
 * @param <T> 定义返回数据结构的泛型类型
 */
@Data  // 生成常用的get、set、toString方法（除非字段被final修饰）
@NoArgsConstructor  // Lombok生成无参构造方法
@AllArgsConstructor  // Lombok生成全参构造方法
public class Result<T> {

    /**
     * 状态码（200=成功，其他值=特定错误码）
     */
    private int code;

    /**
     * 业务提示信息（成功时为描述，错误时为原因）
     */
    private String message;

    /**
     * 响应数据主体（无数据时可为空）
     */
    private T data;

    /**
     * 构建成功响应（带自定义消息和数据）
     * @param message 成功提示
     * @param data 响应数据对象
     * @param <T> 泛型类型
     * @return 封装的Result实例
     */
    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(data);
        return result;
    }


    /**
     * 构建错误响应（仅包含错误码和消息）
     * @param code HTTP状态码或自定义服务错误码
     * @param message 错误描述
     * @return 不包含数据体的Result实例
     */
    public static Result<?> error(int code, String message) {
        Result<?> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}