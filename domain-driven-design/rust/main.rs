mod domain;
mod repository;
mod service;

use service::order_service::OrderService;
use std::io;

fn main() {
    let mut order_service = OrderService::new();

    loop {
        println!("\n订单管理系统");
        println!("1. 创建订单");
        println!("2. 取消订单");
        println!("3. 查询订单");
        println!("4. 查看订单历史");
        println!("5. 退出");
        println!("请选择操作: ");

        let mut choice = String::new();
        io::stdin().read_line(&mut choice).expect("Failed to read line");
        let choice = choice.trim();

        match choice {
            "1" => {
                let id = read_input("请输入订单 ID: ");
                let customer_name = read_input("请输入客户名称: ");
                let amount = read_input("请输入订单金额: ");
                let amount: f64 = amount.parse().expect("请输入有效的金额");
                order_service.create_order(id.parse().unwrap(), customer_name, amount);
            }
            "2" => {
                let id = read_input("请输入要取消的订单 ID: ");
                order_service.cancel_order(id.parse().unwrap());
            }
            "3" => {
                let id = read_input("请输入要查询的订单 ID: ");
                order_service.query_order(id.parse().unwrap());
            }
            "4" => {
                order_service.view_order_history();
            }
            "5" => {
                println!("退出系统，清理资源...");
                order_service.clear_orders(); // 调用清理方法
                break;
            }
            _ => {
                println!("无效选择，请重新输入。");
            }
        }
    }
}

fn read_input(prompt: &str) -> String {
    println!("{}", prompt);
    let mut input = String::new();
    io::stdin().read_line(&mut input).expect("Failed to read line");
    input.trim().to_string()
}

/*
jarry@MacBook-Pro rust % cargo build
   Compiling rust_ddd_project v0.1.0 (/Users/jarry/github/design-patterns/domain-driven-design/rust)
    Finished `dev` profile [unoptimized + debuginfo] target(s) in 0.64s
jarry@MacBook-Pro rust % cargo run  
    Finished `dev` profile [unoptimized + debuginfo] target(s) in 0.00s
     Running `target/debug/rust_ddd_project`

订单管理系统
1. 创建订单
2. 取消订单
3. 查询订单
4. 查看订单历史
5. 退出
请选择操作: 
*/