package com.microwind.knife.domain.repository;

import com.microwind.knife.domain.sign.ApiAuth;

import java.util.Optional;

public interface ApiAuthRepository {
    Optional<ApiAuth> findByAppCode(String appCode); // 根据appCode查询权限
}