// 领域层(Domain) - 领域异常基类
//
// 为什么需要领域异常？
// - 用 IllegalArgumentException / IllegalStateException 这类平台异常承载业务错误，
//   会让 catch 子句只能 instanceof 平台异常，无法区分"业务规则不满足"和"平台 bug"。
// - 自建领域异常体系后，接口层可以精确地把"订单未找到"映射为 404、
//   "状态非法"映射为 409、"输入非法"映射为 400，而不是统统 500。
//
// 这是一种"领域语言"的体现：让代码里出现的异常名，本身就讲业务故事。
package com.microwind.javaweborder.domain.exception;

public abstract class OrderDomainException extends RuntimeException {

    protected OrderDomainException(String message) {
        super(message);
    }

    protected OrderDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
