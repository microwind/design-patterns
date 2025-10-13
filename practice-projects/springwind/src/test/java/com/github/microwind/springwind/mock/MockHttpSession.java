package com.github.microwind.springwind.mock;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 简易的 HttpSession 模拟实现，用于单元测试。
 * 兼容 Jakarta Servlet 6（Tomcat 11+）
 */
public class MockHttpSession implements HttpSession {

    private final String id = UUID.randomUUID().toString();
    private final long creationTime = System.currentTimeMillis();
    private long lastAccessedTime = creationTime;
    private final Map<String, Object> attributes = new HashMap<>();
    private int maxInactiveInterval = 1800; // 30分钟
    private boolean invalidated = false;

    @Override
    public long getCreationTime() {
        checkValid();
        return creationTime;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getLastAccessedTime() {
        checkValid();
        return lastAccessedTime;
    }

    @Override
    public ServletContext getServletContext() {
        return new MockServletContext();
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        this.maxInactiveInterval = interval;
    }

    @Override
    public int getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    @Override
    public Object getAttribute(String name) {
        checkValid();
        lastAccessedTime = System.currentTimeMillis();
        return attributes.get(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        checkValid();
        return Collections.enumeration(attributes.keySet());
    }

    @Override
    public void setAttribute(String name, Object value) {
        checkValid();
        lastAccessedTime = System.currentTimeMillis();
        if (value == null) {
            attributes.remove(name);
        } else {
            attributes.put(name, value);
        }
    }

    @Override
    public void removeAttribute(String name) {
        checkValid();
        attributes.remove(name);
    }

    @Override
    public void invalidate() {
        checkValid();
        attributes.clear();
        invalidated = true;
    }

    @Override
    public boolean isNew() {
        checkValid();
        return false;
    }

    /**
     * Jakarta Servlet 6+ 中的新方法，用于刷新 session ID。
     */
    public String changeSessionId() {
        return id; // 模拟行为：不改变 ID
    }

    private void checkValid() {
        if (invalidated) {
            throw new IllegalStateException("Session has been invalidated");
        }
    }
}
