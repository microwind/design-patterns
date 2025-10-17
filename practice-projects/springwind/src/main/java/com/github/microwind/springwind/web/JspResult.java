package com.github.microwind.springwind.web;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JSP 视图结果
 */
public class JspResult implements ViewResult {
    private final String viewName;
    private final String viewPrefix;
    private final String viewSuffix;

    public JspResult(String viewName) {
        this(viewName, "/WEB-INF/views/", ".jsp");
    }

    public JspResult(String viewName, String viewPrefix, String viewSuffix) {
        this.viewName = viewName;
        this.viewPrefix = viewPrefix;
        this.viewSuffix = viewSuffix;
    }

    @Override
    public void render(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String jspPath;
        if (viewName.contains("/WEB-INF/")) {
            jspPath = viewName;
        } else if (viewName.endsWith(".jsp")) {
            jspPath = viewPrefix + viewName;
        } else {
            jspPath = viewPrefix + viewName + viewSuffix;
        }

        request.setAttribute("forwardPath", jspPath);
        RequestDispatcher dispatcher = request.getRequestDispatcher(jspPath);

        if (dispatcher != null) {
            response.setStatus(200);
            dispatcher.forward(request, response);
        } else {
            response.setStatus(404);
            response.getWriter().write("View not found: " + jspPath);
        }
    }
}
