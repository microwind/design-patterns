/**
 * 单一职责原则 - 反例
 * 这个例子违反了单一职责原则。
 * 1. 订单处理类实现了订单校验以及保存数据库的两种逻辑。
 * 2. 一旦订单条件有修改或保存数据库方式有变更都需要改动此类。
 * 对比：正例将校验与数据持久化拆分为独立类，职责清晰、修改互不影响。
 */
class OrderProcessor {
  // 违规点：一个类同时负责校验、业务处理和持久化
  processOrder(orderId) {
    console.log("oder ID：" + orderId);

    // 校验逻辑应由独立验证类负责，否则业务类频繁修改
    if (!this.validateId(orderId)) {
      console.log("order validate id failed.");
      return false;
    }

    if (!this.validateTime(Date.now())) {
      console.log("order validate time failed.");
      return false;
    }

    if (orderId % 2 === 0) {
      console.log("order data processing.");
    }

    // 持久化职责不应放在业务处理类里
    console.log("order save to DB.");
    this.saveOrder(orderId);

    return true;
  }

  // 校验职责应拆分到单独的验证类
  validateId(orderId) {
    return orderId % 2 === 0;
  }

  validateTime(_time) {
    return true;
  }

  // 保存数据属于持久化层职责
  saveOrder(orderId) {
    if (orderId % 2 === 0) {
      console.log("order saving.");
    }
    console.log("order save done.");
    return true;
  }

  deleteOrder(_orderId) {
    return true;
  }
}

const processor = new OrderProcessor();
processor.processOrder(1001);
processor.processOrder(1002);

/**
jarry@jarrys-MBP js % node SingleResponsibilityBadExample.js 
oder ID：1001
order validate id failed.
oder ID：1002
order data processing.
order save to DB.
order saving.
order save done.
*/
