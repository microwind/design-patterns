/* 视图层（View）*/
// view/OrderView.cpp
#include "OrderView.h"
#include <iostream>

void OrderView::render(const Order &order) const
{
    std::cout << "=== Order Details ===\n"
              << "ID:\t" << order.getId() << '\n'
              << "OrderNo:\t" << order.getOrderNo() << '\n'
              << "Customer:\t" << order.getCustomer() << '\n'
              << "Amount:\t$" << order.getAmount() << "\n\n";
}

void OrderView::showError(const std::string &message) const
{
    std::cerr << "\033[31m[ERROR] " << message << "\033[0m\n\n";
}