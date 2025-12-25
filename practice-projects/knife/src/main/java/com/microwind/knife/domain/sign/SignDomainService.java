package com.microwind.knife.domain.sign;

import com.microwind.knife.application.config.SignConfig;
import com.microwind.knife.application.dto.sign.DynamicSaltDTO;
import com.microwind.knife.application.dto.sign.SignDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HexFormat;

/**
 * 签名领域服务
 * <p>
 * 提供签名和动态盐值的生成、验证等核心业务逻辑
 * 所有签名和盐值算法统一使用 SHA-256
 * </p>
 *
 * @author jarry
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SignDomainService {

    private final SignConfig signConfig;

    /**
     * SHA-256 哈希计算
     * <p>
     * 将输入字符串使用 SHA-256 算法进行哈希，返回十六进制字符串
     * </p>
     *
     * @param input 待哈希的字符串
     * @return SHA-256 哈希值（十六进制小写字符串）
     * @throws IllegalStateException 如果 SHA-256 算法不可用
     */
    public static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            log.error("SHA-256 计算失败: input={}", input, e);
            throw new IllegalStateException("SHA-256 algorithm error", e);
        }
    }

    public static String sm3(String input) {
        try {

        } catch (Exception e) {
        }
        return "";
    }

    //
    public Sign generateSignWithParams(String appCode, String secretKey, String path) {
        log.debug("生成签名: appCode={}, path={}", appCode, path);

        // 获取当前时间戳（毫秒）
        long timestamp = System.currentTimeMillis();

        // 注意：拼接顺序必须与校验端完全一致
        String signSource = appCode + secretKey + path + timestamp;

        // 使用 SHA-256 计算签名值
        String signValue = sm3(signSource);

        // 计算签名过期时间
        long expireTimestamp = timestamp + signConfig.getSignatureTtl();
        LocalDateTime expireTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(expireTimestamp),
                ZoneId.systemDefault()
        );

        log.debug("签名生成成功: sign={}, timestamp={}, expireTime={}",
                signValue, timestamp, expireTime);

        return new Sign(appCode, path, signValue, timestamp, expireTime);
    }

    /**
     * 生成动态盐值
     * <p>
     * 动态盐值生成算法：SHA-256(appCode + apiPath + interfaceSalt + saltTimestamp)
     * 用于防止重放攻击，每次请求使用不同的盐值
     * </p>
     *
     * @param appCode        应用编码
     * @param apiPath        接口路径
     * @param interfaceSalt  接口固定盐值
     * @param saltTimestamp  盐值时间戳（毫秒）
     * @return 动态盐值对象，包含盐值和过期时间
     */
    public DynamicSalt generateDynamicSalt(String appCode, String apiPath,
                                           String interfaceSalt, Long saltTimestamp) {
        log.debug("生成动态盐值: appCode={}, apiPath={}, timestamp={}",
                appCode, apiPath, saltTimestamp);

        // 拼接原始字符串
        String saltSource = appCode + apiPath + interfaceSalt + saltTimestamp;

        // 使用 sha256 计算动态盐值
        String dynamicSaltValue = sha256(saltSource);

        // 如果配置了 TTL，则 expireTime = saltTimestamp + TTL
        // 如果未配置 TTL，则 expireTime 为 null（永不过期）
        Long ttl = signConfig.getDynamicSaltTtl();
        LocalDateTime expireTime = ttl == null
                ? null
                : Instant.ofEpochMilli(saltTimestamp + ttl)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        log.debug("动态盐值生成成功: salt={}, expireTime={}", dynamicSaltValue, expireTime);

        return new DynamicSalt(appCode, apiPath, dynamicSaltValue, expireTime, saltTimestamp);
    }

    /**
     * 生成动态盐值（DTO 版本）
     * <p>
     * 从 DTO 对象中提取参数，调用核心生成方法
     * </p>
     *
     * @param dynamicSaltDTO 动态盐值请求 DTO
     * @param interfaceSalt  接口固定盐值
     * @return 动态盐值对象
     */
    public DynamicSalt generateDynamicSalt(DynamicSaltDTO dynamicSaltDTO, String interfaceSalt) {
        return generateDynamicSalt(
                dynamicSaltDTO.getAppCode(),
                dynamicSaltDTO.getApiPath(),
                interfaceSalt,
                dynamicSaltDTO.getSaltTimestamp()
        );
    }

    /**
     * 校验动态盐值有效性
     * <p>
     * 校验算法：重新计算 SHA-256(appCode + apiPath + interfaceSalt + saltTimestamp)
     * 比较计算结果与传入的 dynamicSalt 是否一致
     * </p>
     * <p>
     * 注意：此方法只校验盐值计算是否正确，不校验时间是否过期
     * 时间过期校验应在上层业务逻辑中处理
     * </p>
     *
     * @param appCode        应用编码
     * @param apiPath        接口路径
     * @param interfaceSalt  接口固定盐值
     * @param dynamicSalt    待校验的动态盐值
     * @param saltTimestamp  盐值时间戳（毫秒）
     * @return true-校验通过，false-校验失败
     */
    public boolean validateDynamicSalt(String appCode, String apiPath, String interfaceSalt,
                                       String dynamicSalt, Long saltTimestamp) {
        log.debug("校验动态盐值: appCode={}, apiPath={}, timestamp={}",
                appCode, apiPath, saltTimestamp);

        // 计算动态盐值算法与生成时一致
        String expectedSaltSource = appCode + apiPath + interfaceSalt + saltTimestamp;
        String expectedSaltValue = sha256(expectedSaltSource);

        // 比较计算结果与传入值
        boolean valid = expectedSaltValue.equals(dynamicSalt);

        if (!valid) {
            log.warn("动态盐值校验失败: expected={}, actual={}", expectedSaltValue, dynamicSalt);
        } else {
            log.debug("动态盐值校验通过");
        }

        return valid;
    }

    /**
     * 校验动态盐值有效性（DTO 版本）
     * <p>
     * 从 DTO 对象中提取参数，调用核心校验方法
     * </p>
     *
     * @param dto           动态盐值 DTO
     * @param interfaceSalt 接口固定盐值
     * @return true-校验通过，false-校验失败
     */
    public boolean validateDynamicSalt(DynamicSaltDTO dto, String interfaceSalt) {
        return validateDynamicSalt(
                dto.getAppCode(),
                dto.getApiPath(),
                interfaceSalt,
                dto.getDynamicSalt(),
                dto.getSaltTimestamp()
        );
    }

    /**
     * 生成签名
     * <p>
     * 签名生成算法：SHA-256(appCode + secretKey + timestamp + path)
     * 用于验证请求的合法性和完整性
     * </p>
     * <p>
     * 生成流程：
     * 1. 获取当前时间戳
     * 2. 按固定顺序拼接字符串
     * 3. 使用 SHA-256 计算签名值
     * 4. 计算签名过期时间
     * </p>
     *
     * @param appCode   应用编码
     * @param secretKey 应用密钥
     * @param path      接口路径
     * @return 签名对象，包含签名值、时间戳和过期时间
     */
    public Sign generateSign(String appCode, String secretKey, String path) {
        log.debug("生成签名: appCode={}, path={}", appCode, path);

        // 获取当前时间戳（毫秒）
        long timestamp = System.currentTimeMillis();

        // 注意：拼接顺序必须与校验端完全一致
        String signSource = appCode + secretKey + path + timestamp;

        // 使用 SHA-256 计算签名值
        String signValue = sha256(signSource);

        // 计算签名过期时间
        long expireTimestamp = timestamp + signConfig.getSignatureTtl();
        LocalDateTime expireTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(expireTimestamp),
                ZoneId.systemDefault()
        );

        log.debug("签名生成成功: sign={}, timestamp={}, expireTime={}",
                signValue, timestamp, expireTime);

        return new Sign(appCode, path, signValue, timestamp, expireTime);
    }

    /**
     * 生成签名（DTO 版本）
     * <p>
     * 从 DTO 对象中提取参数，调用核心生成方法
     * </p>
     *
     * @param signDTO   签名请求 DTO
     * @param secretKey 应用密钥
     * @return 签名对象
     */
    public Sign generateSign(SignDTO signDTO, String secretKey) {
        return generateSign(
                signDTO.getAppCode(),
                secretKey,
                signDTO.getApiPath()
        );
    }

    /**
     * 校验签名有效性
     * <p>
     * 校验算法：重新计算 SHA-256(appCode + secretKey + path + timestamp)
     * 比较计算结果与传入的 signValue 是否一致
     * </p>
     * <p>
     * 注意：
     * 1. 此方法只校验签名计算是否正确，不校验时间是否过期
     * 2. 拼接顺序必须与生成签名时完全一致
     * 3. 时间过期校验应在上层业务逻辑中处理
     * </p>
     *
     * @param appCode   应用编码
     * @param secretKey 应用密钥
     * @param path      接口路径
     * @param signValue 待校验的签名值
     * @param timestamp 签名时间戳（毫秒）
     * @return true-校验通过，false-校验失败
     */
    public boolean validateSign(String appCode, String secretKey, String path,
                                String signValue, Long timestamp) {
        log.debug("校验签名: appCode={}, path={}, timestamp={}", appCode, path, timestamp);

        // 重新计算签名，算法与生成时需要一致
        String signSource = appCode + secretKey + path + timestamp;
        String expectedSign = sha256(signSource);

        // 比较计算结果与传入值
        boolean valid = expectedSign.equals(signValue);

        if (!valid) {
            log.warn("签名校验失败: expected={}, actual={}", expectedSign, signValue);
        } else {
            log.debug("签名校验通过");
        }

        return valid;
    }

    /**
     * 校验签名有效性（DTO 版本）
     * <p>
     * 从 DTO 对象中提取参数，调用核心校验方法
     * </p>
     *
     * @param dto       签名 DTO
     * @param secretKey 应用密钥
     * @return true-校验通过，false-校验失败
     */
    public boolean validateSign(SignDTO dto, String secretKey) {
        return validateSign(
                dto.getAppCode(),
                secretKey,
                dto.getApiPath(),
                dto.getSignValue(),
                dto.getTimestamp()
        );
    }
}