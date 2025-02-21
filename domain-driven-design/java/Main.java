
// Main.java
import service.OrderService;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        OrderService orderService = new OrderService();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n订单管理系统");
            System.out.println("1. 创建订单");
            System.out.println("2. 取消订单");
            System.out.println("3. 查询订单");
            System.out.println("4. 查看订单历史");
            System.out.println("5. 退出");
            System.out.print("请选择操作: ");

            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    System.out.print("请输入订单 ID: ");
                    int id = scanner.nextInt();
                    System.out.print("请输入客户名称: ");
                    String customerName = scanner.next();
                    System.out.print("请输入订单金额: ");
                    double amount = scanner.nextDouble();
                    orderService.createOrder(id, customerName, amount);
                    break;

                case 2:
                    System.out.print("请输入要取消的订单 ID: ");
                    id = scanner.nextInt();
                    orderService.cancelOrder(id);
                    break;

                case 3:
                    System.out.print("请输入要查询的订单 ID: ");
                    id = scanner.nextInt();
                    orderService.queryOrder(id);
                    break;

                case 4:
                    orderService.viewOrderHistory();
                    break;

                case 5:
                    System.out.println("退出系统，清理资源...");
                    scanner.close();
                    return;

                default:
                    System.out.println("无效选择，请重新输入。");
            }
        }
    }
}

/*
 * jarry@MacBook-Pro java % java Main.java
 * 
 * 订单管理系统
 * 1. 创建订单
 * 2. 取消订单
 * 3. 查询订单
 * 4. 查看订单历史
 * 5. 退出
 * 请选择操作:
 */