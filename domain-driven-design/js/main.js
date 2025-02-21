// 应用的入口点，负责初始化并运行服务
// main.js
import {
    OrderService
} from './service/order_service.js';
import readline from 'readline';

// 创建 readline 接口实例
const terminal = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});

// 初始化订单服务
const orderService = new OrderService();

// 显示菜单
function showMenu() {
    console.log("\n订单管理系统");
    console.log("1. 创建订单");
    console.log("2. 取消订单");
    console.log("3. 查询订单");
    console.log("4. 查看订单历史");
    console.log("5. 退出");
    terminal.question("请选择操作: ", handleUserInput);
}

// 处理用户输入
function handleUserInput(choice) {
    switch (choice.trim()) {
        case '1':
            terminal.question("请输入订单 ID: ", (id) => {
                terminal.question("请输入客户名称: ", (customerName) => {
                    terminal.question("请输入订单金额: ", (amount) => {
                        orderService.createOrder(parseInt(id), customerName, parseFloat(amount));
                        showMenu();
                    });
                });
            });
            break;
        case '2':
            terminal.question("请输入要取消的订单 ID: ", (id) => {
                orderService.cancelOrder(parseInt(id));
                showMenu();
            });
            break;
        case '3':
            terminal.question("请输入要查询的订单 ID: ", (id) => {
                orderService.queryOrder(parseInt(id));
                showMenu();
            });
            break;
        case '4':
            orderService.viewOrderHistory();
            showMenu();
            break;
        case '5':
            console.log("退出系统，清理资源...");
            terminal.close();
            break;
        default:
            console.log("无效选择，请重新输入。");
            showMenu();
    }
}

// 启动程序
showMenu();

/*
jarry@MacBook-Pro js % node main.js

订单管理系统
1. 创建订单
2. 取消订单
3. 查询订单
4. 查看订单历史
5. 退出
请选择操作: 
*/