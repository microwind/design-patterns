package com.github.microwind.springboot4ddd.infrastructure.common;

import com.github.microwind.springboot4ddd.infrastructure.exception.BusinessException;
import com.github.microwind.springboot4ddd.infrastructure.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 *
 * @author jarry
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理资源未找到异常
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return ApiResponse.error(404, ex.getMessage());
    }

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusinessException(BusinessException ex) {
        log.warn("Business exception: code={}, message={}", ex.getCode(), ex.getMessage());
        return ApiResponse.error(ex.getCode(), ex.getMessage());
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        String errorMessage = bindingResult.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));

        log.warn("Validation failed: {}", errorMessage);
        return ApiResponse.error(400, errorMessage);
    }

    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("IllegalArgumentException: {}", ex.getMessage());
        return ApiResponse.error(400, ex.getMessage());
    }

    /**
     * 处理非法状态异常
     */
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleIllegalStateException(IllegalStateException ex) {
        log.error("IllegalStateException: {}", ex.getMessage(), ex);
        return ApiResponse.serverError(ex.getMessage());
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleRuntimeException(RuntimeException ex) {
        log.error("RuntimeException: {}", ex.getMessage(), ex);
        return ApiResponse.serverError("Internal server error: " + ex.getMessage());
    }

    /**
     * 处理其他所有异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleException(Exception ex) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return ApiResponse.serverError("System error, please contact administrator");
    }
}
