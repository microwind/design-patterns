package com.microwind.knife.middleware;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.microwind.knife.exception.ResourceNotFoundException;
import org.hibernate.exception.JDBCConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理控制器建议继承 ResponseEntityExceptionHandler
 * 业务异常使用 @ExceptionHandler 注解处理
 * 框架异常通过重写父类方法处理
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 构建标准化错误响应
     *
     * @param status HTTP 状态码
     * @param title  错误标题
     * @param detail 错误详情
     * @param error 验证错误（可选）
     * @return 标准化错误响应
     */
    private ResponseEntity<Object> buildErrorResponse(
            HttpStatusCode status,
            String title,
            Object detail,
            Object error) {
        ProblemDetail problemDetail = null;
        if (detail != null) {
            if (detail instanceof ProblemDetail) {
                problemDetail = (ProblemDetail) detail;
            } else {
                problemDetail = ProblemDetail.forStatusAndDetail(status, detail.toString());
            }
            problemDetail.setTitle(title);
            if (error != null) {
                problemDetail.setProperty("error", error.toString());
            }
            // 增加一个code返回值
            problemDetail.setProperty("code", status.value());
        }

        // 结构化日志记录
        logger.error("API Error [{}]: {} - {} - {}",
                status.value(),
                title,
                detail,
                error
        );
        return ResponseEntity.status(status).body(problemDetail);
    }

    /**
     * 覆盖ResponseEntityExceptionHandler下的createResponseEntity方法，其他handle都会调用此方法
     */
    @Override
    protected ResponseEntity<Object> createResponseEntity(
            @Nullable Object body,
            HttpHeaders headers,
            HttpStatusCode statusCode,
            WebRequest request) {

        // 正常响应（2xx）直接放行
        if (statusCode.is2xxSuccessful()) {
            return ResponseEntity.status(statusCode).headers(headers).body(body);
        }

        // 其他状态码（例如 400/404/500）仍按原来逻辑封装成 ProblemDetail
        return buildErrorResponse(
                statusCode,
                "error",
                body,
                null
        );
    }


    /**
     * 处理权限不足异常（403 Forbidden）
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDenied(AccessDeniedException ex) {
        return buildErrorResponse(
                HttpStatus.FORBIDDEN,
                "Access Denied",
                "You don't have permission to access this resource.",
                ex
        );
    }

    /**
     * 处理安全异常（403 Forbidden）
     * 例如：应用无权访问特定接口
     */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Object> handleSecurityException(SecurityException ex) {
        return buildErrorResponse(
                HttpStatus.FORBIDDEN,
                "Security Exception",
                ex.getMessage(),
                null
        );
    }

    /**
     * 处理非法参数异常（400 Bad Request）
     * 例如：接口路径不存在、盐值不存在等
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid Argument",
                ex.getMessage(),
                null
        );
    }

    /**
     * 处理JDBC连接失败
     */
    @ExceptionHandler(JDBCConnectionException.class)
    public ResponseEntity<Object> handleDatabaseConnectionException(JDBCConnectionException ex) {
        return buildErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Database connection failed",
                "Database connection error. Please contact the administrator.",
                ex
        );
    }

    /**
     * 处理唯一约束异常（如 Duplicate entry）
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<Object> handleDuplicateKey(SQLIntegrityConstraintViolationException ex) {
        String message = ex.getMessage();

        // 判断是否 Duplicate entry
        if (message != null && message.contains("Duplicate entry")) {
            return buildErrorResponse(
                    HttpStatus.CONFLICT, // 409
                    "Duplicate Data",
                    "数据已存在，不能重复提交。",
                    ex
            );
        }

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Integrity Constraint Violation",
                "数据库字段约束异常，请检查输入数据。",
                ex
        );
    }

    /**
     * 处理数据库操作失败
     */
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<Object> handleSQLException(SQLException ex) {
        return buildErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Database operation failed",
                "Database operation timed out. Please try again after.",
                ex
        );
    }

    /**
     * 处理通用404异常（404 Not Found）
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                "404 Not Found",
                "The resource was not found.",
                ex
        );
    }

    /**
     * 重写父类方法：处理JSON反序列化异常（如未识别的字段）
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        String message = "请求数据格式错误";

        // 检查是否是未识别字段的异常
        Throwable cause = ex.getCause();
        if (cause instanceof UnrecognizedPropertyException) {
            UnrecognizedPropertyException upe = (UnrecognizedPropertyException) cause;
            String fieldName = upe.getPropertyName();
            message = String.format("无效的字段：'%s'。该字段不允许更新或不存在。", fieldName);
        }

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid Request Data",
                message,
                ex
        );
    }

    /**
     * 重写父类方法：处理字段验证异常
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Validation Failed",
                errors,
                ex
        );
    }
}