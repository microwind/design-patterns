# configuration-center

该示例聚焦微服务中的集中配置管理，统一演示 3 个动作：

1. 按 `serviceName + environment` 发布配置
2. 客户端首次加载配置快照
3. 配置更新后客户端刷新并读取新版本

统一业务语境：

- 服务：`order-service`
- 环境：`prod`
- 配置项：`dbHost`、`timeoutMs`、`featureOrderAudit`

## 目标

- 说明为什么配置不应散落在服务本地
- 说明配置中心如何提供“按服务、按环境”的视图
- 说明客户端如何通过刷新获得新版本配置

## 语言实现

- c
- go
- java
- js
- python
- ts
