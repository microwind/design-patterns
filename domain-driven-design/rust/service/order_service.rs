// service/order_service.rs
use crate::domain::order::Order;
use crate::repository::order_repository::OrderRepository;

pub struct OrderService {
    order_repository: OrderRepository,
}

impl OrderService {
    pub fn new() -> Self {
        OrderService {
            order_repository: OrderRepository::new(),
        }
    }

    pub fn create_order(&mut self, id: u32, customer_name: String, amount: f64) {
        let order = Order::new(id, customer_name, amount);
        self.order_repository.save(order);
        println!("订单 ID {} 创建成功", id);
    }

    pub fn cancel_order(&mut self, id: u32) {
        if let Some(order) = self.order_repository.find_by_id(id) {
            let mut order = order.clone();
            order.cancel();
            self.order_repository.save(order);
        } else {
            println!("未找到 ID {}", id);
        }
    }

    pub fn query_order(&self, id: u32) {
        if let Some(order) = self.order_repository.find_by_id(id) {
            order.display();
        } else {
            println!("未找到 ID {}", id);
        }
    }

    pub fn view_order_history(&self) {
        let orders = self.order_repository.find_all();
        if orders.is_empty() {
            println!("暂无订单历史记录。");
        } else {
            for order in orders {
                order.display();
            }
        }
    }

    pub fn clear_orders(&mut self) {
        self.order_repository.clear();
    }
}
