package com.microwind.knife.domain.repository;

import com.microwind.knife.domain.sign.ApiAuth;

import java.util.Optional;

public interface ApiAuthRepository {
    Optional<ApiAuth> findByAppKey(String appKey); // 根据appKey查询权限
}