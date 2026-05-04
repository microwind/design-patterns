# Flask MVC 架构实现
## Flask 实现 MVC 架构的核心分层：

```text
  Model（数据模型） → Controller（控制器） → View（视图/响应）
```

**Model：** 负责定义数据结构和业务逻辑，使用 SQLAlchemy ORM 与数据库交互
**Controller：** 处理 HTTP 请求，调用服务层，返回响应
**View：** 在 API 场景中通常是 JSON 响应；在 Web 场景中是模板渲染