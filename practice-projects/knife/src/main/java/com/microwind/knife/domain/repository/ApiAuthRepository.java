package com.microwind.knife.domain.repository;

import com.microwind.knife.domain.sign.SignUserAuth;

import java.util.Optional;

public interface ApiAuthRepository {
    Optional<SignUserAuth> findByAppCode(String appCode); // 根据appCode查询权限
}