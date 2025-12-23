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
    private Short apiType = 1; // 1-不需要签名，2-需要签名

    /**
     * API类型枚举
     */
    public enum ApiType {
        NO_SIGN(1, "不需要签名"),
        NEED_SIGN(2, "需要签名");

        private final int code;
        private final String description;

        ApiType(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        /**
         * 根据code获取枚举
         */
        public static ApiType fromCode(int code) {
            for (ApiType type : values()) {
                if (type.code == code) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown api type code: " + code);
        }
    }

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
