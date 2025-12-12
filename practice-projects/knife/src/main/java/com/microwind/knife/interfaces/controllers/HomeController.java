package com.microwind.knife.interfaces.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return """
                <h1>Welcome to Springboot DDD example.</h1>\
                <pre>\
                测试
                <code>\
                创建：curl -X POST "http://localhost:8080/api/orders" -H "Content-Type: application/json; charset=UTF-8" -d '{"amount": 99.99, "orderName": "Test Order1", "userId": 110110}'
                查询：curl -X GET "http://localhost:8080/api/orders/订单号"
                修改状态：curl -X PATCH "http://localhost:8080/api/orders/订单号/status" -H "Content-Type: application/json; charset=UTF-8" -d '{"status": "PAID"}'
                查询全部订单：curl -X GET "http://localhost:8080/api/orders"
                查用户订单：curl -X GET "http://localhost:8080/api/orders/user/用户ID"
                更新：curl -X PUT "http://localhost:8080/api/orders/订单号" -H "Content-Type: application/json; charset=UTF-8" -d '{"amount": 11.22, "orderName": "Test Order2", "status": "COMPLETED"}'
                删除：curl -X DELETE "http://localhost:8080/api/orders/订单号"
                </code>\
                详细：https://github.com/microwind/design-patterns/tree/main/mvx/mvc\
                </pre>""";
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello world!";
    }
}
