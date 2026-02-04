package main

import (
  "fmt"
)

/*
 这个例子符合单一职责原则。
 1. 分别建立三个类，一个负责订单业务处理(OrderProcessor)，一个负责校验订单(OrderValidator)，一个负责保存数据(OrderDao)，各司其职。
 2. 由处理订单来负责调用工具类，订单业务逻辑修改不会影响到工具类。
 3. 工具类修改也不会影响到订单业务处理。
 4. 职责是否足够单一，要根据具体场景而异，不同情形下采用不同的设计，主要的目标是便于理解、扩展和维护。
*/

type OrderProcessor struct {
  orderValidator OrderValidator
  orderDao       OrderDao
}

// 订单处理逻辑方法
func (op *OrderProcessor) processOrder(orderId int64) bool {
  fmt.Println("oder ID:", orderId)
  // 1. 先验证订单，调用校验类
  if !op.orderValidator.validateId(orderId) {
    fmt.Println("order validate id failed.")
    return false
  }

  if !op.orderValidator.validateTime() {
    fmt.Println("order validate time failed.")
    return false
  }

  // 2. 订单数据其他逻辑处理
  if orderId%2 == 0 {
    fmt.Println("order data processing.")
  }

  // 3. 再保存订单到数据库，调用数据库类
  fmt.Println("order save to DB.")
  op.orderDao.saveOrder(orderId)
  // 或则删除订单
  // op.deleteOrder(orderId)

  return true
}

// 订单校验类，校验订单的合法性和有效性等
type OrderValidator struct{}

// 校验订单逻辑，最好别放在订单处理类中
func (ov *OrderValidator) validateId(orderId int64) bool {
  // doSomething
  if orderId%2 == 1 {
    return false
  }
  return true
}

func (ov *OrderValidator) validateTime() bool {
  // doSomething
  return true
}

// Order数据库访问类，负责处理订单的CRUD操作
type OrderDao struct{}

// 删除订单
func (od *OrderDao) deleteOrder(orderId int64) bool {
  // doSomething
  return true
}

// 保存订单到数据库
func (od *OrderDao) saveOrder(orderId int64) bool {
  if orderId%2 == 0 {
    fmt.Println("data saving.")
  }
  fmt.Println("data save done.")
  return true
}

// 测试验证
func main() {
  // 创建OrderProcessor对象
  orderProcessor := &OrderProcessor{
    orderValidator: OrderValidator{},
    orderDao:       OrderDao{},
  }
  orderProcessor.processOrder(1001)
  orderProcessor.processOrder(1002)
}

/**
jarry@jarrys-MBP go % go run SingleResponsibilityGoodExample.go 
oder ID: 1001
order validate id failed.
oder ID: 1002
order data processing.
order save to DB.
data saving.
data save done.
*/
