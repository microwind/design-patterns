package com.github.microwind.springboot4ddd.domain.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户领域模型
 *
 * @author jarry
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String name;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 微信号
     */
    private String wechat;

    /**
     * 地址
     */
    private String address;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
}
