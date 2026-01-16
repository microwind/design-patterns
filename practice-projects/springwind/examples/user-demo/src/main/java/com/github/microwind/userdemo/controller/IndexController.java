package com.github.microwind.userdemo.controller;

import com.github.microwind.springwind.annotation.Controller;
import com.github.microwind.springwind.annotation.RequestMapping;
import com.github.microwind.springwind.web.HtmlResult;
import com.github.microwind.springwind.web.ViewResult;

/**
 * 首页控制器
 */
@Controller
@RequestMapping("/")
public class IndexController {

    @RequestMapping("")
    public ViewResult index() {
        String html = "<html>" +
                "<head><title>信息管理系统</title></head>" +
                "<body style='font-family: Arial, sans-serif; margin: 40px;'>" +
                "<h1 style='color: #333;'>欢迎使用学生信息管理系统</h1>" +
                "<p>这是一个基于 SpringWind 框架构建的示例应用，模拟了 Spring MVC 框架的核心功能。</p>" +
                "<h2>可用接口：</h2>" +
                "<h3 style='color: #666;'>原有示例（内存存储）</h3>" +
                "<ul>" +
                "<li><a href='/auth/login'>用户登录</a> - /auth/login</li>" +
                "<li><a href='/student/detail'>学生详情</a> - /student/detail</li>" +
                "<li><a href='/class/list'>班级列表</a> - /class/list</li>" +
                "</ul>" +
                "<h3 style='color: #666;'>用户管理 API（数据库操作）</h3>" +
                "<ul>" +
                "<li><a href='/user/list'>用户列表</a> - GET /user/list</li>" +
                "<li><a href='/user/list?page=1&pageSize=10'>用户列表（分页）</a> - GET /user/list?page=1&pageSize=10</li>" +
                "<li><a href='/user/count'>用户总数</a> - GET /user/count</li>" +
                "<li>根据ID获取用户 - GET /user/get?id=1</li>" +
                "<li>根据用户名获取用户 - GET /user/get-by-name?name=admin</li>" +
                "<li>创建用户 - POST /user/create</li>" +
                "<li>更新用户 - POST /user/update</li>" +
                "<li>删除用户 - POST /user/delete?id=1</li>" +
                "<li>用户登录验证 - POST /user/login</li>" +
                "</ul>" +
                "<h3 style='color: #666;'>其他示例</h3>" +
                "<ul>" +
                "<li><a href='/demo/html'>HTML 响应示例</a> - /demo/html</li>" +
                "<li><a href='/demo/json'>JSON 响应示例</a> - /demo/json</li>" +
                "<li><a href='/demo/text'>文本响应示例</a> - /demo/text</li>" +
                "</ul>" +
                "<hr>" +
                "<p style='color: #666; font-size: 14px;'>Powered by SpringWind Framework - 模拟 Spring MVC 的轻量级框架</p>" +
                "</body>" +
                "</html>";

        return new HtmlResult(html);
    }
}
