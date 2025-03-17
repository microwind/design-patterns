package com.microwind.springbootorder.middleware;

import com.microwind.springbootorder.exception.ResourceNotFoundException;
import org.hibernate.exception.JDBCConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLException;

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
    protected ResponseEntity<Object> createResponseEntity(@Nullable Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
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
}