package com.github.microwind.springwind.mock;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.security.Principal;
import java.util.*;

/**
 * 简化版 MockHttpServletRequest，用于单元测试。
 * 不依赖 Spring，仅支持常用属性（方法、路径、参数、Header）。
 */
public class MockHttpServletRequest implements HttpServletRequest {

    private final String method;
    private final String requestURI;
    private String servletPath;
    private final Map<String, Object> attributes = new HashMap<>();
    private final Map<String, String[]> parameters = new HashMap<>();
    private final Map<String, String> headers = new HashMap<>();
    private final MockServletContext servletContext = new MockServletContext();
    private MockHttpSession session;
    private String characterEncoding = "UTF-8";
    private String contentType;
    private String contextPath = "";

    public MockHttpServletRequest(String method, String requestURI) {
        this.method = method;
        this.requestURI = requestURI;
        this.servletPath = requestURI; // 默认servletPath与requestURI相同
    }

    public void setServletPath(String servletPath) {
        this.servletPath = servletPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public void addParameter(String name, String value) {
        parameters.put(name, new String[]{value});
    }

    public void addParameter(String name, String[] values) {
        parameters.put(name, values);
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    @Override
    public String getServletPath() {
        return servletPath;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        // 修复：返回一个简单的RequestDispatcher实现
        return new RequestDispatcher() {
            @Override
            public void forward(ServletRequest request, ServletResponse response) throws ServletException, IOException {
                if (response instanceof MockHttpServletResponse) {
                    ((MockHttpServletResponse) response).setForwardedUrl(path);
                }
            }

            @Override
            public void include(ServletRequest request, ServletResponse response) throws ServletException, IOException {
                // 简化实现，记录包含的路径
                if (response instanceof MockHttpServletResponse) {
                    // 可以在MockHttpServletResponse中添加setIncludedUrl方法来记录包含的路径
                }
            }
        };
    }

    @Override
    public String getMethod() { return method; }

    @Override
    public String getRequestURI() { return requestURI; }

    @Override
    public String getContextPath() { return contextPath; }

    @Override
    public ServletContext getServletContext() { return servletContext; }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        throw new UnsupportedOperationException("Async not supported in mock");
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        throw new UnsupportedOperationException("Async not supported in mock");
    }

    @Override
    public boolean isAsyncStarted() {
        return false;
    }

    @Override
    public boolean isAsyncSupported() {
        return false;
    }

    @Override
    public AsyncContext getAsyncContext() {
        return null;
    }

    @Override
    public DispatcherType getDispatcherType() {
        return DispatcherType.REQUEST;
    }

    @Override
    public String getRequestId() {
        return "";
    }

    @Override
    public String getProtocolRequestId() {
        return "";
    }

    @Override
    public ServletConnection getServletConnection() {
        return null;
    }

    // --- 简化的属性实现 ---
    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(attributes.keySet());
    }

    @Override
    public void setAttribute(String name, Object o) {
        attributes.put(name, o);
    }

    @Override
    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    // --- 参数 ---
    @Override
    public String getParameter(String name) {
        String[] values = parameters.get(name);
        return (values != null && values.length > 0) ? values[0] : null;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return Collections.unmodifiableMap(parameters);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(parameters.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        return parameters.get(name);
    }

    // --- 头部信息 ---
    @Override
    public String getHeader(String name) {
        return headers.get(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        String value = headers.get(name);
        return value != null ?
                Collections.enumeration(Collections.singletonList(value)) :
                Collections.emptyEnumeration();
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return Collections.enumeration(headers.keySet());
    }

    @Override
    public int getIntHeader(String name) {
        String value = getHeader(name);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                // 忽略格式错误
            }
        }
        return -1;
    }

    @Override
    public long getDateHeader(String name) {
        // 简化实现，返回当前时间
        String value = getHeader(name);
        if (value != null) {
            return System.currentTimeMillis();
        }
        return -1;
    }

    // --- 会话管理 ---
    @Override
    public HttpSession getSession(boolean create) {
        if (session == null && create) {
            session = new MockHttpSession();
        }
        return session;
    }

    @Override
    public HttpSession getSession() {
        return getSession(true);
    }

    @Override
    public String changeSessionId() {
        if (session != null) {
            return session.changeSessionId();
        }
        return null;
    }

    // --- 基本请求信息 ---
    @Override
    public String getProtocol() {
        return "HTTP/1.1";
    }

    @Override
    public String getScheme() {
        return "http";
    }

    @Override
    public String getServerName() {
        return "localhost";
    }

    @Override
    public int getServerPort() {
        return 8080;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        // 返回一个空的BufferedReader
        return new BufferedReader(new StringReader(""));
    }

    @Override
    public String getRemoteAddr() {
        return "127.0.0.1";
    }

    @Override
    public String getRemoteHost() {
        return "localhost";
    }

    @Override
    public Locale getLocale() {
        return Locale.getDefault();
    }

    @Override
    public Enumeration<Locale> getLocales() {
        return Collections.enumeration(Collections.singleton(Locale.getDefault()));
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public int getRemotePort() {
        return 0;
    }

    @Override
    public String getLocalName() {
        return "localhost";
    }

    @Override
    public String getLocalAddr() {
        return "127.0.0.1";
    }

    @Override
    public int getLocalPort() {
        return 8080;
    }

    // --- 认证与安全 ---
    @Override
    public String getAuthType() {
        return null;
    }

    @Override
    public Cookie[] getCookies() {
        return new Cookie[0];
    }

    @Override
    public String getPathInfo() {
        return null;
    }

    @Override
    public String getPathTranslated() {
        return null;
    }

    @Override
    public String getQueryString() {
        return null;
    }

    @Override
    public String getRemoteUser() {
        return null;
    }

    @Override
    public boolean isUserInRole(String role) {
        return false;
    }

    @Override
    public Principal getUserPrincipal() {
        return null;
    }

    @Override
    public String getRequestedSessionId() {
        return session != null ? session.getId() : null;
    }

    @Override
    public StringBuffer getRequestURL() {
        return new StringBuffer("http://localhost:8080" + requestURI);
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return session != null;
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return false;
    }

    @Override
    public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
        return false;
    }

    @Override
    public void login(String username, String password) throws ServletException {
        // 简化实现
    }

    @Override
    public void logout() throws ServletException {
        // 简化实现
    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        return Collections.emptyList();
    }

    @Override
    public Part getPart(String name) throws IOException, ServletException {
        return null;
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
        throw new UnsupportedOperationException("Upgrade not supported in mock");
    }

    // --- 字符编码和输入流 ---
    @Override
    public String getCharacterEncoding() {
        return characterEncoding;
    }

    @Override
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
        this.characterEncoding = env;
    }

    @Override
    public int getContentLength() {
        return 0;
    }

    @Override
    public long getContentLengthLong() {
        return 0;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        // 返回一个空的ServletInputStream
        return new ServletInputStream() {
            @Override
            public int read() throws IOException {
                return -1; // EOF
            }

            @Override
            public boolean isFinished() {
                return true;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                // 不支持异步
            }
        };
    }
}