package com.github.microwind.webdemo.controller;

import com.github.microwind.springwind.annotation.Controller;
import com.github.microwind.springwind.annotation.RequestMapping;
import com.github.microwind.springwind.annotation.Autowired;
import com.github.microwind.webdemo.model.Article;
import com.github.microwind.webdemo.service.ArticleService;
import java.util.List;

/**
 * 首页控制器
 */
@Controller
@RequestMapping("/home")
public class HomeController {

    @Autowired
    private ArticleService articleService;

    @RequestMapping("/index")
    public String index() {
        System.out.println("=== 欢迎访问春风公司官网 ===");
        System.out.println("公司简介：春风公司成立于2010年，是一家专注于云计算和大数据领域的创新型企业。");
        System.out.println("我们致力于为客户提供优质的技术服务和解决方案。");

        // 显示最新文章
        List<Article> recentArticles = articleService.getAllArticles();
        System.out.println("\n最新资讯：");
        recentArticles.stream()
            .limit(3)
            .forEach(a -> System.out.println("  - " + a.getTitle()));

        return "首页内容已展示";
    }
}
