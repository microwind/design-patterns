/**
 * 迪米特法则（Law of Demeter，LoD）
 * 也叫最少知识原则（Principle of Least Knowledge）。
 * 即一个对象应当对其他对象尽可能少的了解，对象之间的耦合度应该尽可能低。
 */

// Product类，表示商品
class Product {
  private name: string // 商品名称
  private price: number // 商品价格

  constructor(name: string, price: number) {
    this.name = name
    this.price = price
  }

  getName(): string {
    return this.name // 获取商品名称
  }

  getPrice(): number {
    return this.price // 获取商品价格
  }
}

// ShoppingCart类，表示购物车
class ShoppingCart {
  private products: Product[] // 存储商品的数组

  constructor() {
    this.products = []
  }

  addProduct(product: Product): void {
    this.products.push(product) // 添加商品到购物车
  }

  calculateTotalPrice(): number {
    return this.products.reduce(
      (total, product) => total + product.getPrice(),
      0
    ) // 计算购物车中所有商品的总价格
  }

  validateProduct(product: Product): boolean {
    return product.getPrice() < 1000
  }
}

// Customer类，表示顾客
class Customer {
  private name: string // 顾客名称
  private shoppingCart: ShoppingCart // 顾客的购物车

  constructor(name: string) {
    this.name = name
    this.shoppingCart = new ShoppingCart()
  }

  // 购买商品方法
  buy(product: Product): void {
    // 只跟购物车打交道，且不用了解购物车实现细节，只是调用购物车方法
    if (this.shoppingCart.validateProduct(product)) {
      this.shoppingCart.addProduct(product) // 将商品添加到购物车
      const totalPrice = this.shoppingCart.calculateTotalPrice() // 计算总价格
      console.log(
        `${this.name} 购买了 ${product.getName()} 价格为 ${totalPrice}`
      )
    } else {
      console.log(`${product.getName()} 的价格超出范围：${product.getPrice()}`)
    }
  }
}

/**
 * 测试符合迪米特原则的例子
 * 对象职责单一清晰，不把很多功能堆积在一起。
 * 拆出一个购物车类，作为顾客和商品之间的中介类。
 * 对象不直接依赖朋友的朋友。
 */
const customer = new Customer('Jimmy')
customer.buy(new Product('Computer', 5000))
customer.buy(new Product('Book', 200))

/**
jarry@jarrys-MBP ts % ts-node LawDemeterGoodExample.ts 
Computer 的价格超出范围：5000
Jimmy 购买了 Book 价格为 200
*/
