package com.github.microwind.userdemo.exception;

/**
 * 数据库约束违反异常
 */
public class DuplicateKeyException extends BusinessException {

    public DuplicateKeyException(String message) {
        super(400, message);
    }

    public DuplicateKeyException(String message, Throwable cause) {
        super(400, message, cause);
    }
}
