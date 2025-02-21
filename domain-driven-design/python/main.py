# main.py
from service.order_service import OrderService


def main():
    order_service = OrderService()

    while True:
        print("\n订单管理系统")
        print("1. 创建订单")
        print("2. 取消订单")
        print("3. 查询订单")
        print("4. 查看订单历史")
        print("5. 退出")
        choice = input("请选择操作: ")

        if choice == '1':
            id = int(input("请输入订单 ID: "))
            customer_name = input("请输入客户名称: ")
            amount = float(input("请输入订单金额: "))
            order_service.create_order(id, customer_name, amount)
        elif choice == '2':
            id = int(input("请输入要取消的订单 ID: "))
            order_service.cancel_order(id)
        elif choice == '3':
            id = int(input("请输入要查询的订单 ID: "))
            order_service.query_order(id)
        elif choice == '4':
            order_service.view_order_history()
        elif choice == '5':
            print("退出系统，清理资源...")
            break
        else:
            print("无效选择，请重新输入。")


if __name__ == "__main__":
    main()

"""
jarry@MacBook-Pro python % python main.py

订单管理系统
1. 创建订单
2. 取消订单
3. 查询订单
4. 查看订单历史
5. 退出
"""