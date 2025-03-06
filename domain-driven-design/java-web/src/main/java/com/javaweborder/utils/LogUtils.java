package com.javaweborder.utils;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.*;

public class LogUtils {
    private static final Logger logger = Logger.getLogger(LogUtils.class.getName());

    static {
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter() {
            @Override
            public String format(LogRecord record) {
                return record.getMessage() + "\n";
            }
        });
        logger.setUseParentHandlers(false);
        logger.addHandler(handler);
    }

    // 记录 INFO 级别日志
    public static void logInfo(String message, Object... args) {
        logger.log(Level.INFO, String.format(message, args));
    }

    // 记录 ERROR 级别日志
    public static void logError(String message, Throwable throwable) {
        logger.log(Level.SEVERE, message, throwable);
    }

    // 记录请求日志
    public static void logRequest(HttpServletRequest request, Instant start) {
        Duration duration = Duration.between(start, Instant.now());
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String logMessage = String.format("%s %s %s took %d ms",
                timestamp,
                request.getMethod(),
                request.getRequestURI(),
                duration.toMillis());
        logInfo(logMessage);
    }
}