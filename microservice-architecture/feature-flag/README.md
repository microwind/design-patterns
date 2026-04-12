# 【特性开关模式详解】C/Java/JS/Go/Python/TS不同语言实现

# 简介

特性开关（Feature Flag）是微服务架构中实现"功能发布与代码发布解耦"的核心模式。通过开关控制功能的启用/禁用，支持按用户定向启用（灰度发布），无需重新部署即可控制功能可见性。

# 作用

1. **发布解耦**：功能代码已部署但默认关闭，通过开关随时启用。
2. **灰度发布**：通过 allowlist 按用户定向启用，逐步放量验证。
3. **快速回滚**：发现问题时关闭开关即可，无需回滚代码。

# 实现步骤

1. 定义 FeatureFlag 配置：defaultEnabled（默认开关）+ allowlist（白名单用户）。
2. FeatureFlagService 管理多个开关的注册和查询。
3. enabled(flag, userId) 判断逻辑：白名单优先 → 默认值兜底 → 未注册返回 false。

# 判断流程

```text
  enabled(flag, userId)
    │
    ▼
  开关已注册？──否──► 返回 false
    │
    是
    ▼
  用户在白名单？──是──► 返回 true
    │
    否
    ▼
  返回 defaultEnabled
```

# 涉及的设计模式

| 设计模式 | 在本模块中的体现 |
|---|---|
| **策略模式（Strategy Pattern）** | 不同的开关配置代表不同的发布策略（全量开/全量关/灰度）。实际工程中还支持百分比放量、A/B 测试等策略。 |
| **观察者模式（Observer Pattern）** | 实际工程中开关变更会实时推送到客户端（如 LaunchDarkly 的 streaming）。本示例简化为直接查询。 |

# 与实际开源项目对比

| 对比维度 | 本示例 | 实际工程 |
|---|---|---|
| **存储** | 内存 Map | LaunchDarkly（SaaS）、Unleash（自建）、Flagsmith |
| **评估规则** | 白名单 + 默认值 | 百分比放量、用户属性匹配、A/B 测试、时间窗口 |
| **实时推送** | 无 | LaunchDarkly streaming / Unleash polling |
| **审计日志** | 无 | 开关变更历史、操作审计 |
| **SDK** | 手动查询 | 多语言 SDK + 本地缓存 + 断线兜底 |

> **整体思路一致**：开关注册 + 评估规则 + 默认兜底是所有实现的核心骨架。

# 测试验证

```bash
# Java
cd microservice-architecture/feature-flag/java
javac src/FeatureFlagPattern.java test/Test.java && java test.Test

# Go
cd microservice-architecture/feature-flag/go
go test ./...

# Python
cd microservice-architecture/feature-flag/python
python3 -m unittest discover -s test -p "test_*.py"

# JavaScript
cd microservice-architecture/feature-flag/js
node test/test_feature_flag.js

# TypeScript
cd microservice-architecture/feature-flag/ts
tsc -p . && node dist/test/test_feature_flag.js

# C
cd microservice-architecture/feature-flag/c
cc test/test.c src/*.c -o test.out && ./test.out
```
