// 用户控制器
package com.github.microwind.springwind.example.controller;

import com.github.microwind.springwind.annotation.Controller;
import com.github.microwind.springwind.annotation.RequestMapping;
import com.github.microwind.springwind.annotation.Autowired;
import com.github.microwind.springwind.example.model.User;
import com.github.microwind.springwind.example.service.UserService;

import javax.servlet.http.HttpServletRequest;
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

    @RequestMapping(value = "/info", method = "GET")
    public String getUserInfo() {
        return "userInfo";
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