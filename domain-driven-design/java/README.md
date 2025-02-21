## Java语言`DDD`目录结构
```bash
java/
├── Main.java                           // 应用的入口点，负责初始化并运行服务
├── domain/                             // 领域层，包含核心的业务模型和逻辑
│    └── Order.java                     // 订单的领域模型，定义订单的属性和行为
├── repository/                         // 仓储层，负责与数据源进行交互
│    └── OrderRepository.java           // 订单的仓储实现，负责保存和检索订单
└── service/                            // 服务层，提供高层业务逻辑
     └── OrderService.java              // 订单服务，协调领域模型和仓储操作，提供业务处理功能
```

## 解释：

  **main.java:** 作为应用的入口点，负责启动应用，创建服务层实例并调用其方法，通常会初始化必要的依赖项。
  **domain/:**  包含核心的业务模型（如 order.java），负责定义与业务相关的领域模型和领域逻辑，强调业务的本质。
  **repository/:**  包含与数据源交互的代码（如 order_repository.java），负责数据的持久化和检索，隔离具体的数据存储细节。
  **service/:** 提供高层的业务服务（如 order_service.java），负责组织和调度领域模型与仓储的操作，向外界提供统一的业务接口。

## 运行
```bash
$ java main.java
# 展示结果
```