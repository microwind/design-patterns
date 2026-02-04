"""
 * 迪米特法则（Law of Demeter，LoD）
 * 也叫最少知识原则（Principle of Least Knowledge）。
 * 即一个对象应当对其他对象尽可能少的了解，对象之间的耦合度应该尽可能低。
"""
# Product类，表示商品


class Product:
    def __init__(self, name, price):
        self.name = name  # 商品名称
        self.price = price  # 商品价格

    def get_name(self):
        return self.name  # 获取商品名称

    def get_price(self):
        return self.price  # 获取商品价格


# ShoppingCart类，表示购物车
class ShoppingCart:
    def __init__(self):
        self.products = []  # 存储商品的列表

    def add_product(self, product):
        self.products.append(product)  # 添加商品到购物车

    def calculate_total_price(self):
        # 计算购物车中所有商品的总价格
        return sum(product.get_price() for product in self.products)

    def validate_product(self, product):
        return product.get_price() < 1000


# Customer类，表示顾客
class Customer:
    def __init__(self, name):
        self.name = name  # 顾客名称
        self.shopping_cart = ShoppingCart()  # 顾客的购物车

    # 只跟购物车打交道，且不用了解购物车实现细节，只是调用购物车方法
    def buy(self, product):
        # 购买商品方法
        if self.shopping_cart.validate_product(product):
            self.shopping_cart.add_product(product)  # 将商品添加到购物车
            total_price = self.shopping_cart.calculate_total_price()  # 计算总价格
            print(f"{self.name} 购买了 {product.get_name()} 价格为 {total_price}")
        else:
            print(f"{product.get_name()} 的价格超出范围：{product.get_price()}")


"""
 * 测试符合迪米特原则的例子
 * 对象职责单一清晰，不把很多功能堆积在一起。
 * 拆出一个购物车类，作为顾客和商品之间的中介类。
 * 对象不直接依赖朋友的朋友。
"""
customer = Customer("Jimmy")
customer.buy(Product("Computer", 5000))
customer.buy(Product("Book", 200))

"""
jarry@jarrys-MBP python % python3 law_demeter_good_example.py 
Computer 的价格超出范围：5000
Jimmy 购买了 Book 价格为 200
"""
