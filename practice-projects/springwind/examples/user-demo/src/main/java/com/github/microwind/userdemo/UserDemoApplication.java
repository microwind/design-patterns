package com.github.microwind.userdemo;

import com.github.microwind.springwind.SpringWindApplication;
import com.github.microwind.userdemo.controller.*;

import java.util.Arrays;

/**
 * 学生信息管理系统启动类
 * 演示SpringWind框架的基本功能：IoC容器、依赖注入、组件扫描
 *
 * 运行模式：
 * 1. Web模式：java -jar xxx.jar --web (启动嵌入式Tomcat)
 * 2. 控制台模式：java -jar xxx.jar (直接在控制台运行)
 */
public class UserDemoApplication {

    public static void main(String[] args) {
        // 使用简化的API
        SpringWindApplication app = SpringWindApplication.run(UserDemoApplication.class, args);

        // 根据启动参数决定运行模式
        if (args.length > 0 && Arrays.asList(args).contains("--web")) {
            startWebServer(app);
        } else {
            runConsoleMode(app);
        }
    }
    
    private static void startWebServer(SpringWindApplication app) {
        try {
            System.out.println("========== 学生信息管理系统 (Web模式) ==========");
            TomcatServer server = new TomcatServer(8080);
            server.start(app);
        } catch (Exception e) {
            System.err.println("启动Web服务器失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void runConsoleMode(SpringWindApplication app) {
        System.out.println("========== 学生信息管理系统 (控制台模式) ==========");
        System.out.println("启动SpringWind应用...\n");

        // 测试1: 用户登录
        System.out.println("========== 测试1: 用户登录 ==========");
        AuthController authController = app.getBean(AuthController.class);
        authController.login();
        System.out.println();

        // 测试2: 获取学生详细信息
        System.out.println("========== 测试2: 获取学生详细信息 ==========");
        StudentController studentController = app.getBean(StudentController.class);
        studentController.getDetail();
        System.out.println();

        // 测试3: 获取班级列表
        System.out.println("========== 测试3: 获取班级列表 ==========");
        ClassController classController = app.getBean(ClassController.class);
        classController.getList();
        System.out.println();

        // 测试4: 用户数据库操作 (JdbcTemplate)
        System.out.println("========== 测试4: 用户数据库操作 (JdbcTemplate) ==========");
        try {
            UserController userController = app.getBean(UserController.class);
            System.out.println("User 模块已成功注入，已可用于 Web 服务模式");
            System.out.println("Web 模式下，可通过 HTTP 接口访问用户管理功能:");
            System.out.println("  - GET  /user/list       - 获取用户列表");
            System.out.println("  - POST /user/create     - 创建用户");
            System.out.println("  - GET  /user/{id}       - 获取用户详情");
        } catch (Exception e) {
            System.err.println("注意: User 模块依赖数据库连接，请确保数据库已启动");
            System.err.println("详见: examples/user-demo/DATABASE_OPERATIONS.md");
        }
        System.out.println("\n========== 演示完成 ==========");

        // 关闭应用
        app.close();
    }
}
