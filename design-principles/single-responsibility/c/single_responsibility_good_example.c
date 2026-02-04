#include <stdio.h>

/*
 这个例子符合单一职责原则。
 1. 分别建立三个类，一个负责订单业务处理(OrderProcessor)，一个负责校验订单(OrderValidator)，一个负责保存数据(OrderDao)，各司其职。
 2. 由处理订单来负责调用工具类，订单业务逻辑修改不会影响到工具类。
 3. 工具类修改也不会影响到订单业务处理。
 4. 职责是否足够单一，要根据具体场景而异，不同情形下采用不同的设计，主要的目标是便于理解、扩展和维护。
*/

// 订单校验类，校验订单的合法性和有效性等
typedef struct
{
} OrderValidator;

// 校验订单逻辑，最好别放在订单处理类中
int validateId(int64_t orderId)
{
  // doSomething
  if (orderId % 2 == 1)
  {
    return 0;
  }
  return 1;
}

int validateTime()
{
  // doSomething
  return 1;
}

// Order数据库访问类，负责处理订单的CRUD操作
typedef struct
{
} OrderDao;

// 删除订单
int deleteOrder(int64_t orderId)
{
  // doSomething
  return 1;
}

// 保存订单到数据库
int saveOrder(int64_t orderId)
{
  if (orderId % 2 == 0)
  {
    printf("data saving.\n");
  }
  printf("data save done.\n");
  return 1;
}

// 订单处理逻辑方法
int processOrder(int64_t orderId)
{
  printf("oder ID: %lld\n", orderId);
  // 1. 先验证订单，调用校验类
  if (!validateId(orderId))
  {
    printf("order validate id failed.\n");
    return 0;
  }

  if (!validateTime())
  {
    printf("order validate time failed.\n");
    return 0;
  }

  // 2. 订单数据其他逻辑处理
  if (orderId % 2 == 0)
  {
    printf("order data processing.\n");
  }

  // 3. 再保存订单到数据库，调用数据库类
  printf("order save to DB.\n");
  saveOrder(orderId);
  // 或则删除订单
  // deleteOrder(orderId);

  return 1;
}

// 测试验证
int main()
{
  // 创建OrderProcessor对象
  processOrder(1001);
  processOrder(1002);

  return 0;
}

/**
jarry@jarrys-MBP c % gcc single_responsibility_good_example.c 
jarry@jarrys-MBP c % ./a.out 
oder ID: 1001
order validate id failed.
oder ID: 1002
order data processing.
order save to DB.
data saving.
data save done.
*/
