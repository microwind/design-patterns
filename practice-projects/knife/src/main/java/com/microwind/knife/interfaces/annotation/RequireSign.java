package com.microwind.knife.interfaces.annotation;

import java.lang.annotation.*;

/**
 * 签名验证注解
 * <p>
 * 用于标记需要进行签名验证的 Controller 或方法
 * <p>
 * 使用说明：
 * 1. 可以标注在类上，表示整个 Controller 的所有方法都需要签名验证
 * 2. 可以标注在方法上，表示单个方法需要签名验证
 * 3. 如果方法上有 @IgnoreSignHeader 注解，则该方法会跳过签名验证（优先级更高）
 * 4. withParams 参数指定是否需要参数签名（默认读取配置文件）
 * <p>
 * 示例：
 * <pre>
 * // 整个 Controller 需要签名验证（使用默认 withParams 配置）
 * {@code @RequireSign}
 * {@code @RestController}
 * public class UserController { ... }
 *
 * // 单个方法需要签名验证，且需要参数签名
 * {@code @RequireSign(withParams = WithParams.TRUE)}
 * {@code @PostMapping("/submit")}
 * public ApiResponse submit(...) { ... }
 *
 * // 单个方法明确不使用参数签名
 * {@code @RequireSign(withParams = WithParams.FALSE)}
 * {@code @GetMapping("/list")}
 * public ApiResponse list(...) { ... }
 *
 * // 单个方法跳过签名验证（即使类上有 @RequireSign）
 * {@code @IgnoreSignHeader}
 * {@code @PostMapping("/public")}
 * public ApiResponse publicMethod(...) { ... }
 * </pre>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireSign {
    /**
     * 是否需要参数签名
     * <p>
     * - DEFAULT（默认值）: 使用配置文件中的默认值（或继承类级别的配置）
     * - TRUE: 签名计算包含请求参数（使用 SM3 算法）
     * - FALSE: 签名计算不包含请求参数（使用 SHA-256 算法）
     */
    WithParams withParams() default WithParams.DEFAULT;
}
