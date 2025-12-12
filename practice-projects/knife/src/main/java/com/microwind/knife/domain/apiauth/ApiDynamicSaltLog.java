package com.microwind.knife.domain.apiauth;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 动态盐值记录表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "api_dynamic_salt_log", schema = "public")
public class ApiDynamicSaltLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "app_code", nullable = false, length = 50)
    private String appCode;

    @Column(name = "api_id", nullable = false)
    private Long apiId;

    @Column(name = "api_path", nullable = false, length = 255)
    private String apiPath;

    @Column(name = "dynamic_salt", nullable = false, length = 128)
    private String dynamicSalt;

    @Column(name = "salt_timestamp", nullable = false)
    private Long saltTimestamp;

    @Column(name = "expire_time", nullable = false)
    private LocalDateTime expireTime;

    @Column(name = "used")
    private Short used = 0; // 0-未使用，1-已使用

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
