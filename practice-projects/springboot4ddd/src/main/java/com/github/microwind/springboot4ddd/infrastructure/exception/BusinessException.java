package com.github.microwind.springboot4ddd.infrastructure.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 业务异常
 * 用于处理业务逻辑中的异常情况
 *
 * @author jarry
 * @since 1.0.0
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private final int code;

    /**
     * HTTP状态码
     */
    private final HttpStatus status;

    public BusinessException(String message) {
        super(message);
        this.code = HttpStatus.BAD_REQUEST.value();
        this.status = HttpStatus.BAD_REQUEST;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = HttpStatus.BAD_REQUEST.value();
        this.status = HttpStatus.BAD_REQUEST;
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.status = HttpStatus.valueOf(code);
    }

    public BusinessException(HttpStatus status, String message) {
        super(message);
        this.code = status.value();
        this.status = status;
    }

    public BusinessException(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.code = status.value();
        this.status = status;
    }
}
