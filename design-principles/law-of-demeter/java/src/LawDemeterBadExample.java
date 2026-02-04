package src;

import java.util.ArrayList;
import java.util.List;

/**
 * 这个例子违反了迪米特原则，为了方面理解，把全部类放在了一起。
 * 1. 对象职责不清晰，不单一。顾客类下单购物，还实现了价格计算逻辑。
 * 2. 对象依赖了朋友的朋友。顾客类依赖了购买朋友的朋友商品。
 */
public class LawDemeterBadExample {
    public LawDemeterBadExample() {
        return;
    }

    // 定义一个顾客类，里面直接依赖了购买商品，且包含商品统计与价格计算逻辑
    public class Customer {
        private String name;
        private List<Product> products;

        public Customer(String name) {
            this.name = name;
            this.products = new ArrayList<>();
        }

        public void buy(Product product) {
            // 违反迪米特法则：顾客类只跟购物车打交道，而不应该直接与商品类交互
            if (product.getPrice() > 1000) {
                System.out.println(product.getName() + "'s price exceeds range：" + product.getPrice());
            } else {
                products.add(product);
                double totalPrice = calculateTotalPrice();
                System.out.println(name + " purchased " + product.getName() + " for " + product.getPrice());
                System.out.println("Total price: $" + totalPrice);
            }
        }

        private double calculateTotalPrice() {
            double totalPrice = 0.0;
            for (Product product : products) {
                totalPrice += product.getPrice();
            }
            return totalPrice;
        }
    }

    public class Product {
        private String name;
        private double price;

        public Product(String name, double price) {
            this.name = name;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }
    }
    
    public static void main(String[] args) {
        System.out.println("Testing LawDemeterBadExample...");
        LawDemeterBadExample example = new LawDemeterBadExample();
        Customer customer = example.new Customer("Jimmy");
        
        // Test buying products
        customer.buy(example.new Product("Computer", 5000L));
        customer.buy(example.new Product("Book", 200L));
    }
}
