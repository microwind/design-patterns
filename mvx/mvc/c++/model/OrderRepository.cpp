/* 数据访问层（Repository）*/
// model/OrderRepository.cpp
#include "OrderRepository.h"

void OrderRepository::persist(const std::shared_ptr<Order> &order)
{
    orders_.push_back(order);
}

// 根据id获取订单
std::shared_ptr<Order> OrderRepository::findById(const int id) const
{
    // 不用auto，兼容C++98，显示指定迭代器
    for (std::vector<std::shared_ptr<Order> >::const_iterator it = orders_.begin();
         it != orders_.end(); ++it)
    {
        if ((*it)->getId() == id)
            return *it;
    }
    return std::shared_ptr<Order>();
}

// 根据订单号获取订单
std::shared_ptr<Order> OrderRepository::findByOrderNo(const std::string &orderNo) const
{
    for (const auto &order : orders_)
    {
        if (order->getOrderNo() == orderNo)
            return order;
    }
    return nullptr;
}

bool OrderRepository::removeByOrderNo(const std::string &orderNo)
{
    std::vector<std::shared_ptr<Order> >::iterator
        it = std::remove_if(orders_.begin(), orders_.end(), OrderNoMatcher(orderNo));
    if (it != orders_.end())
    {
        orders_.erase(it, orders_.end());
        return true;
    }
    return false;
}

std::vector<std::shared_ptr<Order> > OrderRepository::findAll() const
{
    return orders_;
}