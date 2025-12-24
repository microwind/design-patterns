package com.microwind.knife.interfaces.vo.sign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 签名请求头 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignHeaderRequest {

    /**
     * 应用编码
     */
    private String appCode;

    /**
     * 签名值
     */
    private String sign;

    /**
     * 时间戳（毫秒）
     */
    private Long time;

    /**
     * 接口路径[可选]
     * 调用方计算时需增加接口路径，但计算时通过接口获取路径
     */
    private String path;

    /**
     * 验证必填字段
     */
    public boolean isValid() {
        return appCode != null && !appCode.isEmpty()
                && sign != null && !sign.isEmpty()
                && time != null;
    }
}