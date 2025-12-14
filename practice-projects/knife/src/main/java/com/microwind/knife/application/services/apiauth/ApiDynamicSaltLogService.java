package com.microwind.knife.application.services.apiauth;

import com.microwind.knife.domain.apiauth.ApiDynamicSaltLog;
import com.microwind.knife.domain.repository.apiauth.ApiDynamicSaltLogJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 动态盐值服务
 */
@Service
@RequiredArgsConstructor
public class ApiDynamicSaltLogService {

    private final ApiDynamicSaltLogJpaRepository saltLogJpaRepository;

    /**
     * 验证并消费动态盐值
     */
    @Transactional
    public boolean validateAndConsumeSalt(String appCode, String apiPath, String dynamicSalt) {
        Optional<ApiDynamicSaltLog> saltOpt = saltLogJpaRepository.findValidSalt(
                appCode, apiPath, dynamicSalt, LocalDateTime.now()
        );

        if (saltOpt.isPresent()) {
            // 标记为已使用
            saltLogJpaRepository.markAsUsed(saltOpt.get().getId());
            return true;
        }
        return false;
    }

    /**
     * 生成并保存动态盐值
     */
    @Transactional
    public ApiDynamicSaltLog generateSalt(String appCode, Long apiId, String apiPath,
                                          String dynamicSalt, Long saltTimestamp,
                                          LocalDateTime expireTime) {
        ApiDynamicSaltLog saltLog = ApiDynamicSaltLog.builder()
                .appCode(appCode)
                .apiId(apiId)
                .apiPath(apiPath)
                .dynamicSalt(dynamicSalt)
                .saltTimestamp(saltTimestamp)
                .expireTime(expireTime)
                .used((short) 0)
                .build();
        return saltLogJpaRepository.save(saltLog);
    }

    /**
     * 清理过期的盐值记录
     */
    @Transactional
    public int cleanExpiredSalts() {
        return saltLogJpaRepository.deleteExpiredSalts(LocalDateTime.now());
    }
}
