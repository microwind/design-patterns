package com.github.microwind.springboot4ddd.application.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户数据传输对象（application 层）
 *
 * <p>application 服务对外的统一用户视图。controller 直接序列化返回。
 *
 * @author jarry
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String wechat;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
