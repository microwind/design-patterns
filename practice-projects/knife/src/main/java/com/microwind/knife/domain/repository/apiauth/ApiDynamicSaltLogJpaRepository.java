package com.microwind.knife.domain.repository.apiauth;

import com.microwind.knife.domain.apiauth.ApiDynamicSaltLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 动态盐值日志Repository
 *
 * @author your-name
 * @since 1.0.0
 */
@Repository
public interface ApiDynamicSaltLogJpaRepository extends JpaRepository<ApiDynamicSaltLog, Long> {

    /**
     * 查询未使用且未过期的动态盐值
     *
     * @param appCode     应用编码
     * @param apiPath     接口路径
     * @param dynamicSalt 动态盐值
     * @param now         当前时间
     * @return 有效的动态盐值记录，如果不存在返回 empty
     */
    @Query("""
            SELECT s FROM ApiDynamicSaltLog s
            WHERE s.appCode = :appCode
              AND s.apiPath = :apiPath
              AND s.dynamicSalt = :dynamicSalt
              AND s.used = 0
              AND (s.expireTime IS NULL OR s.expireTime > :now)
            """)
    Optional<ApiDynamicSaltLog> findValidSalt(
            @Param("appCode") String appCode,
            @Param("apiPath") String apiPath,
            @Param("dynamicSalt") String dynamicSalt,
            @Param("now") LocalDateTime now
    );

    /**
     * 标记盐值为已使用
     *
     * @param id 记录ID
     * @return 更新的记录数
     */
    @Modifying
    @Transactional
    @Query("UPDATE ApiDynamicSaltLog s SET s.used = 1 WHERE s.id = :id")
    int markAsUsed(@Param("id") Long id);

    /**
     * 批量标记盐值为已使用
     *
     * @param ids 记录ID列表
     * @return 更新的记录数
     */
    @Modifying
    @Transactional
    @Query("UPDATE ApiDynamicSaltLog s SET s.used = 1 WHERE s.id IN :ids")
    int markAsUsed(@Param("ids") List<Long> ids);

    /**
     * 删除过期的盐值记录
     *
     * @param now 当前时间
     * @return 删除的记录数
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM ApiDynamicSaltLog s WHERE s.expireTime IS NOT NULL AND s.expireTime < :now")
    int deleteExpiredSalts(@Param("now") LocalDateTime now);

    /**
     * 删除已使用且过期的盐值记录（更安全的清理策略）
     *
     * @param now 当前时间
     * @return 删除的记录数
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM ApiDynamicSaltLog s WHERE s.used = 1 AND s.expireTime < :now")
    int deleteUsedAndExpiredSalts(@Param("now") LocalDateTime now);

    /**
     * 统计指定应用的未使用盐值数量
     *
     * @param appCode 应用编码
     * @param now     当前时间
     * @return 未使用的盐值数量
     */
    @Query("""
            SELECT COUNT(s) FROM ApiDynamicSaltLog s
            WHERE s.appCode = :appCode
              AND s.used = 0
              AND (s.expireTime IS NULL OR s.expireTime > :now)
            """)
    long countUnusedSalts(@Param("appCode") String appCode, @Param("now") LocalDateTime now);

    /**
     * 查询指定时间范围内的盐值记录
     *
     * @param appCode   应用编码
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 盐值记录列表
     */
    @Query("""
            SELECT s FROM ApiDynamicSaltLog s
            WHERE s.appCode = :appCode
              AND s.createdAt BETWEEN :startTime AND :endTime
            ORDER BY s.createdAt DESC
            """)
    List<ApiDynamicSaltLog> findByAppCodeAndTimeRange(
            @Param("appCode") String appCode,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    /**
     * 使用方法命名查询（Spring Data JPA 自动实现）
     * 根据应用编码和路径查询未使用的盐值（used = 0）
     */
    List<ApiDynamicSaltLog> findByAppCodeAndApiPathAndUsed(
            String appCode,
            String apiPath,
            int used
    );

    /**
     * 检查是否存在未使用的动态盐值
     */
    @Query("""
                SELECT COUNT(s) > 0
                FROM ApiDynamicSaltLog s
                WHERE s.appCode = :appCode
                  AND s.apiPath = :apiPath
                  AND s.dynamicSalt = :dynamicSalt
                  AND s.used = 0
            """)
    boolean existsUnusedSalt(@Param("appCode") String appCode,
                             @Param("apiPath") String apiPath,
                             @Param("dynamicSalt") String dynamicSalt);

}