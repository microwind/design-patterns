// repository/order_repository.rs
// repository/order_repository.rs
use crate::domain::order::Order;
use std::collections::HashMap;

pub struct OrderRepository {
    orders: HashMap<u32, Order>,
}

impl OrderRepository {
    pub fn new() -> Self {
        OrderRepository {
            orders: HashMap::new(),
        }
    }

    pub fn save(&mut self, order: Order) {
        self.orders.insert(order.get_id(), order);
    }

    pub fn find_by_id(&self, id: u32) -> Option<&Order> {
        self.orders.get(&id)
    }

    pub fn find_all(&self) -> Vec<&Order> {
        self.orders.values().collect()
    }

    pub fn clear(&mut self) {
        self.orders.clear();
        println!("所有订单已清理");
    }
}