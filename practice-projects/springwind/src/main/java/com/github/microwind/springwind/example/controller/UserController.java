// 用户控制器
package com.github.microwind.springwind.example.controller;

import com.github.microwind.springwind.annotation.Controller;
import com.github.microwind.springwind.annotation.RequestMapping;
import com.github.microwind.springwind.annotation.Autowired;
import com.github.microwind.springwind.annotation.RequestParam;
import com.github.microwind.springwind.example.model.User;
import com.github.microwind.springwind.example.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/get")
    public String getUser(HttpServletRequest request) {
        Long id = Long.valueOf(request.getParameter("id"));
        User user = userService.getUserById(id);
        request.setAttribute("user", user);
        return "userDetail";
    }

    @RequestMapping(value = "/hello", method = "GET")
    public String hello(@RequestParam("name") String name,
                        @RequestParam(value = "age", required = false, defaultValue = "18") int age) {
        System.out.println("name=" + name + ", age=" + age);
        return "redirect:/login";
    }

    @RequestMapping(value = "/check", method = "GET")
    public String check(@RequestParam(value = "code", required = true) String code) {
        return "redirect:/success";
    }

    @RequestMapping(value = "/info", method = "GET")
    public String getUserInfo() {
        return "userInfo"; // 假设视图解析器配置了前缀/WEB-INF/views/和后缀.jsp
    }

    @RequestMapping(value = "/logout", method = "POST")
    public String logout() {
        return "redirect:/login";
    }

    @RequestMapping(value = "/data", method = "GET")
    public Map<String, Object> getUserData() {
        Map<String, Object> data = new HashMap<>();
        data.put("id", 1L);
        data.put("name", "test");
        return data;
    }
}