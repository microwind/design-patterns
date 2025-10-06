package com.github.microwind.springwind.mock;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MockHttpSession implements HttpSession {
    private final String id = UUID.randomUUID().toString();
    private final long creationTime = System.currentTimeMillis();
    private final Map<String, Object> attributes = new HashMap<>();
    private int maxInactiveInterval = 1800; // 30 minutes
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
        return System.currentTimeMillis();
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
    public HttpSessionContext getSessionContext() {
        return null; // Deprecated
    }

    @Override
    public Object getAttribute(String name) {
        checkValid();
        return attributes.get(name);
    }

    @Override
    public Object getValue(String name) {
        return getAttribute(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        checkValid();
        return java.util.Collections.enumeration(attributes.keySet());
    }

    @Override
    public String[] getValueNames() {
        checkValid();
        return attributes.keySet().toArray(new String[0]);
    }

    @Override
    public void setAttribute(String name, Object value) {
        checkValid();
        attributes.put(name, value);
    }

    @Override
    public void putValue(String name, Object value) {
        setAttribute(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        checkValid();
        attributes.remove(name);
    }

    @Override
    public void removeValue(String name) {
        removeAttribute(name);
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

    public String changeSessionId() {
        // 在实际应用中应该生成新的ID，这里简化返回原ID
        return id;
    }

    private void checkValid() {
        if (invalidated) {
            throw new IllegalStateException("Session has been invalidated");
        }
    }
}