package com.github.microwind.userdemo.controller;

import com.github.microwind.springwind.annotation.Autowired;
import com.github.microwind.springwind.annotation.Controller;
import com.github.microwind.springwind.annotation.RequestMapping;
import com.github.microwind.springwind.web.*;
import com.github.microwind.userdemo.utils.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * 响应类型演示控制器
 * 展示 HTML、JSON、Text、JSP、Redirect 等多种响应方式
 */
@Controller
@RequestMapping("/demo")
public class DemoController {

    /**
     * HTML 响应示例
     */
    @RequestMapping("/html")
    public ViewResult htmlDemo() {
        String html = "<html>" +
                "<head><title>HTML 响应示例</title></head>" +
                "<body style='font-family: Arial; margin: 40px;'>" +
                "<h2>HTML 响应示例</h2>" +
                "<p>这是通过 <code>HtmlResult</code> 返回的 HTML 内容。</p>" +
                "<p>可以完全控制 Content-Type 和编码。</p>" +
                "<a href='/'>返回首页</a>" +
                "</body>" +
                "</html>";

        return new HtmlResult(html);
    }

    /**
     * JSON 响应示例
     */
    @RequestMapping("/json")
    public ViewResult jsonDemo() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "success");
        data.put("message", "这是通过 JsonResult 返回的 JSON 数据");
        data.put("code", 200);
        data.put("timestamp", System.currentTimeMillis());

        return new JsonResult(data);
    }

    /**
     * 文本响应示例
     */
    @RequestMapping("/text")
    public ViewResult textDemo() {
        String text = "这是纯文本响应示例\n" +
                "通过 TextResult 返回\n" +
                "Content-Type: text/plain\n" +
                "时间: " + System.currentTimeMillis();

        return new TextResult(text);
    }

    /**
     * 自定义 Content-Type 示例
     */
    @RequestMapping("/custom")
    public ViewResult customContentType() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<response>\n" +
                "  <status>success</status>\n" +
                "  <message>自定义 Content-Type 示例</message>\n" +
                "</response>";

        return new TextResult(xml)
                .contentType("application/xml;charset=UTF-8");
    }

    /**
     * 重定向示例
     */
    @RequestMapping("/redirect")
    public ViewResult redirectDemo() {
        return new RedirectResult("/");
    }

    /**
     * 自定义编码示例
     */
    @RequestMapping("/encoding")
    public ViewResult encodingDemo() {
        return new HtmlResult("<h1>自定义编码示例</h1><p>测试中文编码：你好世界！</p>")
                .encoding("UTF-8")
                .contentType("text/html;charset=UTF-8");
    }
}
