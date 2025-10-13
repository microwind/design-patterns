package com.github.microwind.userdemo.controller;

import com.github.microwind.springwind.annotation.Controller;
import com.github.microwind.springwind.annotation.RequestMapping;
import com.github.microwind.springwind.annotation.Autowired;
import com.github.microwind.userdemo.service.StudentService;

/**
 * 认证控制器 - 处理用户登录
 */
@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private StudentService studentService;

    @RequestMapping("/login")
    public String login() {
        // 模拟登录
        boolean success = studentService.login("admin", "123456");
        if (success) {
            System.out.println("登录成功！");
            return "登录成功";
        } else {
            System.out.println("登录失败！");
            return "登录失败";
        }
    }
}
