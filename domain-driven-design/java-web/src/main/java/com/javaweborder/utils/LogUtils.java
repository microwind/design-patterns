package com.javaweborder.utils;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogUtils {
    private static final Logger logger = Logger.getLogger(LogUtils.class.getName());

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
        String logMessage = String.format("%s %s took %d ms",
                request.getMethod(),
                request.getRequestURI(),
                duration.toMillis());
        logInfo(logMessage);
    }
}