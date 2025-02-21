// UI 层（用户界面） - main.c
#include <stdio.h>
#include "./service/order_service.h"
#include "./repository/order_repository.h"

int main()
{
    int choice, id;
    char customerName[100];
    double amount;

    while (1)
    {
        printf("\n订单管理系统\n");
        printf("1. 创建订单\n");
        printf("2. 取消订单\n");
        printf("3. 查询订单\n");
        printf("4. 查看订单历史\n");
        printf("5. 退出\n");
        printf("请选择操作: ");
        scanf("%d", &choice);

        switch (choice)
        {
        case 1:
            printf("请输入订单 ID: ");
            scanf("%d", &id);
            printf("请输入客户名称: ");
            scanf("%99s", customerName);
            printf("请输入订单金额: ");
            scanf("%lf", &amount);
            createOrderService(id, customerName, amount);
            break;

        case 2:
            printf("请输入要取消的订单 ID: ");
            scanf("%d", &id);
            cancelOrderService(id);
            break;

        case 3:
            printf("请输入要查询的订单 ID: ");
            scanf("%d", &id);
            queryOrderService(id);
            break;

        case 4:
            viewOrderHistory();
            break;

        case 5:
            printf("退出系统，清理资源...\n");
            clearOrders(); // 释放所有订单
            return 0;

        default:
            printf("无效选择，请重新输入。\n");
        }
    }
}

/*
jarry@MacBook-Pro c % gcc -v      
Apple clang version 16.0.0 (clang-1600.0.26.6)
Target: arm64-apple-darwin24.3.0
Thread model: posix
InstalledDir: /Library/Developer/CommandLineTools/usr/bin
jarry@MacBook-Pro c % make clean
jarry@MacBook-Pro c % make                           
gcc main.o domain/order.o repository/order_repository.o service/order_service.o -o order_system.o
jarry@MacBook-Pro c % ./order_system.o 

订单管理系统
1. 创建订单
2. 取消订单
3. 查询订单
4. 查看订单历史
5. 退出

请选择操作: 1
请输入订单 ID: 3
请输入客户名称: Tom
请输入订单金额: 234
订单 ID 3 创建成功
*/