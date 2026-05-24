package com.github.microwind.springboot4ddd.domain.client.user;

import java.util.Collection;
import java.util.Map;

/**
 * 用户上下文查询客户端（防腐层接口）。
 *
 * <p>订单上下文通过该接口跨上下文获取用户的最小化快照，
 * 不直接持有 user 聚合的仓储，避免上下文耦合。
 * 实现位于 infrastructure 层。
 *
 * @author jarry
 * @since 1.0.0
 */
public interface UserInfoQueryClient {

    /**
     * 批量获取用户简介。返回 Map 以便调用方按 userId 直接查找。
     * 不存在的 userId 在返回值中缺失，由调用方决定如何展示（如 "未知用户"）。
     */
    Map<Long, UserBriefInfo> findBriefs(Collection<Long> userIds);
}
