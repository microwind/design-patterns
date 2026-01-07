package com.github.microwind.springboot4ddd.infrastructure.constants;

/**
 * 错误码常量
 *
 * @author jarry
 * @since 1.0.0
 */
public class ErrorCode {

    // 通用错误码 (1000-1999)
    public static final int SUCCESS = 200;
    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int INTERNAL_ERROR = 500;

    // 业务错误码 (2000-2999)
    public static final int BUSINESS_ERROR = 2000;
    public static final int VALIDATION_ERROR = 2001;
    public static final int DUPLICATE_ERROR = 2002;

    // 用户相关错误码 (3000-3999)
    public static final int USER_NOT_FOUND = 3001;
    public static final int USER_ALREADY_EXISTS = 3002;
    public static final int USER_DISABLED = 3003;

    // 订单相关错误码 (4000-4999)
    public static final int ORDER_NOT_FOUND = 4001;
    public static final int ORDER_STATUS_INVALID = 4002;
    public static final int ORDER_ALREADY_PAID = 4003;

    // 签名相关错误码 (5000-5999)
    public static final int SIGNATURE_INVALID = 5001;
    public static final int SIGNATURE_EXPIRED = 5002;
    public static final int PERMISSION_DENIED = 5003;

    private ErrorCode() {
        // 工具类，禁止实例化
    }
}
