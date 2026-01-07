package com.github.microwind.springboot4ddd.infrastructure.repository.user;

import com.github.microwind.springboot4ddd.domain.model.user.User;
import com.github.microwind.springboot4ddd.domain.repository.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * 用户仓储实现 - MySQL数据源
 *
 * @author jarry
 * @since 1.0.0
 */
@Slf4j
@Repository
public class UserRepositoryImpl implements UserRepository {

    // 直接使用JdbcTemplate实现数据操作
    private final JdbcTemplate jdbcTemplate;

    public UserRepositoryImpl(@Qualifier("userJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<User> USER_ROW_MAPPER = (rs, rowNum) -> User.builder()
            .id(rs.getLong("id"))
            .name(rs.getString("name"))
            .email(rs.getString("email"))
            .phone(rs.getString("phone"))
            .wechat(rs.getString("wechat"))
            .address(rs.getString("address"))
            .createdTime(rs.getTimestamp("created_time").toLocalDateTime())
            .updatedTime(rs.getTimestamp("updated_time").toLocalDateTime())
            .build();

    @Override
    @Transactional(transactionManager = "userTransactionManager")
    public User save(User user) {
        String sql = "INSERT INTO users (name, email, phone, wechat, address, created_time, updated_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getWechat());
            ps.setString(5, user.getAddress());
            ps.setTimestamp(6, Timestamp.valueOf(user.getCreatedTime() != null ? user.getCreatedTime() : java.time.LocalDateTime.now()));
            ps.setTimestamp(7, Timestamp.valueOf(user.getUpdatedTime() != null ? user.getUpdatedTime() : java.time.LocalDateTime.now()));
            return ps;
        }, keyHolder);

        user.setId(keyHolder.getKey().longValue());
        log.info("User saved with id: {}", user.getId());
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, USER_ROW_MAPPER, id);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByName(String name) {
        String sql = "SELECT * FROM users WHERE name = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, USER_ROW_MAPPER, name);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, USER_ROW_MAPPER, email);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users ORDER BY created_time DESC";
        return jdbcTemplate.query(sql, USER_ROW_MAPPER);
    }

    @Override
    @Transactional(transactionManager = "userTransactionManager")
    public User update(User user) {
        String sql = "UPDATE users SET name = ?, email = ?, phone = ?, wechat = ?, address = ?, updated_time = ? " +
                "WHERE id = ?";

        int updated = jdbcTemplate.update(sql,
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getWechat(),
                user.getAddress(),
                Timestamp.valueOf(java.time.LocalDateTime.now()),
                user.getId());

        if (updated == 0) {
            throw new IllegalArgumentException("User not found with id: " + user.getId());
        }

        log.info("User updated with id: {}", user.getId());
        return user;
    }

    @Override
    @Transactional(transactionManager = "userTransactionManager")
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
