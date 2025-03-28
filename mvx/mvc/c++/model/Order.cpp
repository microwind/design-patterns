/* 模型层（Model）*/
// model/Order.cpp
#include "Order.h"

Order::Order(int id,
             const std::string &orderNo,
             const std::string &customer,
             double amount)
    : id_(id), orderNo_(orderNo), customer_(customer), amount_(amount)
{
}

// Getters
int Order::getId() const
{
    return id_;
}
std::string Order::getOrderNo() const
{
    return orderNo_;
}
std::string Order::getCustomer() const
{
    return customer_;
}
double Order::getAmount() const
{
    return amount_;
}

// Setters
void Order::setId(const int id)
{
    id_ = id;
}
void Order::setOrderNo(const std::string &orderNo)
{
    orderNo_ = orderNo;
}
void Order::setCustomer(const std::string &customer)
{
    customer_ = customer;
}
void Order::setAmount(double amount)
{
    amount_ = amount;
}