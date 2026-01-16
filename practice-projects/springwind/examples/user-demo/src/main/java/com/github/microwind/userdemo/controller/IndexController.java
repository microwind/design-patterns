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
                "<p>这是一个基于 SpringWind 框架构建的示例应用。</p>" +
                "<h2>可用接口：</h2>" +
                "<ul>" +
                "<li><a href='/auth/login'>用户登录</a> - /auth/login</li>" +
                "<li><a href='/student/detail'>学生详情</a> - /student/detail</li>" +
                "<li><a href='/class/list'>班级列表</a> - /class/list</li>" +
                "<li><a href='/demo/html'>HTML 响应示例</a> - /demo/html</li>" +
                "<li><a href='/demo/json'>JSON 响应示例</a> - /demo/json</li>" +
                "<li><a href='/demo/text'>文本响应示例</a> - /demo/text</li>" +
                "</ul>" +
                "<hr>" +
                "<p style='color: #666; font-size: 14px;'>Powered by SpringWind Framework</p>" +
                "</body>" +
                "</html>";

        return new HtmlResult(html);
    }
}
