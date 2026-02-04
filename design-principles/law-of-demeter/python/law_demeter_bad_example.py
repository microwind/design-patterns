"""
迪米特原则 - 反例
这个例子违反了迪米特原则，为了方便理解把相关类放在一起。
1. 对象职责不清晰，不单一。顾客类下单购物，还实现了价格计算逻辑。
2. 对象依赖了朋友的朋友。顾客类依赖了购买朋友的朋友商品。
对比：正例让顾客只与购物车等直接朋友交互，价格计算由购物车完成。
"""

class Product:
    def __init__(self, name, price):
        self.name = name
        self.price = price

    def get_name(self):
        return self.name

    def get_price(self):
        return self.price


# 违规点：顾客直接依赖商品并承担价格统计
class Customer:
    def __init__(self, name):
        self.name = name
        self.products = []

    # 直接操作商品细节，违反最少知道原则
    def buy(self, product: Product):
        # 直接了解商品价格细节
        if product.get_price() > 1000:
            print(f"{product.get_name()}'s price exceeds range：{product.get_price()}")
            return
        self.products.append(product)
        total_price = self.calculate_total_price()
        print(f"{self.name} purchased {product.get_name()} for {product.get_price()}")
        print(f"Total price: ${total_price}")

    # 价格汇总应由购物车/订单等中介对象负责
    def calculate_total_price(self):
        return sum(product.get_price() for product in self.products)


customer = Customer("Jimmy")
customer.buy(Product("Computer", 5000))
customer.buy(Product("Book", 200))

"""
jarry@jarrys-MBP python % python3 law_demeter_bad_example.py 
Computer's price exceeds range：5000
Jimmy purchased Book for 200
Total price: $200
"""
