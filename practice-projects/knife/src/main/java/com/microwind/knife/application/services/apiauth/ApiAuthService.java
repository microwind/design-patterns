package com.microwind.knife.application.services.apiauth;

import com.microwind.knife.domain.apiauth.ApiAuth;
import com.microwind.knife.domain.repository.apiauth.ApiAuthRepository;
import com.microwind.knife.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * API权限服务
 */
@Service
@RequiredArgsConstructor
public class ApiAuthService {

    private final ApiAuthRepository apiAuthRepository;

    /**
     * 检查权限是否有效
     */
    public boolean checkAuth(String appCode, String apiPath) {
        return apiAuthRepository.findValidAuth(appCode, apiPath, LocalDateTime.now()).isPresent();
    }

    /**
     * 创建权限
     */
    @Transactional
    public ApiAuth createAuth(ApiAuth apiAuth) {
        return apiAuthRepository.save(apiAuth);
    }

    /**
     * 根据ID查询
     */
    public ApiAuth getById(Long id) {
        return apiAuthRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ApiAuth not found with id: " + id));
    }

    /**
     * 查询所有权限
     */
    public List<ApiAuth> getAllAuths() {
        return apiAuthRepository.findAll();
    }

    /**
     * 更新权限
     */
    @Transactional
    public ApiAuth updateAuth(Long id, ApiAuth apiAuth) {
        ApiAuth existingAuth = getById(id);
        if (apiAuth.getAppCode() != null) {
            existingAuth.setAppCode(apiAuth.getAppCode());
        }
        if (apiAuth.getApiPath() != null) {
            existingAuth.setApiPath(apiAuth.getApiPath());
        }
        if (apiAuth.getStatus() != null) {
            existingAuth.setStatus(apiAuth.getStatus());
        }
        if (apiAuth.getExpireTime() != null) {
            existingAuth.setExpireTime(apiAuth.getExpireTime());
        }
        return apiAuthRepository.save(existingAuth);
    }

    /**
     * 删除权限
     */
    @Transactional
    public void deleteAuth(Long id) {
        ApiAuth apiAuth = getById(id);
        apiAuthRepository.delete(apiAuth);
    }
}
