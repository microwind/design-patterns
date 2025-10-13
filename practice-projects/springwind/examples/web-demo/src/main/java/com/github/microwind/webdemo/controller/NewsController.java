package com.github.microwind.webdemo.controller;

import com.github.microwind.springwind.annotation.Controller;
import com.github.microwind.springwind.annotation.RequestMapping;
import com.github.microwind.springwind.annotation.Autowired;
import com.github.microwind.webdemo.model.Article;
import com.github.microwind.webdemo.service.ArticleService;
import java.util.List;

/**
 * 新闻控制器 - 新闻资讯页面
 */
@Controller
@RequestMapping("/news")
public class NewsController {

    @Autowired
    private ArticleService articleService;

    @RequestMapping("/list")
    public String list() {
        System.out.println("=== 新闻资讯 ===");

        // 获取新闻栏目（columnId=2）的所有文章
        List<Article> newsList = articleService.getArticlesByColumnId(2L);

        System.out.println("最新资讯：");
        for (Article news : newsList) {
            System.out.println("\n【" + news.getTitle() + "】");
            System.out.println("  作者：" + news.getAuthor() + " | 发布时间：" + news.getPublishTime());
            System.out.println("  " + news.getContent());
        }

        return "新闻列表已展示，共 " + newsList.size() + " 条新闻";
    }
}
