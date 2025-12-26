package com.microwind.knife.interfaces.controllers.apiauth;

import com.microwind.knife.application.config.SignConfig;
import com.microwind.knife.application.services.apiauth.ApiDynamicSaltLogService;
import com.microwind.knife.common.ApiResponse;
import com.microwind.knife.domain.apiauth.ApiDynamicSaltLog;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 动态盐值管理Controller
 */
@RestController
@RequestMapping("/api/apiauth/dynamic-salt-log")
@RequiredArgsConstructor
public class ApiDynamicSaltLogController {

    private final ApiDynamicSaltLogService saltLogService;
    private final SignConfig signConfig;

    /**
     * 验证并消费动态盐值
     */
    @PostMapping("/validate")
    public ApiResponse<Boolean> validateSalt(@RequestParam String appCode,
                                             @RequestParam String apiPath,
                                             @RequestParam String dynamicSalt) {
        boolean isValid = saltLogService.validateAndConsumeSalt(appCode, apiPath, dynamicSalt);
        return ApiResponse.success(isValid, "盐值验证完成");
    }

    /**
     * 生成动态盐值
     */
    @PostMapping("/create")
    public ApiResponse<ApiDynamicSaltLog> create(@RequestParam String appCode,
                                               @RequestParam Long apiId,
                                               @RequestParam String apiPath,
                                               @RequestParam String dynamicSalt,
                                               @RequestParam Long saltTimestamp,
                                               @RequestParam String expireTime) {
        LocalDateTime expireDateTime = LocalDateTime.parse(expireTime);
        ApiDynamicSaltLog saltLog = saltLogService.save(
                appCode, apiId, apiPath, dynamicSalt, saltTimestamp, expireDateTime
        );
        return ApiResponse.success(saltLog, "动态盐值创建成功。");
    }

    /**
     * 清理过期盐值
     */
    @DeleteMapping("/clean")
    public ApiResponse<Integer> cleanExpiredSalts(
            @RequestParam(defaultValue = "false") boolean force) {

        if (!force) {
            return ApiResponse.failure(HttpStatus.INTERNAL_SERVER_ERROR.value(), "必须显式指定 force=true");
        }

        int count = saltLogService.cleanExpiredSalts();
        return ApiResponse.success(count, "已清理 " + count + " 条过期盐值记录");
    }
}
