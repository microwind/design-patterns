package com.github.microwind.springboot4ddd.infrastructure.repository.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户数据对象（Data Object）
 *
 * <p>仅承担"与 users 表字段一对一映射"的职责。领域模型 {@code User}
 * 不再背负持久化细节，转换由 {@link UserConverter} 显式完成。
 *
 * <p>当前数据库未包含 {@code wechat} 列，DO 保留该字段供未来扩展，
 * RowMapper 在读取时容错处理。
 *
 * @author jarry
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDO {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String wechat;
    private String address;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
