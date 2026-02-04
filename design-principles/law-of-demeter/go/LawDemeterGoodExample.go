/**
 * 迪米特法则（Law of Demeter，LoD）
 * 也叫最少知识原则（Principle of Least Knowledge）。
 * 即一个对象应当对其他对象尽可能少的了解，对象之间的耦合度应该尽可能低。
 */
package main

import (
  "fmt"
)

// Product类，表示商品
type Product struct {
  name  string
  price float64
}

func NewProduct(name string, price float64) *Product {
  return &Product{name: name, price: price}
}

func (p *Product) GetName() string {
  return p.name // 获取商品名称
}

func (p *Product) GetPrice() float64 {
  return p.price // 获取商品价格
}

// ShoppingCart类，表示购物车
type ShoppingCart struct {
  products []*Product // 存储商品的切片
}

func NewShoppingCart() *ShoppingCart {
  return &ShoppingCart{products: []*Product{}}
}

func (sc *ShoppingCart) AddProduct(product *Product) {
  sc.products = append(sc.products, product) // 添加商品到购物车
}

func (sc *ShoppingCart) CalculateTotalPrice() float64 {
  totalPrice := 0.0
  for _, product := range sc.products {
    totalPrice += product.GetPrice() // 计算购物车中所有商品的总价格
  }
  return totalPrice
}

func (sc *ShoppingCart) ValidateProduct(product *Product) bool {
  return product.GetPrice() < 1000
}

// Customer类，表示顾客
type Customer struct {
  name         string        // 顾客名称
  shoppingCart *ShoppingCart // 顾客的购物车
}

func NewCustomer(name string) *Customer {
  return &Customer{name: name, shoppingCart: NewShoppingCart()}
}

// 购买商品方法
func (c *Customer) Buy(product *Product) {
  // 只跟购物车打交道，且不用了解购物车实现细节，只是调用购物车方法
  if c.shoppingCart.ValidateProduct(product) {
    c.shoppingCart.AddProduct(product)                 // 将商品添加到购物车
    totalPrice := c.shoppingCart.CalculateTotalPrice() // 计算总价格
    fmt.Printf("%s 购买了 %s 价格为 %.2f\n", c.name, product.GetName(), totalPrice)
  } else {
    fmt.Printf("%s 的价格超出范围：%.2f\n", product.GetName(), product.GetPrice())
  }
}

func main() {
  customer := NewCustomer("Jimmy")
  product1 := NewProduct("Computer", 5000)
  product2 := NewProduct("Book", 200)

  customer.Buy(product1)
  customer.Buy(product2)
}

/**
jarry@jarrys-MBP go % go run LawDemeterGoodExample.go 
Computer 的价格超出范围：5000.00
Jimmy 购买了 Book 价格为 200.00
*/
