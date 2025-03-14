package com.microwind.springbootorder.middleware;

import com.microwind.springbootorder.utils.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
     * @param errors 验证错误列表（可选）
     * @return 标准化错误响应
     */
    private ResponseEntity<Object> buildErrorResponse(
            HttpStatus status,
            String title,
            String detail,
            List<Map<String, String>> errors) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);

        if (errors != null && !errors.isEmpty()) {
            problemDetail.setProperty("errors", errors);
        }

        // 结构化日志记录
        logger.error("API Error [{}]: {} - {} - {}",
                status.value(),
                title,
                detail,
                errors
        );

        return ResponseEntity.status(status).body(problemDetail);
    }

    /**
     * 处理自定义业务异常（404 Not Found）
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFound(NotFoundException ex) {
        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                "404 Not Found",
                ex.getMessage(),
                null
        );
    }

    /**
     * 处理权限不足异常（403 Forbidden）
     */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDenied(AccessDeniedException ex) {
        return buildErrorResponse(
                HttpStatus.FORBIDDEN,
                "Access Denied",
                "You don't have permission to access this resource",
                null
        );
    }

    /**
     * 重写父类方法处理验证异常（400 Bad Request）
     *
     * @Override 必须添加否则无法正确覆盖父类方法
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        // 提取验证错误详情
        List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fieldError -> {
                    Map<String, String> error = new HashMap<>();
                    error.put("field", fieldError.getField());       // 字段名称
                    error.put("message", fieldError.getDefaultMessage());// 默认错误消息
                    return error;
                })
                .collect(Collectors.toList());

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Validation Failed",
                "Please check the request payload",
                errors
        );
    }
}