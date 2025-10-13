package com.github.microwind.userdemo;

import com.github.microwind.springwind.SpringWindApplication;
import com.github.microwind.userdemo.controller.AuthController;
import com.github.microwind.userdemo.controller.StudentController;
import com.github.microwind.userdemo.controller.ClassController;

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
            System.out.println("========== 学生信息管理系统 (Web模式) ==========");
            TomcatServer server = new TomcatServer(8080);
            server.start(UserDemoApplication.class.getName());
        } catch (Exception e) {
            System.err.println("启动Web服务器失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 控制台模式：直接运行测试
     */
    private static void runConsoleMode() {
        System.out.println("========== 学生信息管理系统 (控制台模式) ==========");
        System.out.println("启动SpringWind应用...\n");

        // 启动SpringWind应用，传入配置类（这里用当前类作为配置类）
        SpringWindApplication app = SpringWindApplication.run(UserDemoApplication.class);

        System.out.println("SpringWind容器启动成功！\n");

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

        System.out.println("========== 演示完成 ==========");

        // 关闭应用
        app.close();
    }
}
