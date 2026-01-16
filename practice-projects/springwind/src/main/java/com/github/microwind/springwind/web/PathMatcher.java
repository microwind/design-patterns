package com.github.microwind.springwind.web;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 路径匹配器 - 支持 RESTful 路径参数
 * 例如: /user/{id} 可以匹配 /user/123
 */
public class PathMatcher {

    private final String pattern;
    private final Pattern regex;
    private final java.util.List<String> variableNames;

    /**
     * 构造路径匹配器
     * @param pattern 路径模式，如 "/user/{id}"
     */
    public PathMatcher(String pattern) {
        this.pattern = pattern;
        this.variableNames = new java.util.ArrayList<>();
        this.regex = compilePattern(pattern);
    }

    /**
     * 将路径模式编译为正则表达式
     * 例如: /user/{id} -> ^/user/([^/]+)$
     */
    private Pattern compilePattern(String pattern) {
        StringBuilder regexBuilder = new StringBuilder("^");
        int start = 0;

        // 查找所有的 {variableName} 占位符
        while (start < pattern.length()) {
            int openBrace = pattern.indexOf('{', start);
            if (openBrace == -1) {
                // 没有更多占位符，添加剩余的字面量部分
                regexBuilder.append(Pattern.quote(pattern.substring(start)));
                break;
            }

            // 添加占位符之前的字面量部分
            if (openBrace > start) {
                regexBuilder.append(Pattern.quote(pattern.substring(start, openBrace)));
            }

            // 找到对应的右花括号
            int closeBrace = pattern.indexOf('}', openBrace);
            if (closeBrace == -1) {
                throw new IllegalArgumentException("路径模式格式错误，缺少 }: " + pattern);
            }

            // 提取变量名
            String variableName = pattern.substring(openBrace + 1, closeBrace);
            variableNames.add(variableName);

            // 添加匹配组（匹配非斜杠字符）
            regexBuilder.append("([^/]+)");

            start = closeBrace + 1;
        }

        regexBuilder.append("$");
        return Pattern.compile(regexBuilder.toString());
    }

    /**
     * 检查请求路径是否匹配此模式
     */
    public boolean matches(String requestPath) {
        return regex.matcher(requestPath).matches();
    }

    /**
     * 从请求路径中提取路径变量
     * @param requestPath 实际请求路径，如 "/user/123"
     * @return 路径变量映射，如 {"id": "123"}
     */
    public Map<String, String> extractPathVariables(String requestPath) {
        Map<String, String> variables = new HashMap<>();
        Matcher matcher = regex.matcher(requestPath);

        if (!matcher.matches()) {
            return variables;
        }

        for (int i = 0; i < variableNames.size(); i++) {
            String value = matcher.group(i + 1);
            variables.put(variableNames.get(i), value);
        }

        return variables;
    }

    /**
     * 检查此模式是否包含路径变量
     */
    public boolean hasPathVariables() {
        return !variableNames.isEmpty();
    }

    public String getPattern() {
        return pattern;
    }

    public java.util.List<String> getVariableNames() {
        return new java.util.ArrayList<>(variableNames);
    }
}
