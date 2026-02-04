/**
 * 迪米特原则 - 反例
 * 这个例子违反了迪米特原则，为了方便理解把相关类放在一起。
 * 1. 对象职责不清晰，不单一。顾客类下单购物，还实现了价格计算逻辑。
 * 2. 对象依赖了朋友的朋友。顾客类依赖了购买朋友的朋友商品。
 * 对比：正例让顾客只与购物车等直接朋友交互，价格计算由购物车完成。
 */
package main

import "fmt"

type Product struct {
  name  string
  price float64
}

// 违规点：顾客直接依赖商品并承担价格统计
type Customer struct {
  name     string
  products []Product
}

// 直接操作商品细节，违反最少知道原则
func (c *Customer) buy(product Product) {
  // 直接了解商品价格细节
  if product.price > 1000 {
    fmt.Printf("%s's price exceeds range：%.0f\n", product.name, product.price)
    return
  }
  c.products = append(c.products, product)
  totalPrice := c.calculateTotalPrice()
  fmt.Printf("%s purchased %s for %.0f\n", c.name, product.name, product.price)
  fmt.Printf("Total price: $%.0f\n", totalPrice)
}

// 价格汇总应由购物车/订单等中介对象负责
func (c *Customer) calculateTotalPrice() float64 {
  total := 0.0
  for _, product := range c.products {
    total += product.price
  }
  return total
}

func main() {
  customer := Customer{name: "Jimmy"}
  customer.buy(Product{name: "Computer", price: 5000})
  customer.buy(Product{name: "Book", price: 200})
}

/**
jarry@jarrys-MBP go % go run LawDemeterBadExample.go 
Computer's price exceeds range：5000
Jimmy purchased Book for 200
Total price: $200
*/
