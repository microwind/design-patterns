package com.microwind.knife.interfaces.vo.sign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

// 提交请求
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignVerifyRequest {
    private String appCode;
    private String path;
    private String sign;
    private Long time;

    // 参数可选
    private Map<String, Object> parameters;

    public boolean isValid() {
        return appCode != null && !appCode.isEmpty()
                && sign != null && !sign.isEmpty()
                && time != null;
    }
}

