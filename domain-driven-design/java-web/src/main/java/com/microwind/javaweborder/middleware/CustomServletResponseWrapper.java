package com.microwind.javaweborder.middleware;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

// 创建ServletResponseWrapper 用于捕获响应的状态码和输出内容，并将输出内容到CharArrayWriter中
public class CustomServletResponseWrapper extends HttpServletResponseWrapper {
    private int httpStatus = SC_OK;
    private final CharArrayWriter charArray = new CharArrayWriter();
    private PrintWriter writer;

    public CustomServletResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public void sendError(int sc) throws IOException {
        this.httpStatus = sc;
        setStatus(sc);
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        this.httpStatus = sc;
        setStatus(sc);
    }

    @Override
    public void setStatus(int sc) {
        super.setStatus(sc);
        this.httpStatus = sc;
    }

    @Override
    public int getStatus() {
        return httpStatus;
    }

    @Override
    public PrintWriter getWriter() {
        if (writer == null) {
            writer = new PrintWriter(charArray);
        }
        return writer;
    }

    public String getOutput() {
        return charArray.toString();
    }
}