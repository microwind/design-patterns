package com.microwind.knife.domain.user;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "INT")
    private Integer id; // 用户ID

    @Getter
    @Setter
    @Column(name = "name", nullable = false)
    private String name; // 用户姓名

    @Getter
    @Setter
    @Column(name = "phone", nullable = true)
    private String phone; // 用户手机

    @Getter
    @Setter
    @Column(name = "email", nullable = true)
    private String email; // 用户邮箱

    @Column(name = "created_time", updatable = false)
    private LocalDateTime createdAt; // 创建时间

    @Column(name = "updated_time", nullable = false)
    private LocalDateTime updatedAt; // 更新时间

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now; // 确保创建时同时初始化 updatedAt
    }

    @PreUpdate // 必须添加此注解
    public void onUpdate() { // 建议改为 public 访问级别
        this.updatedAt = LocalDateTime.now();
    }
}
