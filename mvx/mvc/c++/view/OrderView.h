// view/OrderView.h
#ifndef ORDER_VIEW_H
#define ORDER_VIEW_H

#include "../model/Order.h"

class OrderView
{
public:
  void render(const Order &order) const;
  void showError(const std::string &message) const;
};

#endif // ORDER_VIEW_H