package com.microwind.knife.domain.repository;

import com.microwind.knife.domain.apiauth.ApiDynamicSaltLog;
import com.microwind.knife.domain.apiauth.ApiInfo;
import com.microwind.knife.domain.apiauth.ApiUsers;
import com.microwind.knife.domain.sign.SignUserAuth;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * API 签名认证仓储接口
 * <p>
 * 负责 API 签名认证相关的数据访问操作，包括：
 * <ul>
 *   <li>用户信息查询</li>
 *   <li>API 信息查询</li>
 *   <li>权限验证</li>
 *   <li>动态盐值管理</li>
 * </ul>
 * <p>
 * 实现方式：支持 JdbcTemplate 和 JPA 两种数据访问实现
 * <p>
 * 注意事项：
 * <ul>
 *   <li>所有查询用户/权限的方法都会自动过滤状态（status=1）和过期时间</li>
 *   <li>动态盐值相关操作需要在事务中执行</li>
 * </ul>
 *
 * @see com.microwind.knife.infrastructure.repository.SignRepositoryImpl
 */
public interface SignRepository {

    // ========================================
    // 用户信息查询
    // ========================================

    /**
     * 根据 appCode 查询用户完整的权限信息
     * <p>
     * 包括用户基本信息、允许访问的路径列表、禁止访问的路径列表
     * <p>
     * 查询条件：
     * <ul>
     *   <li>状态为启用（status = 1）</li>
     *   <li>未过期或永久有效（expire_time IS NULL OR expire_time > now）</li>
     * </ul>
     *
     * @param appCode 应用编码，不能为 null
     * @return 用户权限信息，若用户不存在或已失效则返回 empty
     */
    Optional<SignUserAuth> findByAppCode(String appCode);

    /**
     * 根据 appCode 查询 API 用户基本信息
     * <p>
     * 查询条件：
     * <ul>
     *   <li>状态为启用（status = 1）</li>
     *   <li>未过期或永久有效（expire_time IS NULL OR expire_time > now）</li>
     * </ul>
     *
     * @param appCode 应用编码，不能为 null
     * @return API 用户信息，若用户不存在或已失效则返回 empty
     */
    Optional<ApiUsers> findApiUserByAppCode(String appCode);

    // ========================================
    // API 信息查询
    // ========================================

    /**
     * 根据接口路径查询 API 信息
     * <p>
     * 包括接口类型、固定盐值、限流配置等信息
     * <p>
     * 注意：此方法不过滤状态，返回所有匹配路径的 API 信息
     *
     * @param apiPath 接口路径，不能为 null，如：/api/user/login
     * @return API 信息，若接口不存在则返回 empty
     */
    Optional<ApiInfo> findApiInfoByPath(String apiPath);

    // ========================================
    // 权限验证
    // ========================================

    /**
     * 检查应用是否有权限访问指定接口
     * <p>
     * 查询条件：
     * <ul>
     *   <li>状态为启用（status = 1）</li>
     *   <li>未过期或永久有效（expire_time IS NULL OR expire_time > now）</li>
     * </ul>
     *
     * @param appCode 应用编码，不能为 null
     * @param apiPath 接口路径，不能为 null
     * @return true-有权限，false-无权限
     */
    boolean checkAuth(String appCode, String apiPath);

    // ========================================
    // 动态盐值管理
    // ========================================

    /**
     * 保存动态盐值日志
     * <p>
     * 用于记录生成的动态盐值，供后续验证使用
     * <p>
     * 注意：此方法需要在事务中执行
     *
     * @param appCode       应用编码，不能为 null
     * @param apiId         接口 ID，关联 api_info 表
     * @param apiPath       接口路径，不能为 null
     * @param dynamicSalt   动态盐值，不能为 null
     * @param saltTimestamp 盐值生成时间戳（毫秒）
     * @param expireTime    过期时间，null 表示永久有效
     * @return 保存的盐值日志对象，包含生成的 ID
     */
    ApiDynamicSaltLog saveDynamicSaltLog(String appCode, Long apiId, String apiPath,
                                         String dynamicSalt, Long saltTimestamp,
                                         LocalDateTime expireTime);

    /**
     * 验证并消费动态盐值（一次性使用）
     * <p>
     * 验证流程：
     * <ol>
     *   <li>查询数据库中是否存在匹配的盐值记录</li>
     *   <li>检查盐值是否已使用（used = 0）</li>
     *   <li>检查盐值是否已过期（expire_time IS NULL OR expire_time > now）</li>
     *   <li>若验证通过，标记为已使用（used = 1）</li>
     * </ol>
     * <p>
     * 注意：此方法需要在事务中执行，确保原子性
     *
     * @param appCode     应用编码，不能为 null
     * @param apiPath     接口路径，不能为 null
     * @param dynamicSalt 动态盐值，不能为 null
     * @return true-校验通过并成功消费，false-校验失败（盐值不存在、已使用或已过期）
     */
    boolean validateAndConsumeSalt(String appCode, String apiPath, String dynamicSalt);

    /**
     * 清理过期的盐值记录
     * <p>
     * 定期清理已过期的动态盐值日志，释放存储空间
     * <p>
     * 删除条件：expire_time IS NOT NULL AND expire_time < now
     * <p>
     * 建议：通过定时任务（如 Spring @Scheduled）定期执行
     *
     * @return 删除的记录数
     */
    int cleanExpiredSalts();
}