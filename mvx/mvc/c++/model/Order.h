/* 模型层（Model）接口*/
// model/Order.h
#ifndef ORDER_H
#define ORDER_H

#include <string>

class Order
{
public:
    Order(const int id,
          const std::string &orderNo,
          const std::string &customer,
          double amount);

    // Accessors
    int getId() const;
    std::string getOrderNo() const;
    std::string getCustomer() const;
    double getAmount() const;

    // Mutators
    void setId(const int id);
    void setOrderNo(const std::string &orderNo);
    void setCustomer(const std::string &customer);
    void setAmount(double amount);

private:
    int id_;
    std::string orderNo_;
    std::string customer_;
    double amount_;
};

#endif // ORDER_H