package com.microwind.knife.interfaces.controllers.sign;

import com.microwind.knife.application.config.SignConfig;
import com.microwind.knife.application.dto.sign.DynamicSaltDTO;
import com.microwind.knife.application.dto.sign.DynamicSaltMapper;
import com.microwind.knife.application.dto.sign.SignDTO;
import com.microwind.knife.application.dto.sign.SignMapper;
import com.microwind.knife.application.services.sign.DynamicSaltService;
import com.microwind.knife.application.services.sign.SignService;
import com.microwind.knife.common.ApiResponse;
import com.microwind.knife.domain.sign.SignUserAuth;
import com.microwind.knife.interfaces.annotation.IgnoreSignHeader;
import com.microwind.knife.interfaces.vo.sign.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RestController
@RequestMapping("/api/sign")
@RequiredArgsConstructor
public class SignController {
    private final DynamicSaltService dynamicSaltService;
    private final SignService signService;
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
    @IgnoreSignHeader
    public ApiResponse<DynamicSaltResponse> generateDynamicSalt(
            @RequestHeader(value = "appCode", required = false) String appCode,
            @RequestHeader(value = "path", required = false) String path,
            @RequestBody(required = false) DynamicSaltRequest request
    ) {
        if (appCode == null && request != null) {
            appCode = request.getAppCode();
        }
        if (path == null && request != null) {
            path = request.getPath();
        }
        if (appCode == null || appCode.trim().isEmpty()) {
            throw new IllegalArgumentException("appCode 不能为空");
        }
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("path 不能为空");
        }
        // 基于接口固定盐值生成动态盐值
        DynamicSaltDTO salt = dynamicSaltService.generate(appCode, path);

