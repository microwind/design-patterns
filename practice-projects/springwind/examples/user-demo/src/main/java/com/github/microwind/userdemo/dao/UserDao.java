package com.github.microwind.userdemo.dao;

import com.github.microwind.springwind.annotation.Repository;
import com.github.microwind.springwind.annotation.Autowired;
import com.github.microwind.springwind.jdbc.JdbcTemplate;
import com.github.microwind.userdemo.model.User;
import java.sql.Timestamp;
import java.util.List;

/**
 * 用户数据访问层
 * 使用 SpringWind 框架的 JdbcTemplate 进行数据库操作
 */
@Repository
public class UserDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // -------------------- 表名与字段名常量 --------------------
    public static final String TABLE_USER = "users";

    public static final String FIELD_ID = "id";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_EMAIL = "email";
    public static final String FIELD_PHONE = "phone";
    public static final String FIELD_CREATED_TIME = "created_time";
    public static final String FIELD_UPDATED_TIME = "updated_time";

    public static final String FIELD_TOTAL = "total";

    // -------------------- CRUD 方法 --------------------

    /**
     * 创建用户
     */
    public int create(User user) {
        String sql = "INSERT INTO " + TABLE_USER + " (" +
                FIELD_NAME + ", " + FIELD_EMAIL + ", " + FIELD_PHONE + ", " +
                FIELD_CREATED_TIME + ", " + FIELD_UPDATED_TIME +
                ") VALUES (?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                new Timestamp(user.getCreatedTime()),
                new Timestamp(user.getUpdatedTime()));
    }

    /**
     * 根据 ID 查询用户
     */
    public User findById(Long id) {
        String sql = "SELECT " + FIELD_ID + ", " + FIELD_NAME + ", " + FIELD_EMAIL + ", " +
                FIELD_PHONE + ", " + FIELD_CREATED_TIME + ", " + FIELD_UPDATED_TIME +
                " FROM " + TABLE_USER + " WHERE " + FIELD_ID + " = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getLong(FIELD_ID));
            user.setName(rs.getString(FIELD_NAME));
            user.setEmail(rs.getString(FIELD_EMAIL));
            user.setPhone(rs.getString(FIELD_PHONE));

            Timestamp created = rs.getTimestamp(FIELD_CREATED_TIME);
            user.setCreatedTime(created != null ? created.getTime() : 0L);

            Timestamp updated = rs.getTimestamp(FIELD_UPDATED_TIME);
            user.setUpdatedTime(updated != null ? updated.getTime() : 0L);

            return user;
        }, id);
    }

    /**
     * 根据用户名查询用户
     */
    public User findByUsername(String name) {
        String sql = "SELECT " + FIELD_ID + ", " + FIELD_NAME + ", " + FIELD_EMAIL + ", " +
                FIELD_PHONE + ", " + FIELD_CREATED_TIME + ", " + FIELD_UPDATED_TIME +
                " FROM " + TABLE_USER + " WHERE " + FIELD_NAME + " = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getLong(FIELD_ID));
            user.setName(rs.getString(FIELD_NAME));
            user.setEmail(rs.getString(FIELD_EMAIL));
            user.setPhone(rs.getString(FIELD_PHONE));

            Timestamp created = rs.getTimestamp(FIELD_CREATED_TIME);
            user.setCreatedTime(created != null ? created.getTime() : 0L);

            Timestamp updated = rs.getTimestamp(FIELD_UPDATED_TIME);
            user.setUpdatedTime(updated != null ? updated.getTime() : 0L);

            return user;
        }, name);
    }

    /**
     * 查询所有用户
     */
    public List<User> findAll() {
        String sql = "SELECT " + FIELD_ID + ", " + FIELD_NAME + ", " + FIELD_EMAIL + ", " +
                FIELD_PHONE + ", " + FIELD_CREATED_TIME + ", " + FIELD_UPDATED_TIME +
                " FROM " + TABLE_USER;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getLong(FIELD_ID));
            user.setName(rs.getString(FIELD_NAME));
            user.setEmail(rs.getString(FIELD_EMAIL));
            user.setPhone(rs.getString(FIELD_PHONE));

            Timestamp created = rs.getTimestamp(FIELD_CREATED_TIME);
            user.setCreatedTime(created != null ? created.getTime() : 0L);

            Timestamp updated = rs.getTimestamp(FIELD_UPDATED_TIME);
            user.setUpdatedTime(updated != null ? updated.getTime() : 0L);

            return user;
        });
    }

    /**
     * 更新用户信息
     */
    public int update(User user) {
        String sql = "UPDATE " + TABLE_USER + " SET " +
                FIELD_NAME + " = ?, " +
                FIELD_EMAIL + " = ?, " +
                FIELD_PHONE + " = ?, " +
                FIELD_UPDATED_TIME + " = ? " +
                "WHERE " + FIELD_ID + " = ?";
        return jdbcTemplate.update(sql,
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                new Timestamp(System.currentTimeMillis()),
                user.getId());
    }

    /**
     * 删除用户
     */
    public int delete(Long id) {
        String sql = "DELETE FROM " + TABLE_USER + " WHERE " + FIELD_ID + " = ?";
        return jdbcTemplate.update(sql, id);
    }

    /**
     * 根据用户名删除用户
     */
    public int deleteByUsername(String name) {
        String sql = "DELETE FROM " + TABLE_USER + " WHERE " + FIELD_NAME + " = ?";
        return jdbcTemplate.update(sql, name);
    }

    /**
     * 获取用户总数
     */
    public Long count() {
        String sql = "SELECT COUNT(*) AS " + FIELD_TOTAL + " FROM " + TABLE_USER;
        Long count = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rs.getLong(FIELD_TOTAL));
        return count != null ? count : 0L;
    }

    /**
     * 分页查询用户
     * @param page 页码，从1开始
     * @param pageSize 每页大小
     * @return 用户列表
     */
    public List<User> findByPage(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        String sql = "SELECT " + FIELD_ID + ", " + FIELD_NAME + ", " + FIELD_EMAIL + ", " +
                FIELD_PHONE + ", " + FIELD_CREATED_TIME + ", " + FIELD_UPDATED_TIME +
                " FROM " + TABLE_USER + " LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getLong(FIELD_ID));
            user.setName(rs.getString(FIELD_NAME));
            user.setEmail(rs.getString(FIELD_EMAIL));
            user.setPhone(rs.getString(FIELD_PHONE));

            Timestamp created = rs.getTimestamp(FIELD_CREATED_TIME);
            user.setCreatedTime(created != null ? created.getTime() : 0L);

            Timestamp updated = rs.getTimestamp(FIELD_UPDATED_TIME);
            user.setUpdatedTime(updated != null ? updated.getTime() : 0L);

            return user;
        }, pageSize, offset);
    }
}
