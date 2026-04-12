# 【配置中心模式详解】C/Java/JS/Go/Python/TS不同语言实现

# 简介

配置中心（Configuration Center）是微服务架构中解决"配置如何集中管理"的核心模式。当系统拆分为多个服务后，配置散落在各服务的本地文件中会导致管理混乱、环境差异难以控制、配置变更需要重新部署。配置中心将所有配置集中存储，按**服务名 + 环境**维度管理，支持客户端运行时刷新。

# 作用

1. **集中管理**：所有服务的配置统一存储在配置中心，避免配置散落。
2. **环境隔离**：按环境（dev/staging/prod）提供不同的配置视图。
3. **动态刷新**：配置变更后客户端可运行时刷新，无需重新发版部署。

# 实现步骤

1. 定义配置数据结构（ServiceConfig），包含服务名、环境、版本号和业务配置项。
2. 实现配置中心（ConfigCenter），提供 put / get 方法，按 `serviceName@environment` 键存储。
3. 实现配置客户端（ConfigClient），绑定特定服务和环境，提供 load / refresh / current 方法。
4. 测试验证：发布配置 → 客户端加载 → 更新配置 → 客户端刷新 → 验证版本号变化。

# 架构图

```text
  运维/平台 ──put──► ConfigCenter（配置中心）
                         │
                    serviceName@env 键值存储
                         │
                    get / refresh
                         │
  ConfigClient ◄─────────┘
  (绑定特定服务+环境)
       │
    current() 获取本地缓存
       │
    应用使用配置
```

# 涉及的设计模式

| 设计模式 | 在本模块中的体现 |
|---|---|
| **观察者模式（Observer Pattern）** | 实际工程中配置变更会推送通知到客户端（如 Nacos 的 push 机制）。本示例简化为客户端主动 refresh（pull 模式）。 |
| **单例模式（Singleton Pattern）** | ConfigCenter 在系统中通常只有一个实例。实际工程中通过 Spring Bean / 全局变量实现。 |
| **代理模式（Proxy Pattern）** | ConfigClient 代理了对 ConfigCenter 的访问，本地缓存当前配置快照，减少远程调用。 |

# 与实际开源项目对比

| 对比维度 | 本示例 | 实际工程 |
|---|---|---|
| **存储** | 内存 Map/Dict | Apollo（MySQL）、Nacos（MySQL + 内存）、Spring Cloud Config（Git） |
| **通知机制** | 客户端主动 refresh | Nacos（长连接 push）、Apollo（长轮询 + 推送）、etcd（watch） |
| **版本管理** | 手动设置 version 字段 | Apollo 自动版本号 + 灰度发布 + 回滚 |
| **权限控制** | 无 | Apollo 命名空间权限、Nacos 命名空间隔离 |
| **多格式支持** | 固定结构体字段 | 支持 YAML / JSON / Properties / TOML 等格式 |
| **集群高可用** | 单节点 | 多节点集群 + 数据同步 |

> **整体思路一致**：配置中心（存储） + 客户端（加载/刷新/缓存）是所有实现的核心骨架。

# 统一业务语境

```
服务：order-service
环境：prod
配置项：dbHost、timeoutMs、featureOrderAudit
流程：发布 V1 → 加载 → 更新 V2 → 刷新 → 验证版本变化
```

# 测试验证

```bash
# Java
cd microservice-architecture/configuration-center/java
javac src/ConfigurationCenter.java test/Test.java && java test.Test

# Go
cd microservice-architecture/configuration-center/go
go test ./...

# Python
cd microservice-architecture/configuration-center/python
python3 -m unittest discover -s test -p "test_*.py"

# JavaScript
cd microservice-architecture/configuration-center/js
node test/test_configuration_center.js

# TypeScript
cd microservice-architecture/configuration-center/ts
tsc -p . && node dist/test/test_configuration_center.js

# C
cd microservice-architecture/configuration-center/c
cc test/test.c src/*.c -o test.out && ./test.out
```
