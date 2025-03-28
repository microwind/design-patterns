/* 控制器层（Controller）*/
// controller/OrderController.cpp
#include "OrderController.h"
#include <iostream>
#include <memory>

OrderController::OrderController(OrderView &view, OrderRepository &repository)
    : view_(view), repository_(repository) {}

void OrderController::createOrder(int id, const std::string &orderNo, const std::string &customer, double amount)
{
    std::shared_ptr<Order> newOrder = std::make_shared<Order>(id, orderNo, customer, amount);
    repository_.persist(newOrder);
}

std::shared_ptr<Order> OrderController::getById(int id) const
{
    auto order = repository_.findById(id);
    if (!order)
        view_.showError("Invalid id");
    return order;
}

std::shared_ptr<Order> OrderController::getByOrderNo(const std::string &orderNo) const
{
    auto order = repository_.findByOrderNo(orderNo);
    if (!order)
        view_.showError("Invalid order number");
    return order;
}

void OrderController::updateAmount(const std::string &orderNo, double amount)
{
    auto order = repository_.findByOrderNo(orderNo);
    if (order)
    {
        if (amount > 0)
            order->setAmount(amount);
        else
            view_.showError("Amount must be positive");
    }
    else
    {
        view_.showError("Order not found");
    }
}

void OrderController::updateCustomer(const std::string &orderNo, const std::string &customer)
{
    auto order = repository_.findByOrderNo(orderNo);
    if (order)
    {
        if (!customer.empty())
            order->setCustomer(customer);
        else
            view_.showError("Customer name cannot be empty");
    }
    else
    {
        view_.showError("Order not found");
    }
}

void OrderController::deleteOrder(const std::string& orderNo) {
    if (repository_.removeByOrderNo(orderNo)) {
        std::cout << "Order " << orderNo << " deleted successfully." << std::endl;
    } else {
        view_.showError("Order not found");
    }
}

void OrderController::listAllOrders() const {
    auto orders = repository_.findAll();
    if (!orders.empty()) {
        for (const auto& order : orders) {
            view_.render(*order);
        }
    } else {
        view_.showError("No orders available.");
    }
}

void OrderController::refreshView(const std::string &orderNo) const
{
    auto order = repository_.findByOrderNo(orderNo);
    if (order)
    {
        view_.render(*order);
    }
    else
    {
        view_.showError("Order not found");
    }
}