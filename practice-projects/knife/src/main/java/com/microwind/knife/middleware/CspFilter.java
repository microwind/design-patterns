package com.microwind.knife.middleware;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class CspFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse res = (HttpServletResponse) response;

//        res.setHeader("Content-Security-Policy",
//                "default-src 'self'; connect-src 'self' http://localhost:8080");
        res.setHeader("Content-Security-Policy", "");
        chain.doFilter(request, response);
    }
}
