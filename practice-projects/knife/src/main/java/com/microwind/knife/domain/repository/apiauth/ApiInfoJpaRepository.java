package com.microwind.knife.domain.repository.apiauth;

import com.microwind.knife.domain.apiauth.ApiInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * API信息Repository
 */
@Repository
public interface ApiInfoJpaRepository extends JpaRepository<ApiInfo, Long> {

    /**
     * 根据apiPath查询接口信息
     */
    Optional<ApiInfo> findByApiPath(String apiPath);

    /**
     * 根据状态查询接口列表
     */
    List<ApiInfo> findByStatus(Short status);

    /**
     * 查询需要签名的接口（apiType = 2）
     */
    @Query("SELECT a FROM ApiInfo a WHERE a.apiType = 2 AND a.status = 1")
    List<ApiInfo> findSignRequiredApis();

    /**
     * 根据接口类型查询
     */
    List<ApiInfo> findByApiType(Short apiType);
}
