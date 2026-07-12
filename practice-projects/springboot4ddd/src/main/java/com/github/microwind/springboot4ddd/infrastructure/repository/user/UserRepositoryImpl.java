package com.github.microwind.springboot4ddd.infrastructure.repository.user;

import com.github.microwind.springboot4ddd.domain.model.user.User;
import com.github.microwind.springboot4ddd.domain.page.PageRequest;
import com.github.microwind.springboot4ddd.domain.page.PageResult;
import com.github.microwind.springboot4ddd.domain.page.SortOrder;
import com.github.microwind.springboot4ddd.domain.repository.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户仓储实现 - MySQL 数据源
 *
 * <p>通过 {@link UserDO} + {@link UserConverter} 显式分离持久化与领域模型，
 * 与 {@code OrderJdbcRepositoryImpl} 的处理方式对称。
 *
 * @author jarry
 * @since 1.0.0
 */
@Slf4j
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final JdbcClient jdbcClient;

    public UserRepositoryImpl(@Qualifier("userJdbcClient") JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    private static UserDO mapRowToUserDO(java.sql.ResultSet rs) throws java.sql.SQLException {
        return UserDO.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .email(rs.getString("email"))
                .phone(rs.getString("phone"))
                .wechat(getStringOrNull(rs, "wechat"))
                .address(getStringOrNull(rs, "address"))
                .createdTime(rs.getTimestamp("created_time").toLocalDateTime())
                .updatedTime(rs.getTimestamp("updated_time").toLocalDateTime())
                .build();
    }

    /** 列不存在或为 null 时返回 null，避免 SQLException: Column 'xxx' not found */
    private static String getStringOrNull(java.sql.ResultSet rs, String column) throws java.sql.SQLException {
        try {
            rs.findColumn(column);
        } catch (java.sql.SQLException notFound) {
            return null;
        }
        return rs.getString(column);
    }

    @Override
    public User save(User user) {
        UserDO userDO = UserConverter.toDO(user);
        String sql = "INSERT INTO users (name, email, phone, address, created_time, updated_time) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime createdTime = userDO.getCreatedTime() != null ? userDO.getCreatedTime() : now;
        LocalDateTime updatedTime = userDO.getUpdatedTime() != null ? userDO.getUpdatedTime() : now;

        Long generatedId = jdbcClient.sql(sql)
                .params(userDO.getName(), userDO.getEmail(), userDO.getPhone(), 
                        userDO.getAddress(), Timestamp.valueOf(createdTime), Timestamp.valueOf(updatedTime))
                .query(Long.class)
                .single();

        user.markPersisted(generatedId);
        log.info("User saved with id: {}", user.getId());
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        return jdbcClient.sql(sql)
                .param(id)
                .query((rs, rowNum) -> UserConverter.toModel(UserRepositoryImpl.mapRowToUserDO(rs)))
                .optional();
    }

    @Override
    public Optional<User> findByName(String name) {
        String sql = "SELECT * FROM users WHERE name = ?";
        return jdbcClient.sql(sql)
                .param(name)
                .query((rs, rowNum) -> UserConverter.toModel(UserRepositoryImpl.mapRowToUserDO(rs)))
                .optional();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        return jdbcClient.sql(sql)
                .param(email)
                .query((rs, rowNum) -> UserConverter.toModel(UserRepositoryImpl.mapRowToUserDO(rs)))
                .optional();
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users ORDER BY created_time DESC";
        return jdbcClient.sql(sql)
                .query((rs, rowNum) -> UserConverter.toModel(UserRepositoryImpl.mapRowToUserDO(rs)))
                .list();
    }

    @Override
    public PageResult<User> findAll(PageRequest pageRequest) {
        String countSql = "SELECT COUNT(*) FROM users";
        Integer total = jdbcClient.sql(countSql)
                .query(Integer.class)
                .single();
        long totalElements = total != null ? total : 0;

        String orderBy = buildOrderByClause(pageRequest.getSorts());
        String sql = "SELECT * FROM users" + orderBy + " LIMIT ? OFFSET ?";

        List<User> records = jdbcClient.sql(sql)
                .params(pageRequest.getPageSize(), pageRequest.getOffset())
                .query((rs, rowNum) -> UserConverter.toModel(UserRepositoryImpl.mapRowToUserDO(rs)))
                .list();

        return new PageResult<>(
                records,
                totalElements,
                pageRequest.getPageNumber(),
                pageRequest.getPageSize()
        );
    }

    /**
     * 根据领域 SortOrder 列表构建 ORDER BY 子句；为空则按 created_time 倒序。
     */
    private String buildOrderByClause(List<SortOrder> sorts) {
        if (sorts == null || sorts.isEmpty()) {
            return " ORDER BY created_time DESC";
        }

        StringBuilder sb = new StringBuilder(" ORDER BY ");
        for (int i = 0; i < sorts.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            SortOrder order = sorts.get(i);
            sb.append(order.getProperty()).append(' ').append(order.getDirection().name());
        }
        return sb.toString();
    }

    @Override
    public User update(User user) {
        UserDO userDO = UserConverter.toDO(user);
        String sql = "UPDATE users SET name = ?, email = ?, phone = ?, address = ?, updated_time = ? " +
                "WHERE id = ?";

        int updated = jdbcClient.sql(sql)
                .params(userDO.getName(), userDO.getEmail(), userDO.getPhone(), 
                        userDO.getAddress(), Timestamp.valueOf(LocalDateTime.now()), userDO.getId())
                .update();

        if (updated == 0) {
            throw new IllegalArgumentException("User not found with id: " + userDO.getId());
        }

        log.info("User updated with id: {}", user.getId());
        return user;
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        int deleted = jdbcClient.sql(sql)
                .param(id)
                .update();
        if (deleted == 0) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        log.info("User deleted with id: {}", id);
    }

    @Override
    public boolean existsByName(String name) {
        String sql = "SELECT COUNT(*) FROM users WHERE name = ?";
        Integer count = jdbcClient.sql(sql)
                .param(name)
                .query(Integer.class)
                .single();
        return count != null && count > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        Integer count = jdbcClient.sql(sql)
                .param(email)
                .query(Integer.class)
                .single();
        return count != null && count > 0;
    }
}
