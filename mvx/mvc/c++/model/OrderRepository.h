/* 数据访问层（Repository）*/
// model/OrderRepository.h
#ifndef ORDERREPOSITORY_H
#define ORDERREPOSITORY_H

#include "Order.h"
#include <vector>
#include <memory>
#include <string>

class OrderRepository {
public:
    void persist(const std::shared_ptr<Order> &order);
    std::shared_ptr<Order> findById(int id) const;
    std::shared_ptr<Order> findByOrderNo(const std::string &orderNo) const;
    std::vector<std::shared_ptr<Order> > findAll() const;
    bool removeByOrderNo(const std::string& orderNo);

private:
    std::vector<std::shared_ptr<Order> > orders_;
        // 定义一个函数对象用于匹配订单号
    struct OrderNoMatcher {
        explicit OrderNoMatcher(const std::string& orderNo) : orderNo_(orderNo) {}
        bool operator()(const std::shared_ptr<Order>& order) const {
            return order->getOrderNo() == orderNo_;
        }
    private:
        std::string orderNo_;
    };
};

#endif // ORDERREPOSITORY_H