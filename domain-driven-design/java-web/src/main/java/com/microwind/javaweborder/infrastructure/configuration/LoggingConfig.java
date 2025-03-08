package com.microwind.javaweborder.infrastructure.configuration;

import java.io.File;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggingConfig {

    // 日志文件路径和日志级别
    private String file = "logs/app.log"; // 默认日志文件路径
    private String level = "INFO";        // 默认日志级别

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    // 初始化日志配置
    public void init() {
        try {
            // 创建日志目录
            if (file != null) {
                File logFile = new File(file);
                File logDir = logFile.getParentFile();
                if (logDir != null && !logDir.exists()) {
                    logDir.mkdirs(); // 创建日志目录
                }
            }

            // 获取根日志记录器
            Logger rootLogger = Logger.getLogger("");

            // 设置日志级别
            if (level != null) {
                rootLogger.setLevel(Level.parse(level.toUpperCase()));
            } else {
                rootLogger.setLevel(Level.INFO); // 默认日志级别
            }

            // 配置文件日志处理器
            if (file != null) {
                FileHandler fileHandler = new FileHandler(file, true); // true 表示追加模式
                fileHandler.setFormatter(new SimpleFormatter()); // 使用简单格式
                rootLogger.addHandler(fileHandler);
            }
        } catch (Exception e) {
            System.err.println("Failed to initialize logging: " + e.getMessage());
        }
    }
}