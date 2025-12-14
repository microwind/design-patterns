package com.microwind.knife.application.services.apiauth;

import com.microwind.knife.domain.apiauth.ApiInfo;
import com.microwind.knife.domain.repository.apiauth.ApiInfoJpaRepository;
import com.microwind.knife.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * API信息服务
 */
@Service
@RequiredArgsConstructor
public class ApiInfoService {

    private final ApiInfoJpaRepository apiInfoJpaRepository;

    /**
     * 根据apiPath查询接口信息
     */
    public ApiInfo getByApiPath(String apiPath) {
        return apiInfoJpaRepository.findByApiPath(apiPath)
                .orElseThrow(() -> new ResourceNotFoundException("ApiInfo not found with apiPath: " + apiPath));
    }

    /**
     * 创建API信息
     */
    @Transactional
    public ApiInfo createApiInfo(ApiInfo apiInfo) {
        return apiInfoJpaRepository.save(apiInfo);
    }

    /**
     * 根据ID查询
     */
    public ApiInfo getById(Long id) {
        return apiInfoJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ApiInfo not found with id: " + id));
    }

    /**
     * 查询所有API信息
     */
    public List<ApiInfo> getAllApiInfos() {
        return apiInfoJpaRepository.findAll();
    }

    /**
     * 查询需要签名的API列表
     */
    public List<ApiInfo> getSignRequiredApis() {
        return apiInfoJpaRepository.findSignRequiredApis();
    }

    /**
     * 更新API信息
     */
    @Transactional
    public ApiInfo updateApiInfo(Long id, ApiInfo apiInfo) {
        ApiInfo existingApiInfo = getById(id);
        if (apiInfo.getApiPath() != null) {
            existingApiInfo.setApiPath(apiInfo.getApiPath());
        }
        if (apiInfo.getApiName() != null) {
            existingApiInfo.setApiName(apiInfo.getApiName());
        }
        if (apiInfo.getApiType() != null) {
            existingApiInfo.setApiType(apiInfo.getApiType());
        }
        if (apiInfo.getFixedSalt() != null) {
            existingApiInfo.setFixedSalt(apiInfo.getFixedSalt());
        }
        if (apiInfo.getRateLimit() != null) {
            existingApiInfo.setRateLimit(apiInfo.getRateLimit());
        }
        if (apiInfo.getStatus() != null) {
            existingApiInfo.setStatus(apiInfo.getStatus());
        }
        if (apiInfo.getDescription() != null) {
            existingApiInfo.setDescription(apiInfo.getDescription());
        }
        return apiInfoJpaRepository.save(existingApiInfo);
    }

    /**
     * 删除API信息
     */
    @Transactional
    public void deleteApiInfo(Long id) {
        ApiInfo apiInfo = getById(id);
        apiInfoJpaRepository.delete(apiInfo);
    }
}
