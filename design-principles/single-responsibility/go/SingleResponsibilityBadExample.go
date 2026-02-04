/**
 * 单一职责原则 - 反例
 * 这个例子违反了单一职责原则。
 * 1. 订单处理类实现了订单校验以及保存数据库的两种逻辑。
 * 2. 一旦订单条件有修改或保存数据库方式有变更都需要改动此类。
 * 对比：正例将校验与数据持久化拆分为独立类，职责清晰、修改互不影响。
 */
package main

import "fmt"

type OrderProcessor struct{}

// 违规点：业务处理、校验、持久化混在一个结构体
func (p OrderProcessor) processOrder(orderId int) bool {
  fmt.Printf("oder ID：%d\n", orderId)

  // 校验逻辑应拆分为独立模块
  if !p.validateId(orderId) {
    fmt.Println("order validate id failed.")
    return false
  }

  if !p.validateTime(0) {
    fmt.Println("order validate time failed.")
    return false
  }

  if orderId%2 == 0 {
    fmt.Println("order data processing.")
  }

  // 持久化职责不应放在业务处理逻辑里
  fmt.Println("order save to DB.")
  p.saveOrder(orderId)
  return true
}

// 校验职责应独立
func (p OrderProcessor) validateId(orderId int) bool {
  return orderId%2 == 0
}

func (p OrderProcessor) validateTime(_ int) bool {
  return true
}

// 保存数据属于持久化职责
func (p OrderProcessor) saveOrder(orderId int) bool {
  if orderId%2 == 0 {
    fmt.Println("order saving.")
  }
  fmt.Println("order save done.")
  return true
}

func (p OrderProcessor) deleteOrder(_ int) bool {
  return true
}

func main() {
  processor := OrderProcessor{}
  processor.processOrder(1001)
  processor.processOrder(1002)
}

/**
jarry@jarrys-MBP go % go run SingleResponsibilityBadExample.go 
oder ID：1001
order validate id failed.
oder ID：1002
order data processing.
order save to DB.
order saving.
order save done.
*/
