package com.microwind.javaweborder.interfaces.routes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@FunctionalInterface
public interface RouteHandler {
    void handle(HttpServletRequest request, HttpServletResponse response) throws Exception;
}