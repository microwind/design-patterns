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
 * 产品控制器 - 产品介绍页面
 */
@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ArticleService articleService;

    /**
     * 获取产品列表
     * GET /product/list
     */
    @RequestMapping("/list")
    public void list(HttpServletRequest request, HttpServletResponse response) {
        try {
            System.out.println("=== 产品中心 ===");

            // 获取产品栏目（columnId=1）的所有文章
            List<Article> products = articleService.getArticlesByColumnId(1L);

            System.out.println("我们的产品：");
            for (Article product : products) {
                System.out.println("\n【" + product.getTitle() + "】");
                System.out.println("  " + product.getContent());
            }

            ResponseUtils.sendJsonResponse(response, HttpServletResponse.SC_OK,
                "产品列表获取成功，共 " + products.size() + " 个产品", products, null);
        } catch (Exception e) {
            try {
                ResponseUtils.sendJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "获取产品列表失败: " + e.getMessage(), null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
