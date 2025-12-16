package com.microwind.knife.interfaces.vo.user;

import lombok.*;

/**
 * 更新用户请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    private String name;     // 用户姓名
    private String phone;    // 用户手机
    private String email;    // 用户邮箱
    private String address;  // 用户地址
}

