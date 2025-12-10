package com.microwind.knife.domain.repository;

import com.microwind.knife.domain.sign.AppAuth;

import java.util.Optional;

public interface AppAuthRepository {
    Optional<AppAuth> findByAppKey(String appKey); // 根据appKey查询权限
}