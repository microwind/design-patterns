/**
 * 迪米特法则（Law of Demeter，LoD）
 * 也叫最少知识原则（Principle of Least Knowledge）。
 * 即一个对象应当对其他对象尽可能少的了解，对象之间的耦合度应该尽可能低。
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// Product struct对象，只有商品本身信息
typedef struct
{
    char name[50]; // 商品名称
    double price;  // 商品价格
} Product;

// 创建Product对象
Product *create_product(const char *name, double price)
{
    Product *product = (Product *)malloc(sizeof(Product)); // 分配内存
    strcpy(product->name, name);                           // 设置商品名称
    product->price = price;                                // 设置商品价格
    return product;
}

// 获取商品名称
const char *get_product_name(Product *product)
{
    return product->name;
}

// 获取商品价格
double get_product_price(Product *product)
{
    return product->price;
}

// ShoppingCart对象，负责商品校验价格计算等逻辑，是顾客和商品之间的桥梁
typedef struct
{
    Product **products; // 存储商品的指针数组
    int count;          // 商品数量
} ShoppingCart;

//
ShoppingCart *create_shopping_cart()
{
    ShoppingCart *cart = (ShoppingCart *)malloc(sizeof(ShoppingCart)); // 分配内存
    cart->products = NULL;                                             // 初始化为空
    cart->count = 0;                                                   // 初始化数量为0
    return cart;
}

// 向购物车中添加商品
void add_product_to_cart(ShoppingCart *cart, Product *product)
{
    cart->products = (Product **)realloc(cart->products, (cart->count + 1) * sizeof(Product *)); // 重新分配内存
    cart->products[cart->count] = product;                                                       // 将商品指针添加到数组中
    cart->count++;                                                                               // 商品数量加1
}

// 计算购物车中所有商品的总价格
double calculate_total_price(ShoppingCart *cart)
{
    double total_price = 0.0;
    for (int i = 0; i < cart->count; i++)
    {
        total_price += get_product_price(cart->products[i]); // 累加每个商品的价格
    }
    return total_price;
}

// 验证商品是否价格是否大于1000
int validate_product(Product *product)
{
    return get_product_price(product) < 1000 ? 1 : 0;
}

// Customer类，表示顾客
typedef struct
{
    char name[50];               // 顾客名称
    ShoppingCart *shopping_cart; // 顾客的购物车
} Customer;

// Customer，减轻逻辑，调用购物车来实现商品的购买，不直接跟商品交互。
Customer *create_customer(const char *name)
{
    Customer *customer = (Customer *)malloc(sizeof(Customer)); // 分配内存
    strcpy(customer->name, name);                              // 设置顾客名称
    customer->shopping_cart = create_shopping_cart();          // 创建购物车
    return customer;
}

// 顾客购买商品
// 只跟购物车打交道，且不用了解购物车实现细节，只是调用购物车方法
void buy(Customer *customer, Product *product)
{
    if (validate_product(product))
    {                                                                        // 验证商品价格
        add_product_to_cart(customer->shopping_cart, product);               // 将商品添加到购物车
        double total_price = calculate_total_price(customer->shopping_cart); // 计算总价格
        printf("%s 购买了 %s 价格为 %.2f\n", customer->name, get_product_name(product), total_price);
    }
    else
    {
        printf("%s 的价格超出范围：%.2f\n", get_product_name(product), get_product_price(product));
    }
}

int main()
{
    // 测试代码
    Customer *customer = create_customer("Jimmy");
    Product *product1 = create_product("Computer", 5000);
    Product *product2 = create_product("Book", 200);

    buy(customer, product1);
    buy(customer, product2);

    // 释放内存
    free(product1);
    free(product2);
    free(customer->shopping_cart->products);
    free(customer->shopping_cart);
    free(customer);

    return 0;
}

/**
jarry@jarrys-MBP c % gcc law_demeter_good_example.c 
jarry@jarrys-MBP c % ./a.out 
Computer 的价格超出范围：5000.00
Jimmy 购买了 Book 价格为 200.00
*/
