package com.github.microwind.webdemo.controller;

import com.github.microwind.springwind.annotation.Controller;
import com.github.microwind.springwind.annotation.RequestMapping;
import com.github.microwind.springwind.annotation.Autowired;
import com.github.microwind.webdemo.model.Article;
import com.github.microwind.webdemo.service.ArticleService;
import com.github.microwind.webdemo.utils.ResponseUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 新闻控制器 - 新闻资讯页面
 */
@Controller
@RequestMapping("/news")
public class NewsController {

    @Autowired
    private ArticleService articleService;

    /**
     * 获取新闻列表
     * GET /news/list
     */
    @RequestMapping("/list")
    public void list(HttpServletRequest request, HttpServletResponse response) {
        try {
            System.out.println("=== 新闻资讯 ===");

            // 获取新闻栏目（columnId=2）的所有文章
            List<Article> newsList = articleService.getArticlesByColumnId(2L);

            System.out.println("最新资讯：");
            for (Article news : newsList) {
                System.out.println("\n【" + news.getTitle() + "】");
                System.out.println("  作者：" + news.getAuthor() + " | 发布时间：" + news.getPublishTime());
                System.out.println("  " + news.getContent());
            }

            ResponseUtils.sendJsonResponse(response, HttpServletResponse.SC_OK,
                "新闻列表获取成功，共 " + newsList.size() + " 条新闻", newsList, null);
        } catch (Exception e) {
            try {
                ResponseUtils.sendJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "获取新闻列表失败: " + e.getMessage(), null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
