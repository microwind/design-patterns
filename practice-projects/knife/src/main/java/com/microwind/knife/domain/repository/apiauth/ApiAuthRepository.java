package com.microwind.knife.domain.repository.apiauth;

import com.microwind.knife.domain.apiauth.ApiAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * API权限Repository
 */
@Repository
public interface ApiAuthRepository extends JpaRepository<ApiAuth, Long> {

    /**
     * 根据appCode和apiPath查询权限
     */
    Optional<ApiAuth> findByAppCodeAndApiPath(String appCode, String apiPath);

    /**
     * 检查权限是否有效（状态为1且未过期）
     */
    @Query("SELECT a FROM ApiAuth a WHERE a.appCode = :appCode AND a.apiPath = :apiPath " +
           "AND a.status = 1 AND (a.expireTime IS NULL OR a.expireTime > :now)")
    Optional<ApiAuth> findValidAuth(@Param("appCode") String appCode,
                                    @Param("apiPath") String apiPath,
                                    @Param("now") LocalDateTime now);
}
