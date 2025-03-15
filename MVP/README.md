# MVP 分层架构设计概述

模型-视图-展示器（Model-View-Presenter，简称 MVP）是 MVC 的演进架构模式，通过强化视图与业务的解耦实现更清晰的职责边界。MVP 适用于需要严格隔离界面交互与业务逻辑的复杂应用场景。

## MVP 结构图形示例
以跨平台客户端开发为例
```text
User Input  
    | 
    v        由主持人代理View和Model交互
+---------+      +-----------+       +-----------+
|  View   | <--> | Presenter | <---> |   Model   |
+---------+      +-----------+       +-----------+
```

## MVP 的例子（C、Java、JavaScript、Go、Python）
### [MVP架构设计源码详解](../mvx/mvp)
### [MVC架构设计源码详解](../mvx/mvc)
### [MVVM架构设计源码详解](../mvx/mvvm)
### [DDD架构设计源码详解](../domain-driven-design)