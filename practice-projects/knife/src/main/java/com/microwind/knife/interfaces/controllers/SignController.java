package com.microwind.knife.interfaces.controllers;

import com.microwind.knife.application.config.SignConfig;
import com.microwind.knife.application.dto.sign.DynamicSaltDTO;
import com.microwind.knife.application.dto.sign.DynamicSaltMapper;
import com.microwind.knife.application.dto.sign.SignDTO;
import com.microwind.knife.application.dto.sign.SignMapper;
import com.microwind.knife.application.services.sign.DynamicSaltService;
import com.microwind.knife.application.services.sign.DynamicSaltValidationService;
import com.microwind.knife.application.services.sign.SignService;
import com.microwind.knife.application.services.sign.SignValidationService;
import com.microwind.knife.common.ApiResponse;
import com.microwind.knife.interfaces.vo.sign.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 签名控制器
 * <p>
 * 提供签名相关的 REST API 接口，包括：
 * 1. 动态盐值生成和校验
 * 2. 签名生成和校验
 * 3. 带签名的请求提交
 */
@RestController
@RequestMapping("/api/sign")
@RequiredArgsConstructor
public class SignController {
    private final DynamicSaltService dynamicSaltService;
    private final SignService signService;
    private final SignValidationService signValidationService;
    private final DynamicSaltValidationService dynamicSaltValidationService;
    private final SignConfig signConfig;
    private final SignMapper signMapper;
    private final DynamicSaltMapper dynamicSaltMapper;

    /**
     * 动态盐值生成接口
     * <p>
     * 功能：为指定应用和接口生成动态盐值
     * <p>
     * 流程：
     * 1. 验证应用权限
     * 2. 获取接口固定盐值
     * 3. 基于 appCode + path + interfaceSalt + timestamp 生成动态盐值
     * 4. 如果配置了数据库校验，将盐值保存到数据库
     *
     * @param request 包含 appCode 和 path 的请求
     * @return 生成的动态盐值信息
     */
    @PostMapping("/dynamic-salt-generate")
    public ApiResponse<DynamicSaltResponse> generateDynamicSalt(@RequestBody DynamicSaltRequest request) {
        // 基于接口固定盐值生成动态盐值
        DynamicSaltDTO salt = dynamicSaltService.generate(request.getAppCode(), request.getPath());
        // 构建动态盐值响应
        DynamicSaltResponse response = new DynamicSaltResponse();
        response.setPath(salt.getApiPath());
        response.setAppCode(request.getAppCode());
        response.setDynamicSalt(salt.getDynamicSalt());
        response.setDynamicSaltTime(salt.getSaltTimestamp());

        if (signConfig.isValidateDynamicSaltFromDatabase()) {
            return ApiResponse.success(response, "动态盐值创建并保存成功。");
        } else {
            return ApiResponse.success(response, "动态盐值生成成功。");
        }
    }


    /**
     * 动态盐值校验接口
     * <p>
     * 功能：验证客户端提供的动态盐值是否有效
     * <p>
     * 校验规则：
     * 1. 基础校验：参数完整性、时间戳有效性
     * 2. 数据库模式 + 开启数据库校验：查询数据库中的盐值记录，验证后标记为已使用
     * 3. 算法校验模式：基于 appCode + path + interfaceSalt + timestamp 重新计算并比对
     * 4. TTL 时间窗口检查：盐值必须在有效期内
     * <p>
     * 约定说明：
     * - path 必须为接口模板路径（如 /api/users 或 /api/users/{userId}）
     * - 客户端与服务端需使用完全一致的模板路径参与签名计算
     * - 数据库校验模式下，每个盐值仅可使用一次（防止重放攻击）
     *
     * @param request 包含 appCode, path, dynamicSalt, dynamicSaltTime 的请求
     * @return 校验结果及动态盐值信息
     */
    @PostMapping("/dynamic-salt-validate")
    public ApiResponse<Map<String, Object>> validateDynamicSalt(@RequestBody DynamicSaltVerfiyRequest request) {
        String appCode = request.getAppCode();
        String path = request.getPath();
        String dynamicSalt = request.getDynamicSalt();
        Long dynamicSaltTime = request.getDynamicSaltTime();
        boolean isValid;
        if (appCode == null || path == null || dynamicSalt == null || dynamicSaltTime == null) {
            isValid = false;
        } else {
            // 执行动态盐值校验
            DynamicSaltDTO dynamicSaltDTO = dynamicSaltMapper.toDTO(request);
            isValid = dynamicSaltValidationService.validate(dynamicSaltDTO);
        }

        // 构建响应中的动态盐值信息
        DynamicSaltResponse dynamicSaltResponse = new DynamicSaltResponse();
        dynamicSaltResponse.setPath(path);
        dynamicSaltResponse.setAppCode(appCode);
        dynamicSaltResponse.setDynamicSalt(dynamicSalt);
        dynamicSaltResponse.setDynamicSaltTime(dynamicSaltTime);

        // 构建统一响应体
        Map<String, Object> response = new HashMap<>();
        response.put("isValid", isValid);
        response.put("dynamicSalt", dynamicSaltResponse);

        // 返回校验结果
        if (isValid) {
            return ApiResponse.success(response, "动态盐值校验成功。");
        } else {
            return ApiResponse.success(response, "动态盐值校验失败，签名不正确或已使用。");
        }
    }


