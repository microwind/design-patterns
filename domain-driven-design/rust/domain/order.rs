// domain/order.rs
// domain/order.rs
#[derive(Debug, Clone)] // 添加 Clone 特性
pub struct Order {
    id: u32,
    customer_name: String,
    amount: f64,
    status: String,
}

impl Order {
    pub fn new(id: u32, customer_name: String, amount: f64) -> Self {
        Order {
            id,
            customer_name,
            amount,
            status: String::from("CREATED"),
        }
    }

    pub fn cancel(&mut self) {
        if self.status == "CREATED" {
            self.status = String::from("CANCELED");
            println!("订单 ID {} 已取消", self.id);
        } else {
            println!("订单 ID {} 已经取消，无法重复操作", self.id);
        }
    }

    pub fn display(&self) {
        println!("订单 ID: {}", self.id);
        println!("客户名称: {}", self.customer_name);
        println!("订单金额: {:.2}", self.amount);
        println!("订单状态: {}", self.status);
    }

    pub fn get_id(&self) -> u32 {
        self.id
    }
}