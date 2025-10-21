package com.github.microwind.webdemo.controller;

import com.github.microwind.springwind.annotation.Controller;
import com.github.microwind.springwind.annotation.RequestMapping;
import com.github.microwind.springwind.annotation.Autowired;
import com.github.microwind.webdemo.model.Article;
import com.github.microwind.webdemo.service.ArticleService;
import com.github.microwind.webdemo.utils.ResponseUtils;
import com.github.microwind.webdemo.utils.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 文章管理控制器 - 完整的CRUD操作
 */
@Controller
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    /**
     * 获取所有文章列表
     * GET /article/list
     */
    @RequestMapping("/list")
    public void list(HttpServletRequest request, HttpServletResponse response) {
        try {
            List<Article> articles = articleService.getAllArticles();
            ResponseUtils.sendJsonResponse(response, HttpServletResponse.SC_OK,
                "获取文章列表成功", articles, null);
        } catch (Exception e) {
            try {
                ResponseUtils.sendJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "获取文章列表失败: " + e.getMessage(), null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 根据ID获取文章详情
     * GET /article/detail?id=1
     */
    @RequestMapping("/detail")
    public void detail(HttpServletRequest request, HttpServletResponse response) {
        try {
            String idStr = request.getParameter("id");
            if (idStr == null || idStr.trim().isEmpty()) {
                ResponseUtils.sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "缺少文章ID参数", null);
                return;
            }

            Long id = Long.parseLong(idStr);
            if (id <= 0) {
                ResponseUtils.sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "文章ID无效", null);
                return;
            }

            Article article = articleService.getArticleById(id);
            if (article != null) {
                ResponseUtils.sendJsonResponse(response, HttpServletResponse.SC_OK,
                    "获取文章详情成功", article, null);
            } else {
                ResponseUtils.sendJsonError(response, HttpServletResponse.SC_NOT_FOUND,
                    "文章不存在", null);
            }
        } catch (Exception e) {
            try {
                ResponseUtils.sendJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "获取文章详情失败: " + e.getMessage(), null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 根据栏目ID获取文章列表
     * GET /article/column?columnId=1
     */
    @RequestMapping("/column")
    public void listByColumn(HttpServletRequest request, HttpServletResponse response) {
        try {
            String columnIdStr = request.getParameter("columnId");
            if (columnIdStr == null || columnIdStr.trim().isEmpty()) {
                ResponseUtils.sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "缺少栏目ID参数", null);
                return;
            }

            Long columnId = Long.parseLong(columnIdStr);
            if (columnId <= 0) {
                ResponseUtils.sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "栏目ID无效", null);
                return;
            }

            List<Article> articles = articleService.getArticlesByColumnId(columnId);
            ResponseUtils.sendJsonResponse(response, HttpServletResponse.SC_OK,
                "获取栏目文章成功", articles, null);
        } catch (Exception e) {
            try {
                ResponseUtils.sendJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "获取栏目文章失败: " + e.getMessage(), null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 创建新文章
     * POST /article/create
     * Request Body: {"title":"文章标题","content":"文章内容","columnId":1,"author":"作者"}
     */
    @RequestMapping("/create")
    public void create(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 只处理POST请求
            if (!"POST".equalsIgnoreCase(request.getMethod())) {
                ResponseUtils.sendJsonError(response, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                    "只支持POST请求", null);
                return;
            }

            // 从请求体读取JSON数据
            Article article = RequestUtils.readJsonBody(request, Article.class);

            // 验证必填字段
            if (article.getTitle() == null || article.getTitle().trim().isEmpty()) {
                ResponseUtils.sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "文章标题不能为空", null);
                return;
            }

            // 保存文章
            articleService.saveArticle(article);

            ResponseUtils.sendJsonResponse(response, HttpServletResponse.SC_CREATED,
                "文章创建成功", article, null);
        } catch (Exception e) {
            try {
                ResponseUtils.sendJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "创建文章失败: " + e.getMessage(), null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 更新文章
     * PUT /article/update
     * Request Body: {"id":1,"title":"新标题","content":"新内容","columnId":1,"author":"作者"}
     */
    @RequestMapping("/update")
    public void update(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 只处理PUT请求
            if (!"PUT".equalsIgnoreCase(request.getMethod())) {
                ResponseUtils.sendJsonError(response, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                    "只支持PUT请求", null);
                return;
            }

            // 从请求体读取JSON数据
            Article article = RequestUtils.readJsonBody(request, Article.class);

            // 验证ID
            if (article.getId() == null) {
                ResponseUtils.sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "文章ID不能为空", null);
                return;
            }

            // 更新文章
            boolean success = articleService.updateArticle(article);

            if (success) {
                ResponseUtils.sendJsonResponse(response, HttpServletResponse.SC_OK,
                    "文章更新成功", article, null);
            } else {
                ResponseUtils.sendJsonError(response, HttpServletResponse.SC_NOT_FOUND,
                    "文章不存在", null);
            }
        } catch (Exception e) {
            try {
                ResponseUtils.sendJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "更新文章失败: " + e.getMessage(), null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 删除文章
     * DELETE /article/delete?id=1
     */
    @RequestMapping("/delete")
    public void delete(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 只处理DELETE请求
            if (!"DELETE".equalsIgnoreCase(request.getMethod())) {
                ResponseUtils.sendJsonError(response, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                    "只支持DELETE请求", null);
                return;
            }

            String idStr = request.getParameter("id");
            if (idStr == null || idStr.trim().isEmpty()) {
                ResponseUtils.sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "缺少文章ID参数", null);
                return;
            }

            Long id = Long.parseLong(idStr);
            if (id <= 0) {
                ResponseUtils.sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "文章ID无效", null);
                return;
            }

            // 删除文章
            boolean success = articleService.deleteArticle(id);

            if (success) {
                ResponseUtils.sendJsonResponse(response, HttpServletResponse.SC_OK,
                    "文章删除成功", null, null);
            } else {
                ResponseUtils.sendJsonError(response, HttpServletResponse.SC_NOT_FOUND,
                    "文章不存在", null);
            }
        } catch (Exception e) {
            try {
                ResponseUtils.sendJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "删除文章失败: " + e.getMessage(), null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
