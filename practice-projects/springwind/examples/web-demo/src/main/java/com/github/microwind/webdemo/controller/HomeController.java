package com.github.microwind.webdemo.controller;

import com.github.microwind.springwind.annotation.Controller;
import com.github.microwind.springwind.annotation.RequestMapping;
import com.github.microwind.springwind.annotation.Autowired;
import com.github.microwind.webdemo.model.Article;
import com.github.microwind.webdemo.service.ArticleService;
import com.github.microwind.webdemo.utils.ResponseUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 首页控制器
 */
@Controller
@RequestMapping("/")
public class HomeController {

    @Autowired
    private ArticleService articleService;

    @RequestMapping("")
    public void index(HttpServletRequest request, HttpServletResponse response) {
        ResponseUtils.sendResponse(response, HttpServletResponse.SC_OK,
                "欢迎访问春风公司官网", null, null);
    }

    /**
     * 首页
     * GET /home
     */
    @RequestMapping("/home")
    public void home(HttpServletRequest request, HttpServletResponse response) {
        try {
            System.out.println("=== 欢迎访问春风公司官网 ===");
            System.out.println("公司简介：春风公司成立于2020年，是一家专注于云计算和大数据领域的创新型科技企业。");
            System.out.println("我们致力于为客户提供优质的技术服务和解决方案。");

            // 显示最新文章
            List<Article> recentArticles = articleService.getAllArticles();
            System.out.println("\n最新资讯：");
            recentArticles.stream()
                    .limit(3)
                    .forEach(a -> System.out.println("  - " + a.getTitle()));

            // 构建响应数据
            Map<String, Object> homeData = new HashMap<>();
            homeData.put("companyName", "春风公司");
            homeData.put("description", "春风公司成立于2010年，是一家专注于云计算和大数据领域的创新型企业。");
            homeData.put("recentArticles", recentArticles.stream().limit(3).collect(Collectors.toList()));

            ResponseUtils.sendResponse(response, HttpServletResponse.SC_OK,
                    "首页数据获取成功", homeData, null);
        } catch (Exception e) {
            try {
                ResponseUtils.sendJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "获取首页数据失败: " + e.getMessage(), null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
