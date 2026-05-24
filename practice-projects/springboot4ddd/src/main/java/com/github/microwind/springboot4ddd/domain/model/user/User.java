package com.github.microwind.springboot4ddd.domain.model.user;

import com.github.microwind.springboot4ddd.domain.exception.UniquenessViolationException;
import com.github.microwind.springboot4ddd.domain.service.user.UserUniquenessChecker;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 用户领域模型（聚合根）
 *
 * <p>纯领域模型，零框架依赖。状态只能通过行为方法迁移，
 * 不暴露任何 setter。唯一性等跨聚合规则通过领域服务
 * {@link UserUniquenessChecker} 在 {@link #register} / {@link #changeEmail} 内部触发。
 *
 * @author jarry
 * @since 1.0.0
 */
@Getter
public class User {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String wechat;
    private String address;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    User(Long id,
         String name,
         String email,
         String phone,
         String wechat,
         String address,
         LocalDateTime createdTime,
         LocalDateTime updatedTime) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.wechat = wechat;
        this.address = address;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
    }

    /**
     * 注册新用户。用户名 / 邮箱唯一性由领域服务校验，校验失败抛出
     * {@link UniquenessViolationException}。
     */
    public static User register(UserUniquenessChecker checker,
                                String name,
                                String email,
                                String phone,
                                String wechat,
                                String address) {
        requireNonBlank(name, "用户名");
        requireNonBlank(email, "邮箱");
        if (checker.existsByName(name)) {
            throw new UniquenessViolationException("用户名已存在: " + name);
        }
        if (checker.existsByEmail(email)) {
            throw new UniquenessViolationException("邮箱已存在: " + email);
        }
        LocalDateTime now = LocalDateTime.now();
        return new User(null, name, email, phone, wechat, address, now, now);
    }

    /**
     * 从持久化数据重建。仅供 infrastructure 层使用。
     */
    public static User restore(Long id,
                               String name,
                               String email,
                               String phone,
                               String wechat,
                               String address,
                               LocalDateTime createdTime,
                               LocalDateTime updatedTime) {
        return new User(id, name, email, phone, wechat, address, createdTime, updatedTime);
    }

    /**
     * 持久化生成主键后由仓储调用一次。命名与 {@code Order.markCreated} 对齐：
     * 表达"持久化完成、聚合根获得身份"的语义，而非简单 setter。
     */
    public void markPersisted(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("用户 ID 已存在，不可重复赋值");
        }
        this.id = id;
    }

    /**
     * 修改邮箱。新值与旧值相同则跳过；不同则通过领域服务校验全局唯一。
     */
    public void changeEmail(UserUniquenessChecker checker, String newEmail) {
        requireNonBlank(newEmail, "邮箱");
        if (Objects.equals(this.email, newEmail)) {
            return;
        }
        if (checker.existsByEmail(newEmail)) {
            throw new UniquenessViolationException("邮箱已存在: " + newEmail);
        }
        this.email = newEmail;
        touch();
    }

    public void changePhone(String newPhone) {
        if (!Objects.equals(this.phone, newPhone)) {
            this.phone = newPhone;
            touch();
        }
    }

    public void changeWechat(String newWechat) {
        if (!Objects.equals(this.wechat, newWechat)) {
            this.wechat = newWechat;
            touch();
        }
    }

    public void changeAddress(String newAddress) {
        if (!Objects.equals(this.address, newAddress)) {
            this.address = newAddress;
            touch();
        }
    }

    private void touch() {
        this.updatedTime = LocalDateTime.now();
    }

    private static void requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + "不能为空");
        }
    }
}
