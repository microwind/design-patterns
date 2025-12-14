package com.microwind.knife.domain.user;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 用户实体类（纯POJO，用于JdbcTemplate）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Integer id; // 用户ID
    private String name; // 用户姓名
    private String phone; // 用户手机
    private String email; // 用户邮箱
    private String address; // 用户地址
    private LocalDateTime createdTime; // 创建时间
    private LocalDateTime updatedTime; // 更新时间
}
