package com.my.blog.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

//自定义异常类

/**
 * Custom service runtime exception classes are used to encapsulate specific service error codes and error messages
 * Inherited from RuntimeException, it is handled as a non-checked exception by default, and is suitable for scenarios that need to interrupt the current business process
 */
@Getter
public class CustomException extends RuntimeException {
    /**
     * Service Error Status Code (Final Modification to Ensure Thread Safety)
     */
    private final int code;

    /**
     * Exception constructor
     * @param errorCode error code enumeration instances, you need to implement the getCode() and getMessage() methods
     *
     * design specification:
     * 1.Call the parent constructor to initialize the exception message to ensure that standard methods such as printStackTrace() can output error messages correctly
     * 2.It is mandatory to use the ErrorCode enumeration to create exceptions to ensure the correspondence between error codes and messages
     */
    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }
}