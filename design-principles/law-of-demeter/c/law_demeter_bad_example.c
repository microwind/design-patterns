#include <stdio.h>
/**
 * 迪米特原则 - 反例
 * 这个例子违反了迪米特原则，为了方便理解把相关类放在一起。
 * 1. 对象职责不清晰，不单一。顾客类下单购物，还实现了价格计算逻辑。
 * 2. 对象依赖了朋友的朋友。顾客类依赖了购买朋友的朋友商品。
 * 对比：正例让顾客只与购物车等直接朋友交互，价格计算由购物车完成。
 */

typedef struct {
  const char *name;
  double price;
} Product;

// 违规点：顾客直接持有商品并负责价格统计
typedef struct {
  const char *name;
  Product products[10];
  int count;
} Customer;

// 价格汇总应由购物车/订单等中介对象负责
double calculate_total_price(Customer *customer) {
  double total = 0.0;
  for (int i = 0; i < customer->count; i++) {
    total += customer->products[i].price;
  }
  return total;
}

// 直接操作商品细节，违反最少知道原则
void buy(Customer *customer, Product product) {
  // 直接了解商品价格细节
  if (product.price > 1000) {
    printf("%s's price exceeds range：%.0f\n", product.name, product.price);
    return;
  }
  customer->products[customer->count++] = product;
  double total_price = calculate_total_price(customer);
  printf("%s purchased %s for %.0f\n", customer->name, product.name, product.price);
  printf("Total price: $%.0f\n", total_price);
}

int main() {
  Customer customer = {"Jimmy", {0}, 0};
  buy(&customer, (Product){"Computer", 5000});
  buy(&customer, (Product){"Book", 200});
  return 0;
}

/**
jarry@jarrys-MBP c % gcc law_demeter_bad_example.c 
jarry@jarrys-MBP c % ./a.out 
Computer's price exceeds range：5000
Jimmy purchased Book for 200
Total price: $200
*/
