/**
 * 迪米特原则 - 反例
 * 这个例子违反了迪米特原则，为了方便理解把相关类放在一起。
 * 1. 对象职责不清晰，不单一。顾客类下单购物，还实现了价格计算逻辑。
 * 2. 对象依赖了朋友的朋友。顾客类依赖了购买朋友的朋友商品。
 * 对比：正例让顾客只与购物车等直接朋友交互，价格计算由购物车完成。
 */
class Product {
  constructor(name, price) {
    this.name = name;
    this.price = price;
  }

  getName() {
    return this.name;
  }

  getPrice() {
    return this.price;
  }
}

// 违规点：顾客直接依赖商品并承担价格统计
class Customer {
  constructor(name) {
    this.name = name;
    this.products = [];
  }

  // 直接操作商品细节，违反最少知道原则
  buy(product) {
    // 直接了解商品价格细节
    if (product.getPrice() > 1000) {
      console.log(`${product.getName()}'s price exceeds range：${product.getPrice()}`);
      return;
    }
    this.products.push(product);
    const totalPrice = this.calculateTotalPrice();
    console.log(`${this.name} purchased ${product.getName()} for ${product.getPrice()}`);
    console.log(`Total price: $${totalPrice}`);
  }

  // 价格汇总应由购物车/订单等中介对象负责
  calculateTotalPrice() {
    return this.products.reduce((sum, product) => sum + product.getPrice(), 0);
  }
}

const customer = new Customer('Jimmy');
customer.buy(new Product('Computer', 5000));
customer.buy(new Product('Book', 200));

/**
jarry@jarrys-MBP js % node LawDemeterBadExample.js 
Computer's price exceeds range：5000
Jimmy purchased Book for 200
Total price: $200
*/
