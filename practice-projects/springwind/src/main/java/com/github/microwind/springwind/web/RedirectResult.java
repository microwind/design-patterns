package com.github.microwind.springwind.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 重定向结果
 */
public class RedirectResult implements ViewResult {
    private final String url;

    public RedirectResult(String url) {
        this.url = url;
    }

    @Override
    public void render(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setStatus(302);
        response.sendRedirect(url);
    }
}
