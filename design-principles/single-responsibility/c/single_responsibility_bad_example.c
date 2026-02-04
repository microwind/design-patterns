#include <stdio.h>
/**
 * 单一职责原则 - 反例
 * 这个例子违反了单一职责原则。
 * 1. 订单处理类实现了订单校验以及保存数据库的两种逻辑。
 * 2. 一旦订单条件有修改或保存数据库方式有变更都需要改动此类。
 * 对比：正例将校验与数据持久化拆分为独立类，职责清晰、修改互不影响。
 */

typedef struct {
  int unused;
} OrderProcessor;

int validate_id(int order_id) {
  return order_id % 2 == 0;
}

int validate_time(long _time) {
  return 1;
}

// 保存数据属于持久化职责
int save_order(int order_id) {
  if (order_id % 2 == 0) {
    printf("order saving.\n");
  }
  printf("order save done.\n");
  return 1;
}

// 违规点：一个模块负责校验、业务处理、持久化
int process_order(OrderProcessor *processor, int order_id) {
  (void)processor;
  printf("oder ID：%d\n", order_id);

  // 校验职责应独立
  if (!validate_id(order_id)) {
    printf("order validate id failed.\n");
    return 0;
  }

  if (!validate_time(0)) {
    printf("order validate time failed.\n");
    return 0;
  }

  if (order_id % 2 == 0) {
    printf("order data processing.\n");
  }

  // 持久化职责不应放在这里
  printf("order save to DB.\n");
  save_order(order_id);
  return 1;
}

int main() {
  OrderProcessor processor = {0};
  process_order(&processor, 1001);
  process_order(&processor, 1002);
  return 0;
}
/**
 * 单一职责原则 - 反例
 * 这个例子违反了单一职责原则。
 * 1. 订单处理类实现了订单校验以及保存数据库的两种逻辑。
 * 2. 一旦订单条件有修改或保存数据库方式有变更都需要改动此类。
 * 对比：正例将校验与数据持久化拆分为独立类，职责清晰、修改互不影响。
 */
