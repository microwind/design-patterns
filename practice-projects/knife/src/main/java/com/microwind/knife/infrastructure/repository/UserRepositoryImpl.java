package com.microwind.knife.infrastructure.repository;

import com.microwind.knife.domain.repository.UserRepository;
import com.microwind.knife.domain.user.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 基础设施层 - 基于 JdbcTemplate 的用户仓储实现
 * 适用场景：需精细控制 SQL 或对性能要求较高的操作
 */
@Repository
@Primary
public class UserRepositoryImpl implements UserRepository {

    // 表名及列名常量，便于维护
    private static final String TABLE_USERS = "users";
    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_EMAIL = "email";
    private static final String COL_PHONE = "phone";
    private static final String COL_ADDRESS = "address";
    private static final String COL_CREATED_TIME = "created_time";
    private static final String COL_UPDATED_TIME = "updated_time";

    // Spring会自动查找对应数据库配置
    private final JdbcTemplate jdbcTemplate;

    // 显式构造器注入，使用@Qualifier指定userJdbcTemplate
    public UserRepositoryImpl(@Qualifier("userJdbcTemplate") JdbcTemplate jdbcTemplate) {
        System.out.println("initialize UserRepositoryImpl with userDataSource: " + jdbcTemplate.getDataSource());
        this.jdbcTemplate = jdbcTemplate;
    }

    private String camelToSnake(String prop) {
        return prop.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }

    /**
     * 构建 ORDER BY 子句
     */
    private String buildOrderBy(Sort sort) {
        if (sort == null || sort.isUnsorted()) {
            return "";
        }

        // 白名单字段（按数据库字段写）
        Set<String> allowed = Set.of(
                COL_ID,
                COL_NAME,
                COL_CREATED_TIME,
                COL_UPDATED_TIME,
                COL_EMAIL,
                COL_PHONE
        );

        String orderBy = sort.stream()
                .map(order -> {
                    // 支持驼峰 createdAt → created_at
                    String property = camelToSnake(order.getProperty());

                    // 白名单校验（避免 SQL 注入）
                    if (!allowed.contains(property)) {
                        return null;
                    }

                    String direction = order.getDirection().isAscending() ? "ASC" : "DESC";
                    return property + " " + direction;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.joining(", "));

        return orderBy.isEmpty() ? "" : "ORDER BY " + orderBy;
    }

    /**
     * 根据用户号查询用户（精确匹配）
     * 使用 readOnly 事务优化查询
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Integer userId) {
        String sql = String.format("SELECT * FROM %s WHERE %s = ?", TABLE_USERS, COL_ID);
        try {
            User user = jdbcTemplate.queryForObject(sql, userRowMapper(), userId);
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * 根据用户ID查询用户列表
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByName(String userName) {
        String sql = String.format("SELECT * FROM %s WHERE %s LIKE ?", TABLE_USERS, COL_NAME);
        try {
            User user = jdbcTemplate.queryForObject(sql, userRowMapper(), "%" + userName + "%");
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * 分页查询所有用户
     * 注意：COUNT(*) 在大数据量时可能较慢，可考虑分页优化策略
     */
    @Override
    @Transactional(readOnly = true)
    public Page<User> findAllUsers(Pageable pageable) {
        // 分页参数提取
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();
        int offset = pageNumber * pageSize;

        // 提取排序 SQL
        String orderBySql = buildOrderBy(pageable.getSort());

        // 数据查询 SQL
        String dataSql = String.format(
                "SELECT * FROM %s %s LIMIT ? OFFSET ?",
                TABLE_USERS, orderBySql
        );

        List<User> users = jdbcTemplate.query(dataSql, userRowMapper(), pageSize, offset);

        String countSql = String.format("SELECT COUNT(*) FROM %s", TABLE_USERS);
        Integer total = jdbcTemplate.queryForObject(countSql, Integer.class);

        return new PageImpl<>(users, pageable, total != null ? total : 0);
    }

    /**
     * 保存用户（新增或更新）
     * 注意：假设 userNo 由业务层保证唯一性
     */
    @Override
    @Transactional
    public User save(User user) {
        if (user.getId() == null) {
            insertUser(user);
        } else {
            updateUser(user);
        }
        return user;
    }

    private void insertUser(User user) {
        String sql = String.format(
                "INSERT INTO %s (%s, %s, %s) VALUES (?, ?, ?)",
                TABLE_USERS, COL_NAME, COL_PHONE, COL_EMAIL);

        jdbcTemplate.update(
                sql,
                user.getName(),
                user.getPhone(),
                user.getEmail()
        );

        // 获取自增主键
        Integer id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        user.setId(id);
    }

    private void updateUser(User user) {
        String sql = String.format(
                "UPDATE %s SET %s=?, %s=?, %s=? WHERE %s=?",
                TABLE_USERS, COL_NAME, COL_PHONE, COL_EMAIL, COL_ID
        );

        jdbcTemplate.update(
                sql,
                user.getName(),
                user.getPhone(),
                user.getEmail(),
                user.getId()
        );
    }

    /**
     * 删除用户（根据 userNo 存在性检查）
     */
    @Override
    @Transactional
    public void delete(User user) {
        if (user.getId() != null) {
            String sql = String.format(
                    "DELETE FROM %s WHERE %s = ?",
                    TABLE_USERS, COL_ID
            );
            jdbcTemplate.update(sql, user.getId());
        }
    }

    /**
     * 复用 RowMapper 实现（使用 Lambda 简化）
     */
    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getInt(COL_ID));
            user.setName(rs.getString(COL_NAME));
            user.setPhone(rs.getString(COL_PHONE));
            user.setEmail(rs.getString(COL_EMAIL));
            user.setAddress(rs.getString(COL_ADDRESS));
            user.setCreatedTime(rs.getObject(COL_CREATED_TIME, LocalDateTime.class));
            user.setUpdatedTime(rs.getObject(COL_UPDATED_TIME, LocalDateTime.class));
            return user;
        };
    }
}