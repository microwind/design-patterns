package com.github.microwind.springwind.mock;

import jakarta.servlet.*;
import java.io.IOException;

/**
 * 模拟 RequestDispatcher 的转发行为
 */
public class MockRequestDispatcher implements RequestDispatcher {

    private final String path;

    public MockRequestDispatcher(String path) {
        this.path = path;
    }

    @Override
    public void forward(ServletRequest request, ServletResponse response)
            throws ServletException, IOException {
        if (response instanceof MockHttpServletResponse) {
            ((MockHttpServletResponse) response).setForwardedUrl(path);
        }
    }

    @Override
    public void include(ServletRequest request, ServletResponse response)
            throws ServletException, IOException {
        // 可选：不实现
    }

    public String getPath() {
        return path;
    }
}
