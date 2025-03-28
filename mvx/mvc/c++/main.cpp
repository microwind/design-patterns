/* 主程序（Main） */
// main.cpp
#include "model/Order.h"
#include "view/OrderView.h"
#include "controller/OrderController.h"
#include "model/OrderRepository.h"
#include <memory>
#include <iostream>

int main()
{
    // 初始化组件（主程序仅通过 Controller 与其他层交互）
    OrderView view;
    OrderRepository repository;
    OrderController controller(view, repository);

    // 通过 Controller 创建订单
    controller.createOrder(1001, "ORD-001", "Customer101", 2500.0);

    // 显示初始状态
    controller.refreshView("ORD-001");

    // 更新操作：更新订单"ORD-001"的金额和客户
    controller.updateAmount("ORD-001", 3000.0);
    controller.updateCustomer("ORD-001", "Customer102");
    controller.refreshView("ORD-001");

    // 通过 Controller 创建另一订单
    controller.createOrder(1002, "ORD-002", "Customer201", 15000.0);
    controller.refreshView("ORD-002");

    std::cout << "\nAll orders before deletion:" << std::endl;
    controller.listAllOrders();

    // 删除订单
    std::cout << "\nDeleting order ORD-001..." << std::endl;
    controller.deleteOrder("ORD-001");

    std::cout << "\nAll orders after deletion:" << std::endl;
    controller.listAllOrders();

    return 0;
}

// $ 编译命令（兼容旧标准）
// $ g++ -std=c++03 -I. model/*.cpp view/*.cpp controller/*.cpp main.cpp -o mvc_app
// $
// # 使用C++11标准编译（自动支持连续右尖括号）
// $ g++ -std=c++11 -I. model/*.cpp view/*.cpp controller/*.cpp main.cpp -o mvc_app

/*
jarry@MacBook-Pro c++ % g++ -std=c++11 -I. model/*.cpp view/*.cpp controller/*.cpp main.cpp -o mvc_app
jarry@MacBook-Pro c++ % ./mvc_app
jarry@MacBook-Pro c++ % ./mvc_app 
=== Order Details ===
ID:     1001
OrderNo:        ORD-001
Customer:       Customer101
Amount: $2500

=== Order Details ===
ID:     1001
OrderNo:        ORD-001
Customer:       Customer102
Amount: $3000

=== Order Details ===
ID:     1002
OrderNo:        ORD-002
Customer:       Customer201
Amount: $15000


All orders before deletion:
=== Order Details ===
ID:     1001
OrderNo:        ORD-001
Customer:       Customer102
Amount: $3000

=== Order Details ===
ID:     1002
OrderNo:        ORD-002
Customer:       Customer201
Amount: $15000


Deleting order ORD-001...
Order ORD-001 deleted successfully.

All orders after deletion:
=== Order Details ===
ID:     1002
OrderNo:        ORD-002
Customer:       Customer201
Amount: $15000
 */