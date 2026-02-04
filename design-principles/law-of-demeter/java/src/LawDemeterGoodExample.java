package src;

/**
 * 这个例子符合迪米特法则。
 * 1. 顾客类只与购物车（直接朋友）交互，不直接与商品（陌生对象）交互。
 * 2. 购物车作为中介，处理与商品相关的逻辑。
 * 3. 这样降低了对象之间的耦合度，提高了代码的可维护性。
 */
public class LawDemeterGoodExample {
    public LawDemeterGoodExample() {
        return;
    }

    /**
     * Customer类，减轻逻辑，调用购物车来实现商品的购买，不直接跟商品交互。
     */
    public class Customer {
        private String name;
        private ShoppingCart shoppingCart;

        public Customer(String name) {
            this.name = name;
            this.shoppingCart = new ShoppingCart();
        }

        public void buy(Product product) {
            // 只跟购物车打交道，且不用了解购物车实现细节，只是调用购物车方法
            if (shoppingCart.validateProduct(product)) {
                shoppingCart.addProduct(product);
                double totalPrice = shoppingCart.calculateTotalPrice();
                System.out.println(name + " purchased " + product.getName() + " for " + totalPrice);
            } else {
                System.out.println(product.getName() + "'s price exceeds range：" + product.getPrice());
            }
        }
    }

    /**
     * ShoppingCart类，负责商品校验价格计算等逻辑，是顾客和商品之间的桥梁
     */
    public class ShoppingCart {
        private java.util.List<Product> products;

        public ShoppingCart() {
            this.products = new java.util.ArrayList<>();
        }

        public void addProduct(Product product) {
            products.add(product);
        }

        public double calculateTotalPrice() {
            double totalPrice = 0.0;
            for (Product product : products) {
                totalPrice += product.getPrice();
            }
            return totalPrice;
        }

        public boolean validateProduct(Product product) {
            double price = product.getPrice();
            return price > 1000;
        }
    }

    /**
     * Product对象类，只有商品本身信息
     */
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
        System.out.println("Testing LawDemeterGoodExample...");
        LawDemeterGoodExample example = new LawDemeterGoodExample();
        
        // Create Customer
        Customer customer = example.new Customer("Jimmy");
        
        // Test buying products
        Product product1 = example.new Product("Computer", 5000);
        customer.buy(product1);
        
        Product product2 = example.new Product("Book", 200);
        customer.buy(product2);
    }
}
