/* 控制器层（Controller）*/
// controller/OrderController.h

#ifndef ORDERCONTROLLER_H
#define ORDERCONTROLLER_H

#include "../model/Order.h"
#include "../view/OrderView.h"
#include "../model/OrderRepository.h"
#include <memory>
#include <string>

class OrderController
{
public:
  OrderController(OrderView &view, OrderRepository &repository);

  // 通过 Controller 操作创建订单
  void createOrder(int id, const std::string &orderNo, const std::string &customer, double amount);

  // 通过 Controller 查询订单
  std::shared_ptr<Order> getById(int id) const;
  std::shared_ptr<Order> getByOrderNo(const std::string &orderNo) const;

  // 通过 Controller 更新订单（通过 Repository 更新）
  void updateAmount(const std::string &orderNo, double amount);
  void updateCustomer(const std::string &orderNo, const std::string &customer);
  void deleteOrder(const std::string& orderNo);
  void listAllOrders() const;

  // 通过 Controller 更新视图
  void refreshView(const std::string &orderNo) const;

private:
  OrderView &view_;
  OrderRepository &repository_;
};

#endif // ORDERCONTROLLER_H