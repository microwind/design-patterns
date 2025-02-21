## C语言`DDD`目录结构
```bash
c/
├── main.c                  // UI 层：应用程序的入口，处理用户交互
├── domain/                 // 领域层：包含核心业务模型和逻辑
│   ├── order.h             // 订单领域模型的头文件，定义订单属性和操作
│   └── order.c             // 订单领域模型的实现，包含订单创建、取消等功能
├── repository/             // 仓储层：管理数据存储和检索
│   ├── order_repository.h  // 订单仓储的头文件，声明订单持久化的函数
│   └── order_repository.c  // 订单仓储的实现，处理订单的保存和检索
└── service/                // 服务层：提供高层业务逻辑
    ├── order_service.h     // 订单服务的头文件，声明订单操作的服务函数
    └── order_service.c     // 订单服务的实现，协调领域和仓储操作
```


## 解释

- **`main.c`**: 该文件作为用户界面层，负责与用户交互，捕获输入并显示输出。它调用服务层来根据用户输入执行操作。

- **`domain/`**: 该目录包含领域层，是业务逻辑的核心。包括：
  - `order.h`: 定义 `Order` 结构体及其相关操作，如创建和取消订单。
  - `order.c`: 实现 `order.h` 中声明的函数，处理订单管理的核心逻辑。

- **`repository/`**: 该目录代表仓储层，抽象数据存储机制。包括：
  - `order_repository.h`: 声明用于保存和检索订单的函数。
  - `order_repository.c`: 实现仓储函数，管理订单数据的存储和检索。

- **`service/`**: 该目录包含服务层，提供高层次的业务服务。包括：
  - `order_service.h`: 声明协调领域和仓储层操作的服务函数。
  - `order_service.c`: 实现服务函数，组织业务逻辑和数据访问。

## 运行代码
```shell
$ c % make clean
rm -f main.o domain/order.o repository/order_repository.o service/order_service.o order_system.o
$ c % make
gcc -Wall -Wextra -I./domain -I./repository -I./service   -c -o main.o main.c
gcc -Wall -Wextra -I./domain -I./repository -I./service   -c -o domain/order.o domain/order.c
gcc -Wall -Wextra -I./domain -I./repository -I./service   -c -o repository/order_repository.o repository/order_repository.c
gcc -Wall -Wextra -I./domain -I./repository -I./service   -c -o service/order_service.o service/order_service.c
gcc main.o domain/order.o repository/order_repository.o service/order_service.o -o order_system.o
$ c % ./order_system.o 
# 展示结果
```