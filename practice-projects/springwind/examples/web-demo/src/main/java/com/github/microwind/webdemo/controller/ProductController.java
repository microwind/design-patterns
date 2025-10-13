package com.github.microwind.webdemo.controller;

import com.github.microwind.springwind.annotation.Controller;
import com.github.microwind.springwind.annotation.RequestMapping;
import com.github.microwind.springwind.annotation.Autowired;
import com.github.microwind.webdemo.model.Article;
import com.github.microwind.webdemo.service.ArticleService;
import java.util.List;

/**
 * 产品控制器 - 产品介绍页面
 */
@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ArticleService articleService;

    @RequestMapping("/list")
    public String list() {
        System.out.println("=== 产品中心 ===");

        // 获取产品栏目（columnId=1）的所有文章
        List<Article> products = articleService.getArticlesByColumnId(1L);

        System.out.println("我们的产品：");
        for (Article product : products) {
            System.out.println("\n【" + product.getTitle() + "】");
            System.out.println("  " + product.getContent());
        }

        return "产品列表已展示，共 " + products.size() + " 个产品";
    }
}
