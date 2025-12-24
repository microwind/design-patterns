package com.microwind.knife.infrastructure.repository;

import com.microwind.knife.domain.apiauth.ApiDynamicSaltLog;
import com.microwind.knife.domain.apiauth.ApiInfo;
import com.microwind.knife.domain.apiauth.ApiUsers;
import com.microwind.knife.domain.repository.SignRepository;
import com.microwind.knife.domain.sign.SignUserAuth;
import com.microwind.knife.domain.sign.SignUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 基础设施层 - 基于 JdbcTemplate 的 API 权限仓储实现
 * 适用场景：需精细控制 SQL 或对性能要求较高的操作
 * sign仓库操作是多个apiauth下的表
 */
@Slf4j
@Repository
@Primary
public class SignRepositoryImpl implements SignRepository {

    // 状态常量
    private static final short STATUS_ACTIVE = 1;
    private static final short STATUS_INACTIVE = 0;
    private static final short SALT_UNUSED = 0;
    private static final short SALT_USED = 1;

    // 注入 ApiAuth 数据源的 JdbcTemplate
    private final JdbcTemplate jdbcTemplate;

    // 显式构造器注入，使用 @Qualifier 指定 apiAuthJdbcTemplate
    public SignRepositoryImpl(@Qualifier("apiAuthJdbcTemplate") JdbcTemplate jdbcTemplate) {
        log.info("Initialize SignRepositoryImpl with apiAuthDataSource: {}", jdbcTemplate.getDataSource());
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 根据 appCode 查询 API 权限信息
     * 从 api_users 表获取用户信息，从 api_auth 表获取允许/禁止访问的路径列表
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<SignUserAuth> findByAppCode(String appCode) {
        try {
            // 1. 查询用户基本信息
            SignUserInfo userInfo = findUserInfoByAppCode(appCode);
            if (userInfo == null) {
                return Optional.empty();
            }

            // 2. 查询允许访问的接口路径列表
            List<String> permitPaths = findPermitPaths(appCode);

            // 3. 查询禁止访问的接口路径列表
            List<String> forbiddenPaths = findForbiddenPaths(appCode);

            // 4. 构建 SignUserAuth 对象
            SignUserAuth signUserAuth = new SignUserAuth(
                    userInfo.appCode(),
                    userInfo.secretKey(),
                    permitPaths != null ? permitPaths : Collections.emptyList(),
                    forbiddenPaths != null ? forbiddenPaths : Collections.emptyList()
            );

            return Optional.of(signUserAuth);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * 根据 appCode 查询用户基本信息
     */
    private SignUserInfo findUserInfoByAppCode(String appCode) {
        String sql = """
                SELECT app_code, secret_key, app_name, status
                FROM api_users
                WHERE app_code = ?
                  AND status = ?
                  AND (expire_time IS NULL OR expire_time > ?)
                """;

        LocalDateTime now = LocalDateTime.now();
        return jdbcTemplate.queryForObject(sql, userInfoRowMapper(), appCode, STATUS_ACTIVE, now);
    }

    /**
     * 查询允许访问的接口路径列表
     */
    private List<String> findPermitPaths(String appCode) {
        String sql = """
                SELECT api_path
                FROM api_auth
                WHERE app_code = ?
                  AND status = ?
                  AND (expire_time IS NULL OR expire_time > ?)
                """;

        LocalDateTime now = LocalDateTime.now();
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("api_path"), appCode, STATUS_ACTIVE, now);
    }

    /**
     * 查询禁止访问的接口路径列表
     */
    private List<String> findForbiddenPaths(String appCode) {
        String sql = """
                SELECT api_path
                FROM api_auth
                WHERE app_code = ?
                  AND status = ?
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("api_path"), appCode, STATUS_INACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ApiInfo> findApiInfoByPath(String apiPath) {
        try {
            String sql = """
                    SELECT id, api_path, api_name, api_type, fixed_salt,
                           rate_limit, status, description, created_at, updated_at
                    FROM api_info
                    WHERE api_path = ?
                    """;

            ApiInfo apiInfo = jdbcTemplate.queryForObject(sql, apiInfoRowMapper(), apiPath);
            return Optional.ofNullable(apiInfo);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ApiUsers> findApiUserByAppCode(String appCode) {
        try {
            LocalDateTime now = LocalDateTime.now();
            String sql = """
                    SELECT id, app_code, app_name, secret_key, status,
                           daily_limit, expire_time, created_at, updated_at
                    FROM api_users
                    WHERE app_code = ?
                      AND status = ?
                      AND (expire_time IS NULL OR expire_time > ?)
                    """;

            ApiUsers apiUsers = jdbcTemplate.queryForObject(sql, apiUsersRowMapper(), appCode, STATUS_ACTIVE, now);
            return Optional.ofNullable(apiUsers);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkAuth(String appCode, String apiPath) {
        try {
            String sql = """
                    SELECT COUNT(*)
                    FROM api_auth
                    WHERE app_code = ?
                      AND api_path = ?
                      AND status = ?
                      AND (expire_time IS NULL OR expire_time > ?)
                    """;

            LocalDateTime now = LocalDateTime.now();
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, appCode, apiPath, STATUS_ACTIVE, now);
            return count != null && count > 0;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    @Override
    @Transactional(transactionManager = "apiAuthTransactionManager")
    public ApiDynamicSaltLog saveDynamicSaltLog(String appCode, Long apiId, String apiPath,
                                                String dynamicSalt, Long saltTimestamp,
                                                LocalDateTime expireTime) {
        String sql = """
                INSERT INTO api_dynamic_salt_log (api_id, app_code, api_path, dynamic_salt, salt_timestamp, expire_time, used)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                RETURNING id
                """;

        Long id = jdbcTemplate.queryForObject(sql, Long.class,
                apiId, appCode, apiPath, dynamicSalt, saltTimestamp, expireTime, SALT_UNUSED);

        return ApiDynamicSaltLog.builder()
                .id(id)
                .apiId(apiId)
                .appCode(appCode)
                .apiPath(apiPath)
                .dynamicSalt(dynamicSalt)
                .saltTimestamp(saltTimestamp)
                .expireTime(expireTime)
                .used(SALT_UNUSED)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional(transactionManager = "apiAuthTransactionManager")
    public boolean validateAndConsumeSalt(String appCode, String apiPath, String dynamicSalt) {
        try {
            // 查询有效的盐值记录（未使用且未过期）
            String selectSql = """
                    SELECT id
                    FROM api_dynamic_salt_log
                    WHERE app_code = ?
                      AND api_path = ?
                      AND dynamic_salt = ?
                      AND used = ?
                      AND (expire_time IS NULL OR expire_time > ?)
                    """;

            LocalDateTime now = LocalDateTime.now();
            Long id = jdbcTemplate.queryForObject(selectSql, Long.class, appCode, apiPath, dynamicSalt, SALT_UNUSED, now);

            if (id != null) {
                // 标记为已使用
                String updateSql = """
                        UPDATE api_dynamic_salt_log
                        SET used = ?, updated_at = ?
                        WHERE id = ?
                        """;
                jdbcTemplate.update(updateSql, SALT_USED, LocalDateTime.now(), id);
                return true;
            }
            return false;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    @Override
    @Transactional(transactionManager = "apiAuthTransactionManager")
    public int cleanExpiredSalts() {
        String sql = """
                DELETE FROM api_dynamic_salt_log
                WHERE expire_time IS NOT NULL
                  AND expire_time < ?
                """;

        LocalDateTime now = LocalDateTime.now();
        return jdbcTemplate.update(sql, now);
    }

    /**
     * SignUserInfo RowMapper
     */
    private RowMapper<SignUserInfo> userInfoRowMapper() {
        return (rs, rowNum) -> new SignUserInfo(
                rs.getString("app_code"),
                rs.getString("secret_key"),
                rs.getString("app_name"),
                rs.getShort("status")
        );
    }

    /**
     * ApiInfo RowMapper
     */
    private RowMapper<ApiInfo> apiInfoRowMapper() {
        return (rs, rowNum) -> ApiInfo.builder()
                .id(rs.getLong("id"))
                .apiPath(rs.getString("api_path"))
                .apiName(rs.getString("api_name"))
                .apiType(rs.getShort("api_type"))
                .fixedSalt(rs.getString("fixed_salt"))
                .rateLimit(rs.getObject("rate_limit", Integer.class))
                .status(rs.getShort("status"))
                .description(rs.getString("description"))
                .createdAt(rs.getObject("created_at", LocalDateTime.class))
                .updatedAt(rs.getObject("updated_at", LocalDateTime.class))
                .build();
    }

    /**
     * ApiUsers RowMapper
     */
    private RowMapper<ApiUsers> apiUsersRowMapper() {
        return (rs, rowNum) -> ApiUsers.builder()
                .id(rs.getLong("id"))
                .appCode(rs.getString("app_code"))
                .appName(rs.getString("app_name"))
                .secretKey(rs.getString("secret_key"))
                .status(rs.getShort("status"))
                .dailyLimit(rs.getObject("daily_limit", Integer.class))
                .expireTime(rs.getObject("expire_time", LocalDateTime.class))
                .createdAt(rs.getObject("created_at", LocalDateTime.class))
                .updatedAt(rs.getObject("updated_at", LocalDateTime.class))
                .build();
    }
}