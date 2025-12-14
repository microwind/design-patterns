package com.microwind.knife.application.services.apiauth;

import com.microwind.knife.domain.apiauth.ApiUsers;
import com.microwind.knife.domain.repository.apiauth.ApiUsersJpaRepository;
import com.microwind.knife.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * API用户服务
 */
@Service
@RequiredArgsConstructor
public class ApiUsersService {

    private final ApiUsersJpaRepository apiUsersJpaRepository;

    /**
     * 根据appCode查询用户
     */
    public ApiUsers getByAppCode(String appCode) {
        return apiUsersJpaRepository.findByAppCode(appCode)
                .orElseThrow(() -> new ResourceNotFoundException("ApiUsers not found with appCode: " + appCode));
    }

    /**
     * 验证用户是否有效
     */
    public boolean validateUser(String appCode) {
        return apiUsersJpaRepository.findValidUser(appCode, LocalDateTime.now()).isPresent();
    }

    /**
     * 获取有效用户信息（用于签名验证）
     */
    public ApiUsers getValidUser(String appCode) {
        return apiUsersJpaRepository.findValidUser(appCode, LocalDateTime.now())
                .orElseThrow(() -> new ResourceNotFoundException("Valid ApiUsers not found with appCode: " + appCode));
    }

    /**
     * 创建API用户
     */
    @Transactional
    public ApiUsers createApiUser(ApiUsers apiUsers) {
        return apiUsersJpaRepository.save(apiUsers);
    }

    /**
     * 根据ID查询
     */
    public ApiUsers getById(Long id) {
        return apiUsersJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ApiUsers not found with id: " + id));
    }

    /**
     * 查询所有用户
     */
    public List<ApiUsers> getAllUsers() {
        return apiUsersJpaRepository.findAll();
    }

    /**
     * 更新用户信息
     */
    @Transactional
    public ApiUsers updateApiUser(Long id, ApiUsers apiUsers) {
        ApiUsers existingUser = getById(id);
        if (apiUsers.getAppCode() != null) {
            existingUser.setAppCode(apiUsers.getAppCode());
        }
        if (apiUsers.getAppName() != null) {
            existingUser.setAppName(apiUsers.getAppName());
        }
        if (apiUsers.getSecretKey() != null) {
            existingUser.setSecretKey(apiUsers.getSecretKey());
        }
        if (apiUsers.getStatus() != null) {
            existingUser.setStatus(apiUsers.getStatus());
        }
        if (apiUsers.getDailyLimit() != null) {
            existingUser.setDailyLimit(apiUsers.getDailyLimit());
        }
        if (apiUsers.getExpireTime() != null) {
            existingUser.setExpireTime(apiUsers.getExpireTime());
        }
        return apiUsersJpaRepository.save(existingUser);
    }

    /**
     * 删除用户
     */
    @Transactional
    public void deleteApiUser(Long id) {
        ApiUsers apiUsers = getById(id);
        apiUsersJpaRepository.delete(apiUsers);
    }
}
