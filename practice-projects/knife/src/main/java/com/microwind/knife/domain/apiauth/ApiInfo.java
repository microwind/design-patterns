package com.microwind.knife.domain.apiauth;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * API接口信息表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "api_info", schema = "public")
public class ApiInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "api_path", nullable = false, length = 255, unique = true)
    private String apiPath;

    @Column(name = "api_name", nullable = false, length = 100)
    private String apiName;

    @Column(name = "api_type")
    private Short apiType = 1; // 1-不用sign，2-需要sign

    @Column(name = "fixed_salt", length = 64)
    private String fixedSalt;

    @Column(name = "rate_limit")
    private Integer rateLimit = 100; // 限流（次/分钟）

    @Column(name = "status")
    private Short status = 1;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
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
