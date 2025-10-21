package com.github.microwind.webdemo.controller;

import com.github.microwind.springwind.annotation.Controller;
import com.github.microwind.springwind.annotation.RequestMapping;
import com.github.microwind.springwind.annotation.Autowired;
import com.github.microwind.webdemo.model.Article;
import com.github.microwind.webdemo.model.Column;
import com.github.microwind.webdemo.service.ArticleService;
import com.github.microwind.webdemo.service.ColumnService;
import com.github.microwind.webdemo.utils.ResponseUtils;
import com.github.microwind.webdemo.utils.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 管理控制器 - 后台管理功能
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ColumnService columnService;

    @Autowired
    private ArticleService articleService;

    /**
     * 查看所有栏目
     * GET /admin/columns
     */
    @RequestMapping("/columns")
    public void listColumns(HttpServletRequest request, HttpServletResponse response) {
        try {
            System.out.println("=== 栏目管理 ===");

            List<Column> columns = columnService.getAllColumns();
            System.out.println("当前栏目列表：");
            for (Column column : columns) {
                System.out.println("  " + column);
            }

            ResponseUtils.sendJsonResponse(response, HttpServletResponse.SC_OK,
                "栏目列表查询完成，共 " + columns.size() + " 个栏目", columns, null);
        } catch (Exception e) {
            try {
                ResponseUtils.sendJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "获取栏目列表失败: " + e.getMessage(), null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 创建新栏目
     * POST /admin/createColumn
     * Request Body: {"name":"栏目名称","description":"描述","sort":1}
     */
    @RequestMapping("/createColumn")
    public void createColumn(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 兼容控制台模式：如果 request 为 null，使用默认数据
            if (request == null) {
                System.out.println("=== 创建新栏目 ===");
                Column newColumn = new Column();
                newColumn.setName("联系我们");
                newColumn.setDescription("联系方式和地址");
                newColumn.setSort(4);
                columnService.saveColumn(newColumn);
                System.out.println("新栏目创建成功：" + newColumn.getName());
                if (response != null) {
                    ResponseUtils.sendJsonResponse(response, HttpServletResponse.SC_CREATED,
                        "栏目创建成功：" + newColumn.getName(), newColumn, null);
                }
                return;
            }

            if (!"POST".equalsIgnoreCase(request.getMethod())) {
                ResponseUtils.sendJsonError(response, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                    "只支持POST请求", null);
                return;
            }

            System.out.println("=== 创建新栏目 ===");

            Column newColumn = RequestUtils.readJsonBody(request, Column.class);

            if (newColumn.getName() == null || newColumn.getName().trim().isEmpty()) {
                ResponseUtils.sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "栏目名称不能为空", null);
                return;
            }

            columnService.saveColumn(newColumn);
            System.out.println("新栏目创建成功：" + newColumn.getName());

            ResponseUtils.sendJsonResponse(response, HttpServletResponse.SC_CREATED,
                "栏目创建成功：" + newColumn.getName(), newColumn, null);
        } catch (Exception e) {
            try {
                ResponseUtils.sendJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "创建栏目失败: " + e.getMessage(), null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 发布新文章
     * POST /admin/publishArticle
     * Request Body: {"title":"文章标题","content":"内容","columnId":2,"author":"作者"}
     */
    @RequestMapping("/publishArticle")
    public void publishArticle(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 兼容控制台模式：如果 request 为 null，使用默认数据
            if (request == null) {
                System.out.println("=== 发布新文章 ===");
                Article newArticle = new Article();
                newArticle.setTitle("春风公司完成新一轮融资");
                newArticle.setContent("春风公司宣布完成B轮融资，将继续加大研发投入，提升产品竞争力。");
                newArticle.setColumnId(2L);
                newArticle.setAuthor("新闻中心");
                articleService.saveArticle(newArticle);
                System.out.println("新文章发布成功：" + newArticle.getTitle());
                System.out.println("文章ID：" + newArticle.getId());
                if (response != null) {
                    ResponseUtils.sendJsonResponse(response, HttpServletResponse.SC_CREATED,
                        "文章发布成功：" + newArticle.getTitle(), newArticle, null);
                }
                return;
            }

            if (!"POST".equalsIgnoreCase(request.getMethod())) {
                ResponseUtils.sendJsonError(response, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                    "只支持POST请求", null);
                return;
            }

            System.out.println("=== 发布新文章 ===");

            Article newArticle = RequestUtils.readJsonBody(request, Article.class);

            if (newArticle.getTitle() == null || newArticle.getTitle().trim().isEmpty()) {
                ResponseUtils.sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "文章标题不能为空", null);
                return;
            }

            articleService.saveArticle(newArticle);
            System.out.println("新文章发布成功：" + newArticle.getTitle());
            System.out.println("文章ID：" + newArticle.getId());

            ResponseUtils.sendJsonResponse(response, HttpServletResponse.SC_CREATED,
                "文章发布成功：" + newArticle.getTitle(), newArticle, null);
        } catch (Exception e) {
            try {
                ResponseUtils.sendJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "发布文章失败: " + e.getMessage(), null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
