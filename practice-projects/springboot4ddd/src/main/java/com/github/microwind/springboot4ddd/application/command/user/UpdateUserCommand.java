package com.github.microwind.springboot4ddd.application.command.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 更新用户命令（application 层）
 *
 * <p>字段为 null 表示不修改对应属性。
 *
 * @author jarry
 * @since 1.0.0
 */
@Getter
@Builder
@AllArgsConstructor
public class UpdateUserCommand {

    private final String email;
    private final String phone;
    private final String wechat;
    private final String address;
}
