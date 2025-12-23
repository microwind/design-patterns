package com.microwind.knife.application.services.apiauth;

import com.microwind.knife.application.dto.sign.DynamicSaltDTO;
import com.microwind.knife.domain.apiauth.ApiDynamicSaltLog;
import com.microwind.knife.domain.repository.apiauth.ApiDynamicSaltLogJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 动态盐值日志服务
 * <p>
 * 负责动态盐值的数据库持久化和校验操作
 * <p>
 * 主要功能：
 * 1. 保存动态盐值到数据库（用于数据库校验模式）
 * 2. 验证并消费动态盐值（防止重放攻击）
 * 3. 清理过期的盐值记录
 */
@Service
@RequiredArgsConstructor
public class ApiDynamicSaltLogService {

    private final ApiDynamicSaltLogJpaRepository saltLogJpaRepository;

    /**
     * 验证并消费动态盐值
     * <p>
     * 功能：
     * 1. 查询数据库中是否存在匹配的、未使用的、未过期的盐值记录
     * 2. 如果存在，标记为已使用并返回 true
     * 3. 如果不存在或已使用或已过期，返回 false
     * <p>
     * 注意：此方法会修改数据库状态（标记盐值为已使用），实现一次性使用机制
     *
     * @param appCode     应用编码
     * @param apiPath     接口路径
     * @param dynamicSalt 动态盐值
     * @return true 校验通过并成功消费，false 校验失败
     */
    @Transactional(transactionManager = "apiAuthTransactionManager")
    public boolean validateAndConsumeSalt(String appCode, String apiPath, String dynamicSalt) {
        // 查询有效的盐值记录（未使用且未过期）
        Optional<ApiDynamicSaltLog> saltOpt = saltLogJpaRepository.findValidSalt(
                appCode, apiPath, dynamicSalt, LocalDateTime.now()
        );

        if (saltOpt.isPresent()) {
            // 标记为已使用，防止重放攻击
            saltLogJpaRepository.markAsUsed(saltOpt.get().getId());
            return true;
        }
        return false;
    }

    /**
     * 保存动态盐值到数据库（原始多参数方式）
     * <p>
     * 用于数据库校验模式，记录生成的动态盐值供后续验证使用
     *
     * @param appCode       应用编码
     * @param apiId         接口 ID
     * @param apiPath       接口路径
     * @param dynamicSalt   动态盐值
     * @param saltTimestamp 盐值生成时间戳（毫秒）
     * @param expireTime    过期时间
     * @return 保存的盐值日志对象
     */
    @Transactional(transactionManager = "apiAuthTransactionManager")
    public ApiDynamicSaltLog save(String appCode, Long apiId, String apiPath,
                                  String dynamicSalt, Long saltTimestamp,
                                  LocalDateTime expireTime) {
        ApiDynamicSaltLog saltLog = ApiDynamicSaltLog.builder()
                .appCode(appCode)
                .apiId(apiId)
                .apiPath(apiPath)
                .dynamicSalt(dynamicSalt)
                .saltTimestamp(saltTimestamp)
                .expireTime(expireTime)
                .used((short) 0)  // 初始状态：未使用
                .build();
        return saltLogJpaRepository.save(saltLog);
    }

    /**
     * 保存动态盐值到数据库（DTO 方式）
     * <p>
     * 用于数据库校验模式，记录生成的动态盐值供后续验证使用
     *
     * @param dto 动态盐值 DTO 对象
     * @return 保存的盐值日志对象
     */
    @Transactional(transactionManager = "apiAuthTransactionManager")
    public ApiDynamicSaltLog save(DynamicSaltDTO dto) {
        return save(
                dto.getAppCode(),
                dto.getApiId(),
                dto.getApiPath(),
                dto.getDynamicSalt(),
                dto.getSaltTimestamp(),
                dto.getExpireTime()
        );
    }

    /**
     * 清理过期的盐值记录
     * <p>
     * 定期调用此方法可以清理数据库中已过期的盐值记录，释放存储空间
     * 建议通过定时任务（如 @Scheduled）定期执行
     *
     * @return 删除的记录数
     */
    @Transactional(transactionManager = "apiAuthTransactionManager")
    public int cleanExpiredSalts() {
        return saltLogJpaRepository.deleteExpiredSalts(LocalDateTime.now());
    }
}

