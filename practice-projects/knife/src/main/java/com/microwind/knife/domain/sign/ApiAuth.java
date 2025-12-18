package com.microwind.knife.domain.sign;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ApiAuth {
    private String appCode;           // 调用方身份
    private String appSecret;        // 调用方秘钥
    private List<String> permitPaths; // 允许访问的接口路径
    private List<String> forbiddenPath; // 禁止访问的几口路径，禁止的优与允许的
}