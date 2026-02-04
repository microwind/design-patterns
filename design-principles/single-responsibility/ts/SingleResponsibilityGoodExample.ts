/**
 * 这个例子符合单一职责原则。
 * 1.分别建立三个类，一个负责订单业务处理(OrderProcessor)，一个负责校验订单(OrderValidator)，一个负责保存数据(OrderDao)，各司其职。
 * 2. 由处理订单来负责调用工具类，订单业务逻辑修改不会影响到工具类。
 * 3. 工具类修改也不会影响到订单业务处理。
 * 4. 职责是否足够单一，要根据具体场景而异，不同情形下采用不同的设计，主要的目标是便于理解、扩展和维护。
 */
class OrderProcessor {
  private orderValidator: OrderValidator
  private orderDao: OrderDao

  constructor() {
    this.orderValidator = new OrderValidator()
    this.orderDao = new OrderDao()
  }

  // 订单处理逻辑方法
  processOrder(orderId: number): boolean {
    console.log('oder ID：' + orderId)
    // 1. 先验证订单，调用校验类
    if (!this.orderValidator.validateId(orderId)) {
      console.log('order validate id failed.')
      return false
    }

    if (!this.orderValidator.validateTime(Date.now())) {
      console.log('order validate time failed.')
      return false
    }

    // 2. 订单数据其他逻辑处理
    if (orderId % 2 === 0) {
      console.log('order data processing.')
    }

    // 3. 再保存订单到数据库，调用数据库类
    console.log('order save to DB.')
    this.orderDao.saveOrder(orderId)
    // 或则删除订单
    // this.deleteOrder(orderId);

    return true
  }
}

/**
 * 订单校验类，校验订单的合法性和有效性等
 */
class OrderValidator {
  // 校验订单逻辑，最好别放在订单处理类中
  validateId(orderId: number): boolean {
    // doSomething
    if (orderId % 2 === 1) {
      return false
    }
    return true
  }

  validateTime(time: number): boolean {
    // doSomething
    return true
  }
}

/**
 * Order数据库访问类，负责处理订单的CRUD操作
 */
class OrderDao {
  // 删除订单
  deleteOrder(orderId: number): boolean {
    // doSomething
    return true
  }

  // 保存订单到数据库
  saveOrder(orderId: number): boolean {
    if (orderId % 2 === 0) {
      console.log('data saving.')
    }
    console.log('data save done.')
    return true
  }
}

/** 测试 */
;(function () {
  const orderProcessor = new OrderProcessor()
  orderProcessor.processOrder(1001)
  orderProcessor.processOrder(1002)
})()

/**
jarry@jarrys-MBP ts % ts-node SingleResponsibilityGoodExample.ts 
oder ID：1001
order validate id failed.
oder ID：1002
order data processing.
order save to DB.
data saving.
data save done.
*/
