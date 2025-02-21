# domain/order.py
class Order:
    def __init__(self, id, customer_name, amount):
        self.id = id
        self.customer_name = customer_name
        self.amount = amount
        self.status = 'CREATED'

    def cancel(self):
        if self.status == 'CREATED':
            self.status = 'CANCELED'
            print(f"订单 ID {self.id} 已取消")
        else:
            print(f"订单 ID {self.id} 已经取消，无法重复操作")

    def display(self):
        print(f"订单 ID: {self.id}")
        print(f"客户名称: {self.customer_name}")
        print(f"订单金额: {self.amount:.2f}")
        print(f"订单状态: {self.status}")
