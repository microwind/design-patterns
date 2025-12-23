package com.microwind.knife.application.services.apiauth;

import com.microwind.knife.application.dto.apiauth.ApiUserDTO;
import com.microwind.knife.application.dto.apiauth.ApiUserMapper;
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
    private final ApiUserMapper apiUserMapper;
    private final ApiUsersJpaRepository apiUsersJpaRepository;

    /**
     * 根据appCode查询用户
     */
    public ApiUserDTO getByAppCode(String appCode) {
        ApiUsers apiUsers = apiUsersJpaRepository.findByAppCode(appCode)
                .orElseThrow(() -> new ResourceNotFoundException("ApiUsers not found with appCode: " + appCode));
        return apiUserMapper.toDTO(apiUsers);
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
    public ApiUserDTO getValidUser(String appCode) {
        ApiUsers apiUsers = apiUsersJpaRepository.findValidUser(appCode, LocalDateTime.now())
                .orElseThrow(() -> new ResourceNotFoundException("Valid ApiUsers not found with appCode: " + appCode));
        return apiUserMapper.toDTO(apiUsers);
    }

    /**
     * 创建API用户
     */
    @Transactional
    public ApiUserDTO createApiUser(ApiUsers apiUsers) {
        ApiUsers newApiUsers = apiUsersJpaRepository.save(apiUsers);
        return apiUserMapper.toDTO(newApiUsers);
    }

    /**
     * 根据ID查询
     */
    public ApiUserDTO getById(Long id) {
        ApiUsers newApiUsers = apiUsersJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ApiUsers not found with id: " + id));
        return apiUserMapper.toDTO(newApiUsers);
    }

    public ApiUsers getByUserId(Long id) {
        return apiUsersJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ApiUsers not found with id: " + id));
    }

    /**
     * 查询所有用户
     */
    public List<ApiUserDTO> getAllUsers() {
        List<ApiUsers> apiUsers = apiUsersJpaRepository.findAll();
        return apiUserMapper.toDTO(apiUsers);
    }

    /**
     * 更新用户信息
     */
    @Transactional
    public ApiUserDTO updateApiUser(Long id, ApiUsers apiUsers) {
        ApiUsers existingUser = getByUserId(id);
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
        ApiUsers newUser = apiUsersJpaRepository.save(existingUser);
        return apiUserMapper.toDTO(newUser);
    }

    /**
     * 删除用户
     */
    @Transactional
    public void deleteApiUser(Long id) {
        ApiUsers apiUsers = getByUserId(id);
        apiUsersJpaRepository.delete(apiUsers);
    }
}
