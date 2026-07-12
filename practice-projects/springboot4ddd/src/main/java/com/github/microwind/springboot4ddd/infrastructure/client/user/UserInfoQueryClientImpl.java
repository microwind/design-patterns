package com.github.microwind.springboot4ddd.infrastructure.client.user;

import com.github.microwind.springboot4ddd.domain.client.user.UserBriefInfo;
import com.github.microwind.springboot4ddd.domain.client.user.UserInfoQueryClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * 用户信息查询客户端实现（防腐层适配器）。
 *
 * <p>直接访问 user 上下文的 users 表，但只读取 name / phone 两个字段，
 * 避免与 UserRepository 共享更宽的查询能力。
 *
 * <p>使用 IN 批量查询，一次性拉回所有需要的用户信息，消除 N+1。
 *
 * @author jarry
 * @since 1.0.0
 */
@Component
public class UserInfoQueryClientImpl implements UserInfoQueryClient {

    private final JdbcClient jdbcClient;

    public UserInfoQueryClientImpl(@Qualifier("userJdbcClient") JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Map<Long, UserBriefInfo> findBriefs(Collection<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        Set<Long> distinct = new LinkedHashSet<>(userIds);
        Map<Long, UserBriefInfo> result = new HashMap<>(distinct.size());

        // JdbcClient doesn't support IN clause directly with collection, need to use placeholder expansion
        String placeholders = String.join(",", Collections.nCopies(distinct.size(), "?"));
        String sql = "SELECT id, name, phone FROM users WHERE id IN (" + placeholders + ")";

        jdbcClient.sql(sql)
                .params((Object[]) distinct.toArray(new Long[0]))
                .query(rs -> {
                    long id = rs.getLong("id");
                    result.put(id, new UserBriefInfo(id, rs.getString("name"), rs.getString("phone")));
                });

        return result;
    }
}
