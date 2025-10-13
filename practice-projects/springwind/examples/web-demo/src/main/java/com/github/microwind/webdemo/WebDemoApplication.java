package com.github.microwind.webdemo;

import com.github.microwind.springwind.SpringWindApplication;
import com.github.microwind.webdemo.controller.HomeController;
import com.github.microwind.webdemo.controller.ProductController;
import com.github.microwind.webdemo.controller.NewsController;
import com.github.microwind.webdemo.controller.AdminController;

/**
 * 企业网站内容管理系统启动类
 * 演示SpringWind框架的实际应用场景
 *
 * 运行模式：
 * 1. Web模式：java -jar xxx.jar --web (启动嵌入式Tomcat)
 * 2. 控制台模式：java -jar xxx.jar (直接在控制台运行)
 */
public class WebDemoApplication {

    public static void main(String[] args) {
        // 检查启动参数，决定运行模式
        if (args.length > 0 && "--web".equals(args[0])) {
            // Web模式：启动嵌入式Tomcat
            startWebServer();
        } else {
            // 控制台模式：直接运行测试
            runConsoleMode();
        }
    }

    /**
     * Web模式：启动嵌入式Tomcat服务器
     */
    private static void startWebServer() {
        try {
            System.out.println("========== 春风公司企业网站 (Web模式) ==========");
            TomcatServer server = new TomcatServer(8080);
            server.start(WebDemoApplication.class.getName());
        } catch (Exception e) {
            System.err.println("启动Web服务器失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 控制台模式：直接运行测试
     */
    private static void runConsoleMode() {
        System.out.println("========== 春风公司企业网站 (控制台模式) ==========");
        System.out.println("启动SpringWind应用...\n");

        // 启动SpringWind应用
        SpringWindApplication app = SpringWindApplication.run(WebDemoApplication.class);

        System.out.println("SpringWind容器启动成功！\n");

        // 测试1: 访问首页
        System.out.println("========== 测试1: 访问首页 ==========");
        HomeController homeController = app.getBean(HomeController.class);
        homeController.index();
        System.out.println();

        // 测试2: 访问产品中心
        System.out.println("========== 测试2: 访问产品中心 ==========");
        ProductController productController = app.getBean(ProductController.class);
        productController.list();
        System.out.println();

        // 测试3: 访问新闻资讯
        System.out.println("========== 测试3: 访问新闻资讯 ==========");
        NewsController newsController = app.getBean(NewsController.class);
        newsController.list();
        System.out.println();

        // 测试4: 后台管理 - 查看栏目
        System.out.println("========== 测试4: 后台管理 - 查看栏目 ==========");
        AdminController adminController = app.getBean(AdminController.class);
        adminController.listColumns();
        System.out.println();

        // 测试5: 后台管理 - 创建栏目
        System.out.println("========== 测试5: 后台管理 - 创建栏目 ==========");
        adminController.createColumn();
        System.out.println();

        // 测试6: 后台管理 - 发布文章
        System.out.println("========== 测试6: 后台管理 - 发布文章 ==========");
        adminController.publishArticle();
        System.out.println();

        System.out.println("========== 演示完成 ==========");

        // 关闭应用
        app.close();
    }
}
