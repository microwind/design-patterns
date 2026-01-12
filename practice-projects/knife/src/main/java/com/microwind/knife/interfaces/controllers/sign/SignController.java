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
import com.microwind.knife.interfaces.annotation.RequireSign;
import com.microwind.knife.interfaces.annotation.WithParams;
import com.microwind.knife.interfaces.vo.sign.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.HttpStatus;
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
     * 生成规则：基于 appCode + path + interfaceSalt + timestamp 生成动态盐值
     * 如果配置了数据库校验，将盐值保存到数据库
     * <p>
     * 参数传递：支持通过 Header 或 Body 传参，Header 优先
     *
     * @param appCode 应用标识（Header 或 Body）
     * @param path    接口路径（Header 或 Body）
     * @param request 请求体（可选）
     * @return 生成的动态盐值信息
     */
    @PostMapping("/dynamic-salt-generate")
    @IgnoreSignHeader
    public ApiResponse<DynamicSaltResponse> generateDynamicSalt(
            @RequestHeader(value = SignConfig.HEADER_APP_CODE, required = false) String appCode,
            @RequestHeader(value = SignConfig.HEADER_PATH, required = false) String path,
            @RequestBody(required = false) DynamicSaltRequest request) {

        // Header 优先，Body 备用
        if (appCode == null && request != null) appCode = request.getAppCode();
        if (path == null && request != null) path = request.getPath();

        // 参数校验
        if (appCode == null || path == null) {
            log.error("动态盐值生成失败: 参数不完整 - appCode={}, path={}", appCode, path);
            throw new IllegalArgumentException("appCode 或 path 不能为空");
        }

        // 生成动态盐值
        DynamicSaltDTO salt = dynamicSaltService.generate(appCode, path);

        // 构建响应
        DynamicSaltResponse response = new DynamicSaltResponse();
        response.setAppCode(salt.getAppCode());
        response.setPath(salt.getApiPath());
        response.setDynamicSalt(salt.getDynamicSalt());
        response.setDynamicSaltTime(salt.getSaltTimestamp());

        String message = signConfig.isValidateDynamicSaltFromDatabase()
                ? "动态盐值创建并保存成功。"
                : "动态盐值生成成功。";
        return ApiResponse.success(response, message);
    }

    /**
     * 动态盐值校验接口
     * <p>
     * 功能：验证客户端提供的动态盐值是否有效
     * <p>
     * 校验规则：
     * 1. 参数完整性校验
     * 2. 数据库模式：查询数据库中的盐值记录，验证后标记为已使用（防重放攻击）
     * 3. 算法模式：基于 appCode + path + interfaceSalt + timestamp 重新计算并比对
     * 4. TTL 时间窗口检查：盐值必须在有效期内
     * <p>
     * 参数传递：支持通过 Header 或 Body 传参，Header 优先
     * <p>
     * 约定说明：path 必须为接口模板路径（如 /api/users 或 /api/users/{userId}）
     *
     * @param appCode         应用标识（Header 或 Body）
     * @param path            接口路径（Header 或 Body）
     * @param dynamicSalt     动态盐值（Header 或 Body）
     * @param dynamicSaltTime 盐值时间戳（Header 或 Body）
     * @param request         请求体（可选）
     * @return 校验结果及动态盐值信息
     */
    @PostMapping("/dynamic-salt-validate")
    @IgnoreSignHeader
    public ApiResponse<Map<String, Object>> validateDynamicSalt(
            @RequestHeader(value = SignConfig.HEADER_APP_CODE, required = false) String appCode,
            @RequestHeader(value = SignConfig.HEADER_PATH, required = false) String path,
            @RequestHeader(value = SignConfig.HEADER_DYNAMIC_SALT, required = false) String dynamicSalt,
            @RequestHeader(value = SignConfig.HEADER_DYNAMIC_SALT_TIME, required = false) Long dynamicSaltTime,
            @RequestBody(required = false) DynamicSaltVerfiyRequest request) {

        // Header 优先，Body 备用
        if (request != null) {
            if (appCode == null) appCode = request.getAppCode();
            if (path == null) path = request.getPath();
            if (dynamicSalt == null) dynamicSalt = request.getDynamicSalt();
            if (dynamicSaltTime == null) dynamicSaltTime = request.getDynamicSaltTime();
        }

        // 参数校验并执行盐值校验
        boolean isValid = false;
        if (appCode == null || path == null || dynamicSalt == null || dynamicSaltTime == null) {
            log.warn("动态盐值校验失败: 参数不完整 - appCode={}, path={}, dynamicSalt={}, dynamicSaltTime={}",
                    appCode, path, dynamicSalt != null, dynamicSaltTime);
            return ApiResponse.failure(HttpStatus.SC_BAD_REQUEST, "动态盐值校验失败，参数不正确。");
        } else {
            DynamicSaltDTO dto = DynamicSaltDTO.builder()
                    .appCode(appCode)
                    .apiPath(path)
                    .dynamicSalt(dynamicSalt)
                    .saltTimestamp(dynamicSaltTime)
                    .build();
            isValid = dynamicSaltService.validateDynamicSalt(dto);

            if (!isValid) {
                log.warn("动态盐值校验失败: appCode={}, path={}, dynamicSalt={}", appCode, path, dynamicSalt);
                return ApiResponse.failure(HttpStatus.SC_BAD_REQUEST, "动态盐值校验失败。");
            }
        }

        // 构建响应
        Map<String, Object> response = new HashMap<>();
        response.put("isValid", isValid);

        if (isValid) {
            // 校验成功才返回盐值信息（避免泄露敏感信息）
            DynamicSaltResponse saltInfo = new DynamicSaltResponse();
            saltInfo.setAppCode(appCode);
            saltInfo.setPath(path);
            saltInfo.setDynamicSalt(dynamicSalt);
            saltInfo.setDynamicSaltTime(dynamicSaltTime);
            response.put("dynamicSalt", saltInfo);
            return ApiResponse.success(response, "动态盐值校验成功。");
        } else {
            return ApiResponse.failure(HttpStatus.SC_BAD_REQUEST, response, "动态盐值校验失败。");
        }
    }

    /**
     * 签名生成接口
     * <p>
     * 功能：为已通过动态盐值校验的请求生成签名
     * <p>
     * 生成规则：基于 appCode + secretKey + path + timestamp 生成签名
     * 支持带参数或不带参数两种模式
     * <p>
     * 参数传递：支持通过 Header 或 Body 传参，Header 优先
     * <p>
     * 约定说明：
     * - 必须先调用 /dynamic-salt-generate 获取动态盐值
     * - dynamicSalt 和 dynamicSaltTime 必须与生成时保持一致
     * - 通过 withParams 参数指定是否携带业务参数生成签名
     *
     * @param appCode         应用标识（Header 或 Body）
     * @param path            接口路径（Header 或 Body）
     * @param dynamicSalt     动态盐值（Header 或 Body）
     * @param dynamicSaltTime 盐值时间戳（Header 或 Body）
     * @param withParams      是否携带参数生成签名（Header）
     * @param body            请求体，可包含 params 参数（可选）
     * @return 生成的签名信息
     */
    @PostMapping("/generate")
    @IgnoreSignHeader
    public ApiResponse<SignResponse> generateSign(
            @RequestHeader(value = SignConfig.HEADER_APP_CODE, required = false) String appCode,
            @RequestHeader(value = SignConfig.HEADER_PATH, required = false) String path,
            @RequestHeader(value = SignConfig.HEADER_DYNAMIC_SALT, required = false) String dynamicSalt,
            @RequestHeader(value = SignConfig.HEADER_DYNAMIC_SALT_TIME, required = false) Long dynamicSaltTime,
            @RequestHeader(value = SignConfig.HEADER_WITH_PARAMS, required = false, defaultValue = "false") Boolean withParams,
            @RequestBody(required = false) Map<String, Object> body) {

        // Header 优先，Body 备用
        if (body != null) {
            if (appCode == null && body.get("appCode") != null) {
                appCode = String.valueOf(body.get("appCode"));
            }
            if (path == null && body.get("path") != null) {
                path = String.valueOf(body.get("path"));
            }
            if (dynamicSalt == null && body.get("dynamicSalt") != null) {
                dynamicSalt = String.valueOf(body.get("dynamicSalt"));
            }
            if (dynamicSaltTime == null && body.get("dynamicSaltTime") != null) {
                try {
                    Object timeObj = body.get("dynamicSaltTime");
                    dynamicSaltTime = timeObj instanceof Number
                            ? ((Number) timeObj).longValue()
                            : Long.valueOf(timeObj.toString());
                } catch (NumberFormatException e) {
                    log.error("签名生成失败: dynamicSaltTime 格式错误 - {}", body.get("dynamicSaltTime"), e);
                    return ApiResponse.failure(HttpStatus.SC_BAD_REQUEST, "dynamicSaltTime 格式错误");
                }
            }
            if (body.get("withParams") != null) {
                withParams = Boolean.valueOf(body.get("withParams").toString());
            }
        }

        // 参数校验
        if (appCode == null || path == null || dynamicSalt == null || dynamicSaltTime == null) {
            log.error("签名生成失败: 缺少必要参数 - appCode={}, path={}, dynamicSalt={}, dynamicSaltTime={}",
                    appCode, path, dynamicSalt != null, dynamicSaltTime);
            return ApiResponse.failure(HttpStatus.SC_BAD_REQUEST, "缺少必要参数");
        }

        // 构建签名请求
        SignRequest request = new SignRequest();
        request.setAppCode(appCode);
        request.setPath(path);
        request.setDynamicSalt(dynamicSalt);
        request.setDynamicSaltTime(dynamicSaltTime);

        // 提取业务参数（如果有）
        if (Boolean.TRUE.equals(withParams)) {
            Object paramsObj = body.get("params");
            if (paramsObj instanceof Map) {
                request.setParameters((Map<String, Object>) paramsObj);
            }
        }

        // 生成签名（根据 withParams 决定是否包含业务参数）
        SignDTO dto = Boolean.TRUE.equals(withParams)
                ? signService.generateWithParams(signMapper.toDTO(request), request.getParameters())
                : signService.generate(signMapper.toDTO(request));

        // 构建响应
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
     * 1. 参数完整性校验
     * 2. 根据配置模式（本地配置或数据库）获取应用秘钥
     * 3. 算法校验：基于 appCode + secretKey + path + timestamp 重新计算并比对
     * 4. TTL 时间窗口检查：签名必须在有效期内
     * <p>
     * 参数传递：支持通过 Header 或 Body 传参
     * <p>
     * 约定说明：
     * - path 必须为接口模板路径（如 /api/users 或 /api/users/{userId}）
     * - 通过 withParams 参数指定是否携带业务参数校验
     *
     * @param appCode    应用标识（Header）
     * @param path       接口路径（Header）
     * @param sign       签名值（Header）
     * @param time       签名时间戳（Header）
     * @param withParams 是否携带参数校验（Header）
     * @param body       请求体，可包含 params 参数（可选）
     * @return 校验结果及签名信息
     */
    @PostMapping("/sign-validate")
    @IgnoreSignHeader
    public ApiResponse<Map<String, Object>> validateSign(
            @RequestHeader(value = SignConfig.HEADER_APP_CODE, required = false) String appCode,
            @RequestHeader(value = SignConfig.HEADER_PATH, required = false) String path,
            @RequestHeader(value = SignConfig.HEADER_SIGN, required = false) String sign,
            @RequestHeader(value = SignConfig.HEADER_TIME, required = false) Long time,
            @RequestHeader(value = SignConfig.HEADER_WITH_PARAMS, required = false, defaultValue = "false") Boolean withParams,
            @RequestBody(required = false) Map<String, Object> body) {

        // 构建签名校验请求
        SignVerifyRequest request = new SignVerifyRequest();
        request.setAppCode(appCode);
        request.setPath(path);
        request.setSign(sign);
        request.setTime(time);

        // 提取业务参数（如果有）
        if (body != null) {
            Object paramsObj = body.get("params");
            if (paramsObj instanceof Map) {
                request.setParameters((Map<String, Object>) paramsObj);
            }
        }

        // 参数校验并执行签名校验
        boolean isValid = false;
        if (appCode == null || path == null || sign == null || time == null) {
            log.warn("签名校验失败: 参数不完整 - appCode={}, path={}, sign={}, time={}",
                    appCode, path, sign != null, time);
        } else {
            SignDTO signDTO = signMapper.toDTO(request);
            // 根据 withParams 决定是否包含业务参数校验
            isValid = Boolean.TRUE.equals(withParams)
                    ? signService.validateSignWithParams(signDTO, request.getParameters())
                    : signService.validateSign(signDTO);

            if (!isValid) {
                log.warn("签名校验失败: appCode={}, path={}, sign={}, time={}", appCode, path, sign, time);
            }
        }

        // 构建响应
        Map<String, Object> response = new HashMap<>();
        response.put("isValid", isValid);

        if (isValid) {
            // 校验成功才返回签名信息（避免泄露敏感信息）
            SignResponse signInfo = new SignResponse();
            signInfo.setAppCode(appCode);
            signInfo.setPath(path);
            signInfo.setSign(sign);
            signInfo.setTime(time);
            response.put("sign", signInfo);
            return ApiResponse.success(response, "签名校验成功。");
        } else {
            return ApiResponse.failure(HttpStatus.SC_BAD_REQUEST, response, "签名校验失败。");
        }
    }

    /**
     * 获取调用方 API 权限列表
     * <p>
     * 需要携带有效签名才能访问（由 @RequireSign 拦截器自动校验）
     * 返回结果中的 secretKey 已脱敏处理
     *
     * @param signHeaders 签名请求头信息（由拦截器注入）
     * @param body        其他参数（可选）
     * @return API 权限列表
     */
    @RequireSign
    @RequestMapping(value = "/user-auth-list", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResponse<Object> userAuthList(
            @ModelAttribute("SignHeaders") SignHeaderRequest signHeaders,
            @RequestBody(required = false) Map<String, Object> body) {

        log.info("获取API权限列表: appCode={}, path={}", signHeaders.getAppCode(), signHeaders.getPath());

        SignDTO signDTO = signMapper.toDTO(signHeaders);
        SignUserAuth signUserAuth = signService.getSignUserAuth(signDTO.getAppCode());

        // 脱敏处理：隐藏 secretKey
        SignUserAuth maskedAuth = new SignUserAuth(
                signUserAuth.appCode(),
                "***",
                signUserAuth.permitPaths(),
                signUserAuth.forbiddenPath()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("body", body);
        response.put("request", signHeaders);
        response.put("signUserAuth", maskedAuth);

        return ApiResponse.success(response, "请求提交成功。");
    }

    /**
     * 带签名的数据提交测试接口
     * <p>
     * 测试签名验证流程，不包含业务参数签名
     * 签名验证由 @RequireSign 拦截器自动完成
     * <p>
     * 使用说明：
     * - 此为测试接口，其他业务接口可参考此实现
     * - 必须携带有效的签名才能提交数据
     *
     * @param signHeaders 签名请求头信息（由拦截器注入）
     * @param request     原始请求对象
     * @param parameters  业务参数（可选）
     * @return 提交结果
     */
    @RequireSign(withParams = WithParams.FALSE)
    @PostMapping("/submit-test")
    public ApiResponse<Map<String, Object>> submitTest(
            @ModelAttribute("SignHeaders") SignHeaderRequest signHeaders,
            HttpServletRequest request,
            @RequestBody(required = false) Map<String, Object> parameters) {

        log.info("签名测试提交: appCode={}, path={}", signHeaders.getAppCode(), signHeaders.getPath());

        Map<String, Object> response = new HashMap<>();
        response.put("isValid", true);  // 能到这里说明签名已通过验证
        response.put("parameters", parameters);

        return ApiResponse.success(response, "带签名的请求提交成功。");
    }
}