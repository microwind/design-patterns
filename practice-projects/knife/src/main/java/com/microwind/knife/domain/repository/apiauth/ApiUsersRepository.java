package com.microwind.knife.domain.repository.apiauth;

import com.microwind.knife.domain.apiauth.ApiUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * API用户Repository
 */
@Repository
public interface ApiUsersRepository extends JpaRepository<ApiUsers, Long> {

    /**
     * 根据appCode查询用户
     */
    Optional<ApiUsers> findByAppCode(String appCode);

    /**
     * 查询有效的API用户（状态为1且未过期）
     */
    @Query("SELECT u FROM ApiUsers u WHERE u.appCode = :appCode AND u.status = 1 " +
           "AND (u.expireTime IS NULL OR u.expireTime > :now)")
    Optional<ApiUsers> findValidUser(@Param("appCode") String appCode,
                                     @Param("now") LocalDateTime now);

    /**
     * 根据状态查询用户列表
     */
    List<ApiUsers> findByStatus(Short status);
}
