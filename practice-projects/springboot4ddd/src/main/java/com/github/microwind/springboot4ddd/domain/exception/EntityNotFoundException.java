package com.github.microwind.springboot4ddd.domain.exception;

/**
 * 实体未找到异常
 *
 * <p>聚合查找失败的领域语义异常。由仓储 / 应用服务在
 * {@code findById(...).orElseThrow(...)} 等场景抛出，
 * 全局异常处理器映射为 HTTP 404。
 *
 * @author jarry
 * @since 1.0.0
 */
public class EntityNotFoundException extends DomainException {

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String entityName, String fieldName, Object fieldValue) {
        super(String.format("%s 不存在，%s=%s", entityName, fieldName, fieldValue));
    }
}
