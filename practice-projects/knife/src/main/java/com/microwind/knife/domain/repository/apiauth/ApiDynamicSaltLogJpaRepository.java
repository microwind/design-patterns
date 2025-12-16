package com.microwind.knife.domain.repository.apiauth;

import com.microwind.knife.domain.apiauth.ApiDynamicSaltLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 动态盐值日志Repository
 */
@Repository
public interface ApiDynamicSaltLogJpaRepository extends JpaRepository<ApiDynamicSaltLog, Long> {

    /**
     * 查询未使用且未过期的动态盐值
     */
    @Query("SELECT s FROM ApiDynamicSaltLog s WHERE s.appCode = :appCode AND s.apiPath = :apiPath " +
           "AND s.dynamicSalt = :dynamicSalt AND s.used = 0 AND s.expireTime > :now")
    Optional<ApiDynamicSaltLog> findValidSalt(@Param("appCode") String appCode,
                                              @Param("apiPath") String apiPath,
                                              @Param("dynamicSalt") String dynamicSalt,
                                              @Param("now") LocalDateTime now);

    /**
     * 标记盐值为已使用
     */
    @Modifying
    @Query("UPDATE ApiDynamicSaltLog s SET s.used = 1 WHERE s.id = :id")
    int markAsUsed(@Param("id") Long id);

    /**
     * 删除过期的盐值记录
     */
    @Modifying
    @Query("DELETE FROM ApiDynamicSaltLog s WHERE s.expireTime < :now")
    int deleteExpiredSalts(@Param("now") LocalDateTime now);
}
