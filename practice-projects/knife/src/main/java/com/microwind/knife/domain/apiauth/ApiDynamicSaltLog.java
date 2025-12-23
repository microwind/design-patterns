package com.microwind.knife.domain.apiauth;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 动态盐值日志实体
 */
@Entity
@Table(
        name = "api_dynamic_salt_log",
        indexes = {
                @Index(name = "idx_app_path_salt", columnList = "app_code,api_path,dynamic_salt"),
                @Index(name = "idx_expire_time", columnList = "expire_time"),
                @Index(name = "idx_used", columnList = "used")
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiDynamicSaltLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 接口ID（关联 api_info 表）
     */
    @Column(name = "api_id")
    private Long apiId;

    /**
     * 应用编码
     */
    @Column(name = "app_code", nullable = false, length = 50)
    private String appCode;

    /**
     * 接口路径
     */
    @Column(name = "api_path", nullable = false, length = 500)
    private String apiPath;

    /**
     * 动态盐值
     */
    @Column(name = "dynamic_salt", nullable = false, length = 100)
    private String dynamicSalt;

    /**
     * 盐值生成时间戳（毫秒）
     */
    @Column(name = "salt_timestamp")
    private Long saltTimestamp;

    /**
     * 过期时间（null 表示永久有效）
     */
    @Column(name = "expire_time")
    private LocalDateTime expireTime;

    /**
     * 是否已使用 0 未使用 1 已使用
     */
    @Column(name = "used", nullable = false)
    private Short used = 0;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at", nullable = false, updatable = false)
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