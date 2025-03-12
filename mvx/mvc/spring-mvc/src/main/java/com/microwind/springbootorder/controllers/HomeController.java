package com.microwind.springbootorder.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return """
                <h1>Welcome to DDD example.</h1>\
                <pre>\
                测试
                <code>\
                创建：curl -X POST "http://localhost:8080/api/orders" -H "Content-Type: application/json; charset=UTF-8" -d '{"customerName": "齐天大圣", "amount": 99.99, "orderName": "Test Order", "userId": 110110}'
                查询：curl -X GET "http://localhost:8080/api/orders/订单号"
                更新：curl -X PUT "http://localhost:8080/api/orders/订单号" -H "Content-Type: application/json; charset=UTF-8" -d '{"customerName": "孙悟空", "amount": 11.22, "orderName": "Test Order"}'
                删除：curl -X DELETE "http://localhost:8080/api/orders/订单号"
                </code>\
                详细：https://github.com/microwind/design-patterns/tree/main/mvx/mvc\
                </pre>""";
    }

    @GetMapping("/api/hello")
    public String hello() {
        return "Hello world!";
    }
}