    /**
     * 签名生成接口
     * <p>
     * 功能：为已通过动态盐值校验的请求生成签名
     * <p>
     * 流程：
     * 1. 校验动态盐值的有效性
     * 2. 验证应用权限
     * 3. 基于 appCode + secretKey + path + timestamp 生成签名
     * <p>
     * 约定说明：
     * - 必须先调用 /dynamic-salt-generate 获取动态盐值
     * - dynamicSalt 和 dynamicSaltTime 必须与生成时保持一致
     *
     * @param request 包含 appCode, path, dynamicSalt, dynamicSaltTime 的请求
     * @return 生成的签名信息
     */
    @PostMapping("/generate")
    public ApiResponse<SignResponse> generateSign(@RequestBody SignRequest request) {
        SignDTO dto = signService.generate(signMapper.toDTO(request));
        SignResponse response = new SignResponse();
        response.setPath(dto.getApiPath());
        response.setSign(dto.getSignValue());
        response.setTime(dto.getTimestamp());
        response.setExpireTime(dto.getExpireTime());
        return ApiResponse.success(response, "申请签名成功。");
    }

    /**
     * 签名校验接口
     * <p>
     * 功能：验证客户端提供的签名是否有效
     * <p>
     * 校验规则：
     * 1. 基础校验：参数完整性、时间戳有效性
     * 2. 根据配置模式（本地配置 或 数据库）获取应用秘钥
     * 3. 算法校验：基于 appCode + secretKey + path + timestamp 重新计算并比对
     * 4. TTL 时间窗口检查：签名必须在有效期内
     * <p>
     * 约定说明：
     * - path 必须为接口模板路径（如 /api/users 或 /api/users/{userId}）
     * - 客户端与服务端需使用完全一致的模板路径参与签名计算
     *
     * @param request 包含 appCode, path, sign, time 的请求
     * @return 校验结果及签名信息
     */
    @PostMapping("/sign-validate")
    public ApiResponse<Map<String, Object>> validateSign(@RequestBody SignVerifyRequest request) {
        String appCode = request.getAppCode();
        String path = request.getPath();
        String sign = request.getSign();
        Long time = request.getTime();
        boolean isValid;
        if (appCode == null || path == null || sign == null || time == null) {
            isValid = false;
        } else {
            // 执行签名校验
            SignDTO signDTO = signMapper.toDTO(request);
            isValid = signValidationService.validate(signDTO);
        }

        // 构建响应中的签名信息
        SignResponse signResponse = new SignResponse();
        signResponse.setAppCode(appCode);
        signResponse.setPath(path);
        signResponse.setSign(sign);
        signResponse.setTime(time);

        // 构建统一响应体
        Map<String, Object> response = new HashMap<>();
        response.put("isValid", isValid);
        response.put("sign", signResponse);

        // 返回校验结果
        if (isValid) {
            return ApiResponse.success(response, "签名校验成功。");
        } else {
            return ApiResponse.success(response, "签名校验失败，签名不正确或已过期。");
        }
    }

    /**
     * 带签名的数据提交接口（测试）
     * <p>
     * 功能：验证签名并提交业务数据
     * <p>
     * 流程：
     * 1. 校验应用权限
     * 2. 验证签名的有效性和时效性
     * 3. 执行业务逻辑（保存数据等）
     * <p>
     * 使用说明：
     * - 此为测试接口，其他业务接口可参考此实现
     * - 必须携带有效的签名才能提交数据
     *
     * @return 提交结果
     * @header 包含 appCode, path, sign, time 签名请求
     */
    @PostMapping("/submit-test")
    public ApiResponse<Map<String, Object>> submit(
            @RequestHeader(value = "appCode", required = false) String appCode,
            @RequestHeader(value = "sign", required = false) String sign,
            @RequestHeader(value = "path", required = false) String path,
            @RequestHeader(value = "time", required = false) Long time,
            @RequestBody(required = false) Map<String, Object> body) {
        SignVerifyRequest signVerifyRequest = new SignVerifyRequest();
        signVerifyRequest.setAppCode(appCode);
        signVerifyRequest.setPath(path);
        signVerifyRequest.setSign(sign);
        signVerifyRequest.setTime(time);
        SignDTO signDTO = signMapper.toDTO(signVerifyRequest);
        boolean isValid = signValidationService.validate(signDTO);
        Map<String, Object> response = new HashMap<>();
        response.put("isValid", isValid);
        response.put("body", body);
        if (isValid) {
            return ApiResponse.success(response, "带签名的请求提交成功。");
        } else {
            return ApiResponse.failure(HttpStatus.INTERNAL_SERVER_ERROR.value(), response, "带签名的请求校验失败。");
        }
    }
}