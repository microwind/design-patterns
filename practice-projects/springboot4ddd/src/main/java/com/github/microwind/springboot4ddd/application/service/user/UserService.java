package com.github.microwind.springboot4ddd.application.service.user;

import com.github.microwind.springboot4ddd.application.command.user.CreateUserCommand;
import com.github.microwind.springboot4ddd.application.command.user.UpdateUserCommand;
import com.github.microwind.springboot4ddd.application.dto.user.UserDTO;
import com.github.microwind.springboot4ddd.application.port.CachePolicy;
import com.github.microwind.springboot4ddd.application.port.CacheService;
import com.github.microwind.springboot4ddd.domain.exception.EntityNotFoundException;
import com.github.microwind.springboot4ddd.domain.model.user.User;
import com.github.microwind.springboot4ddd.domain.page.PageRequest;
import com.github.microwind.springboot4ddd.domain.page.PageResult;
import com.github.microwind.springboot4ddd.domain.repository.user.UserRepository;
import com.github.microwind.springboot4ddd.domain.service.user.UserUniquenessChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户应用服务
 *
 * <p>仅负责用例编排与事务管理，业务规则下沉到聚合根 {@link User} 与领域服务
 * {@link UserUniquenessChecker}。输入用 application.command 包下的 Command，
 * 输出统一为 {@link UserDTO}，不引用 interfaces 层。
 *
 * @author jarry
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "userTransactionManager")
public class UserService {

    private final UserRepository userRepository;
    private final UserUniquenessChecker userUniquenessChecker;
    private final CacheService cacheService;

    public UserDTO createUser(CreateUserCommand command) {
        User user = User.register(
                userUniquenessChecker,
                command.getName(),
                command.getEmail(),
                command.getPhone(),
                command.getWechat(),
                command.getAddress()
        );
        User savedUser = userRepository.save(user);
        log.info("User created: id={}, name={}", savedUser.getId(), savedUser.getName());
        return toDTO(savedUser);
    }

    public UserDTO getUserById(Long id) {
        return cacheService.getOrSet(
                CachePolicy.USER_KEY_PREFIX + id,
                CachePolicy.USER_TTL,
                () -> {
                    User user = userRepository.findById(id)
                            .orElseThrow(() -> new EntityNotFoundException("User", "id", id));
                    return toDTO(user);
                }
        );
    }

    public UserDTO getUserByName(String name) {
        return cacheService.getOrSet(
                CachePolicy.USER_KEY_PREFIX + "name:" + name,
                CachePolicy.USER_TTL,
                () -> {
                    User user = userRepository.findByName(name)
                            .orElseThrow(() -> new EntityNotFoundException("User", "name", name));
                    return toDTO(user);
                }
        );
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public PageResult<UserDTO> getAllUsers(PageRequest pageRequest) {
        return userRepository.findAll(pageRequest).map(this::toDTO);
    }

    public UserDTO updateUser(Long id, UpdateUserCommand command) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("用户", "id", id));

        if (command.getEmail() != null) {
            user.changeEmail(userUniquenessChecker, command.getEmail());
        }
        if (command.getPhone() != null) {
            user.changePhone(command.getPhone());
        }
        if (command.getWechat() != null) {
            user.changeWechat(command.getWechat());
        }
        if (command.getAddress() != null) {
            user.changeAddress(command.getAddress());
        }

        User updatedUser = userRepository.update(user);
        log.info("User updated: id={}, name={}", updatedUser.getId(), updatedUser.getName());

        cacheService.delete(CachePolicy.USER_KEY_PREFIX + updatedUser.getId());
        return toDTO(updatedUser);
    }

    public void deleteUser(Long id) {
        if (!userRepository.findById(id).isPresent()) {
            throw new EntityNotFoundException("用户", "id", id);
        }
        userRepository.deleteById(id);
        log.info("User deleted: id={}", id);
        cacheService.delete(CachePolicy.USER_KEY_PREFIX + id);
    }

    private UserDTO toDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .wechat(user.getWechat())
                .address(user.getAddress())
                .createdAt(user.getCreatedTime())
                .updatedAt(user.getUpdatedTime())
                .build();
    }
}
