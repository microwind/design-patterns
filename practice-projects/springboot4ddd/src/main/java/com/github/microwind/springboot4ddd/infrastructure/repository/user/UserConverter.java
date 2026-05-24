package com.github.microwind.springboot4ddd.infrastructure.repository.user;

import com.github.microwind.springboot4ddd.domain.model.user.User;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户领域模型 ↔ 数据对象 显式转换器
 *
 * <p>放在 infrastructure 层，避免领域模型反向依赖持久化结构。
 * 所有字段映射在编译期可见，字段调整时编译器会立刻报错。
 *
 * @author jarry
 * @since 1.0.0
 */
public final class UserConverter {

    private UserConverter() {
    }

    public static UserDO toDO(User user) {
        if (user == null) {
            return null;
        }
        return UserDO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .wechat(user.getWechat())
                .address(user.getAddress())
                .createdTime(user.getCreatedTime())
                .updatedTime(user.getUpdatedTime())
                .build();
    }

    public static User toModel(UserDO userDO) {
        if (userDO == null) {
            return null;
        }
        return User.restore(
                userDO.getId(),
                userDO.getName(),
                userDO.getEmail(),
                userDO.getPhone(),
                userDO.getWechat(),
                userDO.getAddress(),
                userDO.getCreatedTime(),
                userDO.getUpdatedTime()
        );
    }

    public static List<User> toModelList(List<UserDO> dos) {
        if (dos == null) {
            return null;
        }
        return dos.stream()
                .map(UserConverter::toModel)
                .collect(Collectors.toList());
    }
}
