"""
单一职责原则 - 反例
这个例子违反了单一职责原则。
1. 订单处理类实现了订单校验以及保存数据库的两种逻辑。
2. 一旦订单条件有修改或保存数据库方式有变更都需要改动此类。
对比：正例将校验与数据持久化拆分为独立类，职责清晰、修改互不影响。
"""

class OrderProcessor:
    # 违规点：业务处理、校验、持久化混在一个类
    def process_order(self, order_id):
        print("oder ID：" + str(order_id))

        # 校验逻辑应拆分为独立验证类
        if not self.validate_id(order_id):
            print("order validate id failed.")
            return False

        if not self.validate_time(0):
            print("order validate time failed.")
            return False

        if order_id % 2 == 0:
            print("order data processing.")

        # 持久化职责不应放在业务处理类里
        print("order save to DB.")
        self.save_order(order_id)
        return True

    # 校验职责应独立
    def validate_id(self, order_id):
        return order_id % 2 == 0

    def validate_time(self, _time):
        return True

    # 保存数据属于持久化职责
    def save_order(self, order_id):
        if order_id % 2 == 0:
            print("order saving.")
        print("order save done.")
        return True

    def delete_order(self, _order_id):
        return True


processor = OrderProcessor()
processor.process_order(1001)
processor.process_order(1002)

"""
jarry@jarrys-MBP python % python3 single_responsibility_bad_example.py 
oder ID：1001
order validate id failed.
oder ID：1002
order data processing.
order save to DB.
order saving.
order save done.
"""
