package com.my.blog.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Unified API response result wrapper class
 * @param <T> A generic type that defines the structure of the returned data
 */
@Data  //Generate common get, set, toString methods (unless the field is modified by final)
@NoArgsConstructor  //Lombok generates a parameter-free construction method
@AllArgsConstructor  //Lombok generates a full parameter construction method
public class Result<T> {

    /**
     * Status code (200 = Success, other values = specific error code)
     */
    private int code;

    /**
     * Business Prompt Information (Description in case of success, Reason in case of error)
     */
    private String message;

    /**
     * Respond to the data subject (can be empty if no data is available)
     */
    private T data;

    /**
     * Build a success response (with custom messages and data)
     * @param message Success prompts
     * @param data Respond to data objects
     * @param <T>
     * @return Encapsulated Result instance  /ɪn'kæpsjʊleɪt/
     */
    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(data);
        return result;
    }


    /**
     * Build an error response (contains only error codes and messages)
     * @param code HTTP status code or custom service error code
     * @param message Error Description
     * @return A result instance that does not have a data body
     */
    public static Result<?> error(int code, String message) {
        Result<?> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}