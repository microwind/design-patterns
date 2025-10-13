package com.github.microwind.springwind.mock;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

/**
 * 简化版 MockHttpServletResponse，用于单元测试
 * 支持：状态码、重定向URL、转发URL、响应体、Content-Type、响应头、字符编码
 */
public class MockHttpServletResponse implements HttpServletResponse {

    // 核心状态与路径存储
    private int status = 200; // 默认200 OK
    private String redirectedUrl; // 重定向路径
    private String forwardedUrl; // 转发路径

    // 响应体存储（用于获取输出内容）
    private final StringWriter bodyWriter = new StringWriter();
    private final PrintWriter writer = new PrintWriter(bodyWriter);

    // 响应头存储（支持setHeader/getHeader，解决Content-Type问题）
    private final Map<String, String> headers = new HashMap<>(); // 单值头（常用场景）
    private final Map<String, List<String>> multiValueHeaders = new LinkedHashMap<>(); // 多值头（兼容标准）

    // 内容类型与字符编码（独立存储，优先级：显式设置 > 响应头）
    private String contentType;
    private String characterEncoding = "UTF-8"; // 默认UTF-8


    // ------------------------------
    // 核心功能：响应体相关
    // ------------------------------
    /**
     * 获取响应体内容（如JSON字符串、文本内容）
     */
    public String getContentAsString() {
        writer.flush(); // 确保缓冲区内容已写入
        return bodyWriter.toString();
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return writer;
    }

    @Override
    public void flushBuffer() throws IOException {
        writer.flush(); // 强制刷新响应体缓冲区
    }


    // ------------------------------
    // 核心功能：状态码、重定向、转发
    // ------------------------------
    @Override
    public void setStatus(int sc) {
        this.status = sc;
    }

    @Override
    public int getStatus() {
        return this.status;
    }

    @Override
    public void sendRedirect(String location) throws IOException {
        this.redirectedUrl = location;
        this.status = 302; // 重定向默认302状态码
    }

    @Override
    public void sendRedirect(String s, int i, boolean b) throws IOException {

    }

    /**
     * 获取重定向路径
     */
    public String getRedirectedUrl() {
        return this.redirectedUrl;
    }

    /**
     * 设置转发路径（供DispatcherServlet调用，记录转发目标）
     */
    public void setForwardedUrl(String path) {
        this.forwardedUrl = path;
    }

    /**
     * 获取转发路径
     */
    public String getForwardedUrl() {
        return this.forwardedUrl;
    }


    // ------------------------------
    // 核心功能：Content-Type 与 字符编码
    // ------------------------------
    @Override
    public void setContentType(String type) {
        this.contentType = type; // 存储显式设置的Content-Type
        // 同时同步到响应头（符合Servlet标准：setContentType会更新Content-Type头）
        if (type != null) {
            setHeader("Content-Type", type);
        }
    }

    @Override
    public String getContentType() {
        // 优先级：1. 显式setContentType设置的值 2. 响应头中的Content-Type 3. 默认text/html
        if (this.contentType != null) {
            return this.contentType;
        }
        String headerContentType = getHeader("Content-Type");
        return headerContentType != null ? headerContentType : "text/html";
    }

    @Override
    public void setCharacterEncoding(String charset) {
        this.characterEncoding = charset;
        // 同步更新Content-Type头中的charset（如：application/json;charset=UTF-8）
        String currentContentType = getContentType();
        if (currentContentType != null && charset != null) {
            if (currentContentType.contains("charset=")) {
                currentContentType = currentContentType.replaceAll("charset=[^;]+", "charset=" + charset);
            } else {
                currentContentType += ";charset=" + charset;
            }
            setContentType(currentContentType); // 重新设置Content-Type，同步头信息
        }
    }

    @Override
    public String getCharacterEncoding() {
        return this.characterEncoding;
    }


