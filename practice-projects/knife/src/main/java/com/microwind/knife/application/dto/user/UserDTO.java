package com.microwind.knife.application.dto.user;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
  private Long userId;       // 用户Id
  private String userName;   // 用户名称
  private Long phone;        // 用户电话
  private String address;    // 用户地址
  private String email;      // 用户邮箱
  private LocalDateTime createTime; // 用户创建时间
  private LocalDateTime updateTime; // 用户更新时间
}