        // 构建动态盐值响应
        DynamicSaltResponse response = new DynamicSaltResponse();
        response.setAppCode(salt.getAppCode());
        response.setPath(salt.getApiPath());
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
    public ApiResponse<Map<String, Object>> validateDynamicSalt(
            @RequestHeader(value = "appCode", required = false) String appCode,
            @RequestHeader(value = "path", required = false) String path,
            @RequestHeader(value = "dynamicSalt", required = false) String dynamicSalt,
            @RequestHeader(value = "dynamicSaltTime", required = false) Long dynamicSaltTime,
            @RequestBody DynamicSaltVerfiyRequest request) {

        if (appCode == null && request != null) {
            appCode = request.getAppCode();
        }
        if (path == null && request != null) {
            path = request.getPath();
        }
        if (dynamicSalt == null && request != null) {
            dynamicSalt = request.getDynamicSalt();
        }
        if (dynamicSaltTime == null && request != null) {
            dynamicSaltTime = request.getDynamicSaltTime();
        }

        boolean isValid;
        if (appCode == null || path == null || dynamicSalt == null || dynamicSaltTime == null) {
            isValid = false;
        } else {
            // 执行动态盐值校验
            DynamicSaltVerfiyRequest verfiyRequest = new DynamicSaltVerfiyRequest();
            verfiyRequest.setAppCode(appCode);
            verfiyRequest.setPath(path);
            verfiyRequest.setDynamicSalt(dynamicSalt);
            verfiyRequest.setDynamicSaltTime(dynamicSaltTime);
            DynamicSaltDTO dynamicSaltDTO = dynamicSaltMapper.toDTO(verfiyRequest);
            isValid = dynamicSaltService.validateDynamicSalt(dynamicSaltDTO);
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
     * - 通过 header 中的 withParams 显式指定是否携带参数生成签名
     *
     * @param withParams [header] 是否携带参数生成签名，true表示携带参数，false或不传表示不携带参数
     * @param parameters [可选] 携带参数生成签名
     * @return 生成的签名信息
     */
    @PostMapping("/generate")
    @IgnoreSignHeader
    public ApiResponse<SignResponse> generateSign(
            @RequestHeader(value = "appCode", required = false) String appCode,
            @RequestHeader(value = "path", required = false) String path,
            @RequestHeader(value = "dynamicSalt", required = false) String dynamicSalt,
            @RequestHeader(value = "dynamicSaltTime", required = false) Long dynamicSaltTime,
            @RequestHeader(value = "withParams", required = false, defaultValue = "false") Boolean withParams,
            @RequestBody(required = false) Map<String, Object> parameters) {

        SignRequest request = new SignRequest();
        request.setAppCode(appCode);
        request.setPath(path);
        request.setDynamicSalt(dynamicSalt);
        request.setDynamicSaltTime(dynamicSaltTime);
        request.setParameters(parameters);

        SignDTO dto;
        // 根据 header 中的 withParams 显式指定是否携带参数
        if (Boolean.TRUE.equals(withParams)) {
            dto = signService.generateWithParams(signMapper.toDTO(request), parameters);
        } else {
            dto = signService.generate(signMapper.toDTO(request));
        }
        SignResponse response = new SignResponse();
        response.setAppCode(dto.getAppCode());
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
     * - 通过 header 中的 withParams 显式指定是否携带参数校验
     *
     * @param withParams [header] 是否携带参数校验，true表示携带参数，false或不传表示不携带参数
     * @param parameters [可选] 携带参数校验
     * @return 校验结果及签名信息
     */
    @PostMapping("/sign-validate")
    public ApiResponse<Map<String, Object>> validateSign(
            @RequestHeader(value = "appCode", required = false) String appCode,
            @RequestHeader(value = "path", required = false) String path,
            @RequestHeader(value = "sign", required = false) String sign,
            @RequestHeader(value = "time", required = false) Long time,
            @RequestHeader(value = "withParams", required = false, defaultValue = "false") Boolean withParams,
            @RequestBody(required = false) Map<String, Object> parameters) {

        SignVerifyRequest request = new SignVerifyRequest();
        request.setAppCode(appCode);
        request.setPath(path);
        request.setSign(sign);
        request.setTime(time);
        request.setParameters(parameters);

        boolean isValid;
        if (appCode == null || path == null || sign == null || time == null) {
            isValid = false;
        } else {
            // 执行签名校验
            SignDTO signDTO = signMapper.toDTO(request);
            // 根据 header 中的 withParams 显式指定是否携带参数
            if (Boolean.TRUE.equals(withParams)) {
                isValid = signService.validateSignWithParams(signDTO, parameters);
            } else {
                isValid = signService.validateSign(signDTO);
            }
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
     * 获取调用方全部api列表
     * @param signHeaders sign请求头信息
     * @param withParams [header] 是否携带参数校验，true表示携带参数，false或不传表示不携带参数
     * @param parameters 其他参数【可选】
     * @return 请求结果
     */
    @RequestMapping(
            value = "/user-auth-list",
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    public ApiResponse<Object> userAuthList(
            @ModelAttribute("signHeaders") SignHeaderRequest signHeaders,
            @RequestHeader(value = "withParams", required = false, defaultValue = "false") Boolean withParams,
            @RequestBody(required = false) Map<String, Object> parameters) {
        log.info("完整headers：appCode={}, sign={}, time={}, path={}",
                signHeaders.getAppCode(), signHeaders.getSign(), signHeaders.getTime(), signHeaders.getPath());
        SignDTO signDTO = signMapper.toDTO(signHeaders);
        boolean isValid;
        // 根据 header 中的 withParams 显式指定是否携带参数
        if (Boolean.TRUE.equals(withParams)) {
            isValid = signService.validateSignWithParams(signDTO, parameters);
        } else {
            isValid = signService.validateSign(signDTO);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("parameters", parameters);
        response.put("request", signHeaders);
        if (isValid) {
            SignUserAuth signUserAuth = signService.getSignUserAuth(signDTO.getAppCode());
            // 脱敏 secretKey
            SignUserAuth maskedAuth = new SignUserAuth(
                    signUserAuth.appCode(),
                    "***",  // 脱敏 secretKey
                    signUserAuth.permitPaths(),
                    signUserAuth.forbiddenPath()
            );
            response.put("signUserAuth", maskedAuth);
            return ApiResponse.success(response, "请求提交成功。");
        } else {
            return ApiResponse.failure(HttpStatus.INTERNAL_SERVER_ERROR.value(), response, "签名校验失败。");
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
     * - 通过 header 中的 withParams 显式指定是否携带参数校验
     *
     * @param withParams [header] 是否携带参数校验，true表示携带参数，false或不传表示不携带参数
     * @return 提交结果
     * @header 包含 appCode, path, sign, time 签名请求
     */
    @PostMapping("/submit-test")
    public ApiResponse<Map<String, Object>> submitTest(
            @RequestHeader(value = "appCode", required = false) String appCode,
            @RequestHeader(value = "sign", required = false) String sign,
            @RequestHeader(value = "path", required = false) String path,
            @RequestHeader(value = "time", required = false) Long time,
            @RequestHeader(value = "withParams", required = false, defaultValue = "false") Boolean withParams,
            HttpServletRequest request,
            @RequestBody(required = false) Map<String, Object> parameters) {
        if (path == null) {
            path = request.getRequestURI().substring(request.getContextPath().length());
        }
        SignVerifyRequest signVerifyRequest = new SignVerifyRequest();
        signVerifyRequest.setAppCode(appCode);
        signVerifyRequest.setPath(path);
        signVerifyRequest.setSign(sign);
        signVerifyRequest.setTime(time);
        log.info("完整SignVerifyRequest={}", signVerifyRequest);
        SignDTO signDTO = signMapper.toDTO(signVerifyRequest);
        boolean isValid;
        // 根据 header 中的 withParams 显式指定是否携带参数
        if (Boolean.TRUE.equals(withParams)) {
            isValid = signService.validateSignWithParams(signDTO, parameters);
        } else {
            isValid = signService.validateSign(signDTO);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("isValid", isValid);
        response.put("parameters", parameters);
        if (isValid) {
            return ApiResponse.success(response, "带签名的请求提交成功。");
        } else {
            return ApiResponse.failure(HttpStatus.INTERNAL_SERVER_ERROR.value(), response, "带签名的请求校验失败。");
        }
    }
}