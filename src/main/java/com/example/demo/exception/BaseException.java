package com.example.demo.exception;

/**
 * 业务异常
 */
public class BaseException extends RuntimeException {

    public BaseException() {
        super("业务异常");
    }

    public BaseException(String msg) {
        super(msg);
    }

}
