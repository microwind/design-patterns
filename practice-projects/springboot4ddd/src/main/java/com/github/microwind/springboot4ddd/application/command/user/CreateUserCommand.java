package com.github.microwind.springboot4ddd.application.command.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 创建用户命令（application 层）
 *
 * @author jarry
 * @since 1.0.0
 */
@Getter
@Builder
@AllArgsConstructor
public class CreateUserCommand {

    private final String name;
    private final String email;
    private final String phone;
    private final String wechat;
    private final String address;
}
