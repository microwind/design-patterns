package com.microwind.knife.infrastructure.repository;

import com.microwind.knife.domain.repository.AppAuthRepository;
import com.microwind.knife.domain.sign.ApiAuth;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 基础设施层 - 基于 JdbcTemplate 的订单仓储实现
 * 适用场景：需精细控制 SQL 或对性能要求较高的操作
 */
@Repository
@Primary
public class ApiAuthRepositoryImpl implements AppAuthRepository {

    // 表名及列名常量，便于维护
    private static final String TABLE_AUTH = "authorization";
    private static final String COL_AUTH_NO = "auth_no";
    private static final String COL_USER_ID = "user_id";
    private static final String COL_STATUS = "status";
    private static final String COL_AUTH_NAME = "auth_name";
    private static final String COL_AMOUNT = "amount";

    // 注入User数据源的JdbcTemplate
    private final JdbcTemplate jdbcTemplate;

    // 显式构造器注入，替代 @Resource 和 Lombok
    public ApiAuthRepositoryImpl(JdbcTemplate jdbcTemplate) {
        System.out.println("initialize UserRepositoryImpl" + jdbcTemplate.getDataSource());
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<ApiAuth> findByAppKey(String appKey) {
        return Optional.empty();
    }
}