    // ------------------------------
    // 核心功能：响应头处理（支持setHeader/getHeader）
    // ------------------------------
    @Override
    public void setHeader(String name, String value) {
        if (name == null || value == null) {
            return;
        }
        headers.put(name, value); // 单值头覆盖
        // 同步多值头（移除旧值，添加新值）
        multiValueHeaders.remove(name);
        List<String> values = new ArrayList<>();
        values.add(value);
        multiValueHeaders.put(name, values);
    }

    @Override
    public void addHeader(String name, String value) {
        if (name == null || value == null) {
            return;
        }
        // 多值头追加（不覆盖旧值）
        multiValueHeaders.computeIfAbsent(name, k -> new ArrayList<>()).add(value);
        // 单值头保留最后一个值（兼容常用场景）
        headers.put(name, value);
    }

    @Override
    public String getHeader(String name) {
        if (name == null) {
            return null;
        }
        return headers.get(name); // 返回单值头的最后一个值
    }

    @Override
    public Collection<String> getHeaderNames() {
        return new ArrayList<>(headers.keySet()); // 返回所有头名称
    }

    @Override
    public Collection<String> getHeaders(String name) {
        if (name == null) {
            return Collections.emptyList();
        }
        return multiValueHeaders.getOrDefault(name, Collections.emptyList()); // 返回多值头
    }


    // ------------------------------
    // 其他标准方法实现（兼容Servlet接口，避免空指针）
    // ------------------------------
    @Override
    public void addCookie(Cookie cookie) {
        // 暂不实现（测试未用到）
    }

    @Override
    public boolean containsHeader(String name) {
        return headers.containsKey(name);
    }

    @Override
    public String encodeURL(String url) {
        return url; // 简化实现：不处理URL编码（测试场景无需）
    }

    @Override
    public String encodeRedirectURL(String url) {
        return url;
    }

    @Override
    public void sendError(int sc) throws IOException {
        this.status = sc;
        this.writer.write("Error " + sc); // 写入错误信息到响应体
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        this.status = sc;
        this.writer.write("Error " + sc + ": " + msg); // 写入错误信息
    }

    @Override
    public void setDateHeader(String name, long date) {
        setHeader(name, new Date(date).toString()); // 简化：日期转字符串存储
    }

    @Override
    public void addDateHeader(String name, long date) {
        addHeader(name, new Date(date).toString());
    }

    @Override
    public void setIntHeader(String name, int value) {
        setHeader(name, String.valueOf(value)); // 整数转字符串存储
    }

    @Override
    public void addIntHeader(String name, int value) {
        addHeader(name, String.valueOf(value));
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException("Mock不支持ServletOutputStream，建议用getWriter()");
    }

    @Override
    public void setContentLength(int len) {
        setHeader("Content-Length", String.valueOf(len)); // 同步到响应头
    }

    @Override
    public void setContentLengthLong(long len) {
        setHeader("Content-Length", String.valueOf(len));
    }

    @Override
    public void setBufferSize(int size) {
        // 暂不实现（测试未用到缓冲区大小控制）
    }

    @Override
    public int getBufferSize() {
        return 8192; // 模拟默认缓冲区大小
    }

    @Override
    public void resetBuffer() {
        // 修复：清空响应体缓冲区，而不是反转
        bodyWriter.getBuffer().setLength(0);
    }

    @Override
    public boolean isCommitted() {
        return false; // 简化：默认未提交（测试无需复杂提交逻辑）
    }

    @Override
    public void reset() {
        // 重置所有状态：状态码、响应体、头信息、路径
        this.status = 200;
        this.redirectedUrl = null;
        this.forwardedUrl = null;
        this.contentType = null;
        this.characterEncoding = "UTF-8";
        headers.clear();
        multiValueHeaders.clear();
        // 修复：清空响应体缓冲区
        bodyWriter.getBuffer().setLength(0);
    }

    @Override
    public void setLocale(Locale loc) {
        setHeader("Content-Language", loc.getLanguage()); // 同步到响应头
    }

    @Override
    public Locale getLocale() {
        String language = getHeader("Content-Language");
        return language != null ? new Locale(language) : Locale.getDefault();
    }
}