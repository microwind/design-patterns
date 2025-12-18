package com.microwind.knife.infrastructure.repository;

import com.microwind.knife.domain.repository.ApiAuthRepository;
import com.microwind.knife.domain.sign.ApiAuth;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 基础设施层 - 基于 JdbcTemplate 的 API 权限仓储实现
 * 适用场景：需精细控制 SQL 或对性能要求较高的操作
 */
@Repository
@Primary
public class ApiAuthRepositoryImpl implements ApiAuthRepository {

    // 表名及列名常量，便于维护
    private static final String TABLE_API_USERS = "api_users";
    private static final String TABLE_API_AUTH = "api_auth";
    
    // api_users 表字段
    private static final String COL_APP_CODE = "app_code";
    private static final String COL_SECRET_KEY = "secret_key";
    private static final String COL_STATUS = "status";
    private static final String COL_EXPIRE_TIME = "expire_time";
    
    // api_auth 表字段
    private static final String COL_API_PATH = "api_path";

    // 注入 ApiAuth 数据源的 JdbcTemplate
    private final JdbcTemplate jdbcTemplate;

    // 显式构造器注入，使用 @Qualifier 指定 apiAuthJdbcTemplate
    public ApiAuthRepositoryImpl(@Qualifier("apiAuthJdbcTemplate") JdbcTemplate jdbcTemplate) {
        System.out.println("initialize ApiAuthRepositoryImpl with apiAuthDataSource: " + jdbcTemplate.getDataSource());
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 根据 appCode 查询 API 权限信息
     * 从 api_users 表获取 appSecret，从 api_auth 表获取允许访问的路径列表
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<ApiAuth> findByAppCode(String appCode) {
        try {
            // 1. 从 api_users 表查询用户信息和密钥
            String userSql = String.format(
                "SELECT %s, %s FROM %s WHERE %s = ? AND %s = 1 AND (%s IS NULL OR %s > ?)",
                COL_APP_CODE, COL_SECRET_KEY, TABLE_API_USERS, COL_APP_CODE, COL_STATUS, COL_EXPIRE_TIME, COL_EXPIRE_TIME
            );
            
            LocalDateTime now = LocalDateTime.now();
            UserInfo userInfo = jdbcTemplate.queryForObject(
                userSql,
                (rs, rowNum) -> new UserInfo(
                    rs.getString(COL_APP_CODE),
                    rs.getString(COL_SECRET_KEY)
                ),
                appCode, now
            );
            
            if (userInfo == null) {
                return Optional.empty();
            }
            
            // 2. 从 api_auth 表查询允许访问的接口路径列表（status=1表示允许）
            String permitSql = String.format(
                "SELECT %s FROM %s WHERE %s = ? AND %s = 1 AND (%s IS NULL OR %s > ?)",
                COL_API_PATH, TABLE_API_AUTH, COL_APP_CODE, COL_STATUS, COL_EXPIRE_TIME, COL_EXPIRE_TIME
            );

            List<String> permitPaths = jdbcTemplate.query(
                permitSql,
                (rs, rowNum) -> rs.getString(COL_API_PATH),
                appCode, now
            );

            // 3. 从 api_auth 表查询禁止访问的接口路径列表（status=0表示禁止）
            String forbiddenSql = String.format(
                "SELECT %s FROM %s WHERE %s = ? AND %s = 0",
                COL_API_PATH, TABLE_API_AUTH, COL_APP_CODE, COL_STATUS
            );

            List<String> forbiddenPaths = jdbcTemplate.query(
                forbiddenSql,
                (rs, rowNum) -> rs.getString(COL_API_PATH),
                appCode
            );

            // 4. 构建 ApiAuth 对象
            ApiAuth apiAuth = new ApiAuth(
                userInfo.appCode,
                userInfo.secretKey,
                permitPaths != null ? permitPaths : Collections.emptyList(),
                forbiddenPaths != null ? forbiddenPaths : Collections.emptyList()
            );
            
            return Optional.of(apiAuth);
            
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
    
    /**
     * 内部类：用于存储用户信息
     */
    private static class UserInfo {
        final String appCode;
        final String secretKey;
        
        UserInfo(String appCode, String secretKey) {
            this.appCode = appCode;
            this.secretKey = secretKey;
        }
    }
}