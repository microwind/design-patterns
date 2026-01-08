package com.github.microwind.springboot4ddd.interfaces.vo.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户响应DTO
 *
 * @author jarry
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;

    private String name;

    private String email;

    private String phone;

    private String wechat;

    private String address;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
