package com.github.microwind.springboot4ddd.infrastructure.repository.user;

import com.github.microwind.springboot4ddd.domain.model.user.User;
import com.github.microwind.springboot4ddd.domain.page.PageRequest;
import com.github.microwind.springboot4ddd.domain.page.PageResult;
import com.github.microwind.springboot4ddd.domain.page.SortOrder;
import com.github.microwind.springboot4ddd.domain.repository.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

    private final JdbcTemplate jdbcTemplate;

    public UserRepositoryImpl(@Qualifier("userJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<UserDO> USER_DO_ROW_MAPPER = (rs, rowNum) -> UserDO.builder()
            .id(rs.getLong("id"))
            .name(rs.getString("name"))
            .email(rs.getString("email"))
            .phone(rs.getString("phone"))
            .wechat(getStringOrNull(rs, "wechat"))
            .address(getStringOrNull(rs, "address"))
            .createdTime(rs.getTimestamp("created_time").toLocalDateTime())
            .updatedTime(rs.getTimestamp("updated_time").toLocalDateTime())
            .build();

    /** 列不存在或为 null 时返回 null，避免 SQLException: Column 'xxx' not found */
    private static String getStringOrNull(ResultSet rs, String column) throws SQLException {
        try {
            rs.findColumn(column);
        } catch (SQLException notFound) {
            return null;
        }
        return rs.getString(column);
    }

    @Override
    public User save(User user) {
        UserDO userDO = UserConverter.toDO(user);
        String sql = "INSERT INTO users (name, email, phone, address, created_time, updated_time) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime createdTime = userDO.getCreatedTime() != null ? userDO.getCreatedTime() : now;
        LocalDateTime updatedTime = userDO.getUpdatedTime() != null ? userDO.getUpdatedTime() : now;

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, userDO.getName());
            ps.setString(2, userDO.getEmail());
            ps.setString(3, userDO.getPhone());
            ps.setString(4, userDO.getAddress());
            ps.setTimestamp(5, Timestamp.valueOf(createdTime));
            ps.setTimestamp(6, Timestamp.valueOf(updatedTime));
            return ps;
        }, keyHolder);

        user.markPersisted(keyHolder.getKey().longValue());
        log.info("User saved with id: {}", user.getId());
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            UserDO userDO = jdbcTemplate.queryForObject(sql, USER_DO_ROW_MAPPER, id);
            return Optional.ofNullable(UserConverter.toModel(userDO));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByName(String name) {
        String sql = "SELECT * FROM users WHERE name = ?";
        try {
            UserDO userDO = jdbcTemplate.queryForObject(sql, USER_DO_ROW_MAPPER, name);
            return Optional.ofNullable(UserConverter.toModel(userDO));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try {
            UserDO userDO = jdbcTemplate.queryForObject(sql, USER_DO_ROW_MAPPER, email);
            return Optional.ofNullable(UserConverter.toModel(userDO));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users ORDER BY created_time DESC";
        return UserConverter.toModelList(jdbcTemplate.query(sql, USER_DO_ROW_MAPPER));
    }

    @Override
    public PageResult<User> findAll(PageRequest pageRequest) {
        String countSql = "SELECT COUNT(*) FROM users";
        Integer total = jdbcTemplate.queryForObject(countSql, Integer.class);
        long totalElements = total != null ? total : 0;

        String orderBy = buildOrderByClause(pageRequest.getSorts());
        String sql = "SELECT * FROM users" + orderBy + " LIMIT ? OFFSET ?";

        List<UserDO> records = jdbcTemplate.query(
                sql, USER_DO_ROW_MAPPER, pageRequest.getPageSize(), pageRequest.getOffset());

        return new PageResult<>(
                UserConverter.toModelList(records),
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

        int updated = jdbcTemplate.update(sql,
                userDO.getName(),
                userDO.getEmail(),
                userDO.getPhone(),
                userDO.getAddress(),
                Timestamp.valueOf(LocalDateTime.now()),
                userDO.getId());

        if (updated == 0) {
            throw new IllegalArgumentException("User not found with id: " + userDO.getId());
        }

        log.info("User updated with id: {}", user.getId());
        return user;
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        int deleted = jdbcTemplate.update(sql, id);
        if (deleted == 0) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        log.info("User deleted with id: {}", id);
    }

    @Override
    public boolean existsByName(String name) {
        String sql = "SELECT COUNT(*) FROM users WHERE name = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, name);
        return count != null && count > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }
}
