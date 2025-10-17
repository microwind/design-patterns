package com.github.microwind.userdemo.controller;

import com.github.microwind.springwind.annotation.Controller;
import com.github.microwind.springwind.annotation.RequestMapping;
import com.github.microwind.springwind.web.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器 - 验证 ViewResult 功能
 */
@Controller
@RequestMapping("/test")
public class TestController {

    @RequestMapping("/html")
    public ViewResult testHtml() {
        return new HtmlResult("<h1>HTML Test</h1><p>This works!</p>");
    }

    @RequestMapping("/json")
    public ViewResult testJson() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "success");
        data.put("message", "JSON works");
        return new JsonResult(data);
    }

    @RequestMapping("/text")
    public ViewResult testText() {
        return new TextResult("Plain text works!");
    }
}
