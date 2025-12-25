package com.microwind.knife.application.config;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ErrorReportValve;

import java.io.IOException;

public class JsonErrorReportValve extends ErrorReportValve {

    @Override
    protected void report(Request request, Response response, Throwable throwable) {
        if (!response.isCommitted()) {
            try {
                // 强制设置响应头
                response.setContentType("application/json;charset=UTF-8");
                int status = response.getStatus();
                String reason = (throwable != null) ? throwable.getMessage() : "Invalid request";
                // 构造你想要的 JSON 字符串
                String jsonResponse = String.format(
                        "{\"code\": %d, \"title\": \"Bad Request\", \"detail\": \" %s \"}",
                        status,
                        reason
                );

                response.getWriter().write(jsonResponse);
                response.finishResponse();
            } catch (IOException e) {
                System.out.println("JsonErrorReportValve error:" + e.getMessage());
            }
        }
    }
}