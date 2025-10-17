package com.github.microwind.springwind.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * HTML 响应结果
 */
public class HtmlResult implements ViewResult {
    private final String content;
    private String contentType = "text/html;charset=UTF-8";
    private String encoding = "UTF-8";

    public HtmlResult(String content) {
        this.content = content;
    }

    public HtmlResult contentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public HtmlResult encoding(String encoding) {
        this.encoding = encoding;
        return this;
    }

    @Override
    public void render(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setCharacterEncoding(encoding);
        response.setContentType(contentType);
        PrintWriter writer = response.getWriter();
        writer.write(content);
        writer.flush();
        response.flushBuffer();
    }
}
