package com.microwind.knife.domain.apiauth;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * API调用者表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "api_users", schema = "public")
public class ApiUsers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "app_code", nullable = false, length = 64, unique = true)
    private String appCode; // 调用方编码，如 h5、miniapp

    @Column(name = "app_name", nullable = false, length = 128)
    private String appName; // 调用方名称

    @Column(name = "secret_key", nullable = false, length = 128)
    private String secretKey; // 调用方秘钥，用于签名

    @Column(name = "status", nullable = false)
    private Short status = 1; // 1启用 0禁用

    @Column(name = "daily_limit")
    private Integer dailyLimit; // 每日调用限额

    @Column(name = "expire_time")
    private LocalDateTime expireTime; // 密钥过期时间

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
