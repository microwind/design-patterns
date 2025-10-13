package com.github.microwind.webdemo.controller;

import com.github.microwind.springwind.annotation.Controller;
import com.github.microwind.springwind.annotation.RequestMapping;
import com.github.microwind.springwind.annotation.Autowired;
import com.github.microwind.webdemo.model.Article;
import com.github.microwind.webdemo.model.Column;
import com.github.microwind.webdemo.service.ArticleService;
import com.github.microwind.webdemo.service.ColumnService;
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
     */
    @RequestMapping("/columns")
    public String listColumns() {
        System.out.println("=== 栏目管理 ===");

        List<Column> columns = columnService.getAllColumns();
        System.out.println("当前栏目列表：");
        for (Column column : columns) {
            System.out.println("  " + column);
        }

        return "栏目列表查询完成，共 " + columns.size() + " 个栏目";
    }

    /**
     * 创建新栏目
     */
    @RequestMapping("/createColumn")
    public String createColumn() {
        System.out.println("=== 创建新栏目 ===");

        Column newColumn = new Column();
        newColumn.setName("联系我们");
        newColumn.setDescription("联系方式和地址");
        newColumn.setSort(4);

        columnService.saveColumn(newColumn);
        System.out.println("新栏目创建成功：" + newColumn.getName());

        return "栏目创建成功：" + newColumn.getName();
    }

    /**
     * 发布新文章
     */
    @RequestMapping("/publishArticle")
    public String publishArticle() {
        System.out.println("=== 发布新文章 ===");

        Article newArticle = new Article();
        newArticle.setTitle("春风公司完成新一轮融资");
        newArticle.setContent("春风公司宣布完成B轮融资，将继续加大研发投入，提升产品竞争力。");
        newArticle.setColumnId(2L); // 新闻资讯栏目
        newArticle.setAuthor("新闻中心");

        articleService.saveArticle(newArticle);
        System.out.println("新文章发布成功：" + newArticle.getTitle());
        System.out.println("文章ID：" + newArticle.getId());

        return "文章发布成功：" + newArticle.getTitle();
    }
}
